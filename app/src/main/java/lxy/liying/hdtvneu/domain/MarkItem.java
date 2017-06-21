package lxy.liying.hdtvneu.domain;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/8/24 13:48
 * 版本：1.0
 * 描述：收藏的视频项目
 * 备注：
 * =======================================================
 */
public class MarkItem {
    /** 视频ID（添加收藏时的时间戳） */
    private long markId;
    /** 收藏的视频的名称 */
    private String name;
    /** 收藏的视频的封面 */
    private String coverPath;
    /** 收藏的视频所在分组 */
    private MarkGroup group;
    /** 视频播放地址 */
    private String path;
    /** 本地视频所在文件夹 */
    private String folder;
    /** 上次的播放位置 */
    private long position;

    public static final String MARK_ID = "markId";
    public static final String NAME = "name";
    public static final String COVER_PATH = "coverPath";
    public static final String GROUP_INDEX = "groupIndex";
    public static final String PATH = "path";
    public static final String FOLDER = "folder";
    public static final String POSITION = "position";

    public MarkItem(long markId, String name, String coverPath, MarkGroup group, String path,
                    String folder, long position) {
        this.markId = markId;
        this.name = name;
        this.coverPath = coverPath;
        this.group = group;
        this.path = path;
        this.folder = folder;
        this.position = position;
    }

    public MarkItem() {

    }

    public long getMarkId() {
        return markId;
    }

    public void setMarkId(long markId) {
        this.markId = markId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MarkGroup getGroup() {
        return group;
    }

    public void setGroup(MarkGroup group) {
        this.group = group;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

}
