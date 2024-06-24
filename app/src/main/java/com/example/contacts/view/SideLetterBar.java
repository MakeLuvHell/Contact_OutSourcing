package com.example.contacts.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.contacts.R;

public class SideLetterBar extends View {
    // 声明一个接口变量用于字母变化时的回调
    private OnLetterChangedListener onLetterChangedListener;
    // 定义侧边栏显示的字母数组
    private final String[] letters = {"#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    // 创建一个绘制工具对象
    private final Paint paint = new Paint();
    // 声明一个TextView用于显示当前选中的字母
    private TextView overlay;
    // 记录当前选中的字母索引
    private int chosen = -1;

    // 构造方法1
    public SideLetterBar(Context context) {
        super(context);
    }

    // 构造方法2
    public SideLetterBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 构造方法3
    public SideLetterBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // 设置显示当前选中字母的TextView
    public void setOverlay(TextView overlay) {
        this.overlay = overlay;
    }

    // 绘制侧边栏字母的方法
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        int height = getHeight(); // 获取控件高度
        int width = getWidth(); // 获取控件宽度
        int singleHeight = height / letters.length; // 计算每个字母占据的高度

        for (int i = 0; i < letters.length; i++) {
            paint.setColor(Color.BLACK); // 设置画笔颜色为黑色
            paint.setAntiAlias(true); // 抗锯齿
            paint.setTextSize(30); // 设置文字大小
            if (i == chosen) {
                paint.setColor(Color.BLUE); // 选中字母变为蓝色
                paint.setFakeBoldText(true); // 设置加粗
            }
            // 计算字母绘制的X和Y坐标
            float xPos = width / 2 - paint.measureText(letters[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(letters[i], xPos, yPos, paint); // 绘制字母
            paint.reset(); // 重置画笔
        }
    }

    // 处理触摸事件的方法
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction(); // 获取触摸动作
        final float y = event.getY(); // 获取触摸点的Y坐标
        final int oldChosen = chosen; // 记录之前选中的字母索引
        final int c = (int) (y / getHeight() * letters.length); // 计算当前触摸点对应的字母索引

        switch (action) {
            case MotionEvent.ACTION_UP:
                setBackgroundColor(Color.TRANSPARENT); // 触摸结束时设置背景透明
                chosen = -1; // 重置选中字母索引
                invalidate(); // 重新绘制
                if (overlay != null) {
                    overlay.setVisibility(GONE); // 隐藏提示TextView
                }
                break;
            default:
                setBackgroundResource(R.drawable.sidebar_background); // 设置触摸时的背景
                if (oldChosen != c) {
                    if (c >= 0 && c < letters.length) {
                        if (onLetterChangedListener != null) {
                            onLetterChangedListener.onLetterChanged(letters[c]); // 调用字母变化的回调方法
                        }
                        if (overlay != null) {
                            overlay.setText(letters[c]); // 设置提示TextView的文字
                            overlay.setVisibility(VISIBLE); // 显示提示TextView
                        }
                        chosen = c; // 更新选中字母索引
                        invalidate(); // 重新绘制
                    }
                }
                break;
        }
        return true;
    }

    // 设置字母变化监听器的方法
    public void setOnLetterChangedListener(OnLetterChangedListener listener) {
        this.onLetterChangedListener = listener;
    }

    // 字母变化监听器接口
    public interface OnLetterChangedListener {
        void onLetterChanged(String letter);
    }
}
