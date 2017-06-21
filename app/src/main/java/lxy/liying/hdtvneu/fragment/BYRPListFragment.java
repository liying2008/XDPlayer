package lxy.liying.hdtvneu.fragment;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.adapter.BY_ProgramsAdapter;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.Program;
import lxy.liying.hdtvneu.service.MainBinder;
import lxy.liying.hdtvneu.service.MainService;
import lxy.liying.hdtvneu.service.callback.On_BY_GetAllProgramsCallback;
import lxy.liying.hdtvneu.utils.Constants;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/8/14 14:52
 * 版本：1.0
 * 描述：北邮人IPTV频道列表
 * 备注：
 * =======================================================
 */
public class BYRPListFragment extends BaseFragment implements On_BY_GetAllProgramsCallback, SwipeRefreshLayout.OnRefreshListener {
    /** 数据 */
    private BY_ProgramsAdapter programsAdapter;
    /** 数据源  */
    private MainBinder mainBinder;
    private Button btnSearch;
    private EditText etSearchProgram;
    /** 节目列表  */
    private List<Program> mProgramsList;
    /** 搜索到的节目列表  */
    private List<Program> mSearchList = new ArrayList<>(150);
    /** 是否处于搜索状态 */
    private static boolean isSearching = false;
    /** 节目列表视图 */
    private RecyclerView rvPrograms;
    private TextView tvTotal;
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        setContentView(R.layout.fragment_byr_plist);
        rvPrograms = (RecyclerView) findViewById(R.id.rvPrograms);
        tvTotal = (TextView) findViewById(R.id.tvTotal);
        etSearchProgram = (EditText) findViewById(R.id.etSearchProgram);
        SearchTextWatcher watcher = new SearchTextWatcher();
        etSearchProgram.addTextChangedListener(watcher);    // 监听EditText内容变化
        // 搜索按钮
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvTotal.setVisibility(View.GONE);
                etSearchProgram.setVisibility(View.VISIBLE);
                btnSearch.setVisibility(View.GONE);
                isSearching = true;
                // 弹出输入法面板
                etSearchProgram.requestFocus();
                InputMethodManager imm = (InputMethodManager) etSearchProgram.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("正在获取节目列表...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        programsAdapter = new BY_ProgramsAdapter(getActivity());
        rvPrograms.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(Constants.SWIPE_REFRESH_COLOR_SCHEME);
        Intent intent = new Intent(getActivity(), MainService.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        if (mainBinder != null) {
            // 初始化数据
            initData();
            progressDialog.dismiss();
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mainBinder = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mainBinder = (MainBinder) service;
            mainBinder.by_getAllPrograms(BYRPListFragment.this);
            setDataSource(mainBinder);
        }
    };

    /**
     * 数据初始化
     */
    private void initData() {
        rvPrograms.setAdapter(programsAdapter);
    }

    public void setDataSource(MainBinder mainBinder) {
        this.mainBinder = mainBinder;
    }

    @Override
    protected void onFragmentStartLazy() {
        super.onFragmentStartLazy();
        App.getInstance().currentFragment = this;
    }

    @Override
    public void onGetAllPrograms(List<Program> programsList) {
        if (programsList.size() > 0) {
            btnSearch.setVisibility(View.VISIBLE); // 设置搜索按钮可见
        }
        mProgramsList = programsList;
        App.programsList = mProgramsList;
        App.programType = "3";
        programsAdapter.setData(programsList, null);
        tvTotal.setText("当前共有" + programsList.size() + "个节目");
        initData();
        swipeRefreshLayout.setRefreshing(false);
        progressDialog.dismiss();
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        getActivity().unbindService(serviceConnection);
    }

    @Override
    public void onRefresh() {
        if (!isSearching) {
            btnSearch.setVisibility(View.GONE);
            mainBinder.by_getAllPrograms(this);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    /***
     * 点击back键退出搜索状态
     */
    public boolean onBackPress() {
        if (isSearching) {
            // 退出搜索状态
            btnSearch.setVisibility(View.VISIBLE);
            etSearchProgram.setText("");
            tvTotal.setVisibility(View.VISIBLE);
            etSearchProgram.setVisibility(View.GONE);
            tvTotal.setText("当前共有" + mProgramsList.size() + "个节目");
            isSearching = false;
            programsAdapter.setData(mProgramsList, null);
            programsAdapter.notifyDataSetChanged();
            return true;
        }
        return false;
    }

    private class SearchTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String searchText2 = s.toString().toUpperCase();
            mSearchList.clear();
            if (mProgramsList != null && mProgramsList.size() > 0) {
                for (int i = 0; i < mProgramsList.size(); i++) {
                    if (mProgramsList.get(i).getName().contains(searchText2)) {
                        mSearchList.add(mProgramsList.get(i));
                    }
                }
            }
            programsAdapter.setData(mSearchList, searchText2);
            programsAdapter.notifyDataSetChanged();
            tvTotal.setText("共有" + mSearchList.size() + "个搜索结果");
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
