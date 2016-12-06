package youga.viewapplication.model;

import android.support.annotation.NonNull;

/**
 * Created by YougaKing on 2016/11/24.
 */

public class PieData {
    // 用户关心数据
    private String name;        // 名字
    private float value;        // 数值
    private float percentage;   // 百分比

    // 非用户关心数据
    private int color = 0;      // 颜色
    private float angle = 0;    // 角度

    public PieData(@NonNull String name, @NonNull float value) {
        this.name = name;
        this.value = value;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public String getName() {
        return name;
    }

    public float getValue() {
        return value;
    }

    public float getPercentage() {
        return percentage;
    }

    public int getColor() {
        return color;
    }

    public float getAngle() {
        return angle;
    }
}
