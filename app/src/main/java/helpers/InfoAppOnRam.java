package helpers;

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by USER on 27/05/2014.
 */
public class InfoAppOnRam {
    private String packageName;
    private String appName;
    private Drawable drawableIcon;
    private Long size;
    private Integer pid;
    public View view;

    public InfoAppOnRam(String packageName, String appName, Drawable drawableIcon, Long size, Integer pid) {
        this.packageName = packageName;
        this.appName = appName;
        this.drawableIcon = drawableIcon;
        this.size = size;
        this.pid = pid;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getAppName() {
        return appName;
    }

    public Drawable getDrawableIcon() {
        return drawableIcon;
    }

    public Long getSize() {
        return size;
    }

    public Integer getPid() {
        return pid;
    }
}
