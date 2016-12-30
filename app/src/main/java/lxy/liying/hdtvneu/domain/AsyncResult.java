package lxy.liying.hdtvneu.domain;

import android.os.Bundle;

/**
 * 异步执行结果
 */
public class AsyncResult<Data> {
    private int result;
    private Data data;
    private Bundle bundle = new Bundle();

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Bundle getBundle() {
        return bundle;
    }
}
