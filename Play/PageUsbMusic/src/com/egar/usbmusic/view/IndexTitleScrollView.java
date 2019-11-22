package com.egar.usbmusic.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 自定义的首字母滑动条
 *
 * @author yangbofeng
 */
public class IndexTitleScrollView extends View {
    public static final String TAG = "indexview";
    private int height;
    private int width;
    private final char[] chars = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
            'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
            'W', 'X', 'Y', 'Z', '#'};// 5+13+9
    private int mIndex = 0;
    private OnIndexListener mChanged;
    Paint paint;
    private final int textSize = 14;
    private float iHeight;
    private boolean isScroll = false;


    public IndexTitleScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub

        Log.i(TAG, "new IndexTitleScrollView ");
        paint = new Paint();
        paint.setTextAlign(Paint.Align.CENTER); // 文字对齐，CENTER表示绘制的文字居中显示
        paint.setTextSize(textSize);
        paint.setStrokeWidth(0.7f); //设置描边宽度
        paint.setAntiAlias(true);//抗锯齿 - [避免绘制的视图边缘有模糊毛边，呈锯齿形]
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(Color.BLACK);

    }

    /**
     * 光标位置，(0~26)
     *
     * @param index
     */
    public void setIndex(int index) {
        if (index >= 0 && index <= 26) {
            this.mIndex = index;
            invalidate();
        }

    }

    /**
     * 得到当前的位置
     *
     * @return
     */
    public int getCurrentIndex() {
        return mIndex;
    }

    /**
     * 得到当前的字符
     *
     * @return
     */
    public char getCurrentChar() {
        return chars[mIndex];
    }

    /**
     * 光标所指的字符，(A ~Z,#)
     *
     * @param c
     */
    public void setIndex(char c) {
        setIndex(findCharIndex(c));
    }

    private int findCharIndex(char c) {
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == c) {
                return i;
            }
        }
        return -1;
    }

    public void registIndexChanged(OnIndexListener mIndexChanged) {
        mChanged = mIndexChanged;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        if (height == 0)
            height = getHeight();
        if (width == 0)
            width = getWidth();

        Log.i(TAG, "width->" + getWidth());
        // Log.i(TAG, "height->" + getHeight());
        if (iHeight == 0) {
            iHeight = ((float) height / (float) (chars.length));
            int[] location = new int[2];
            getLocationOnScreen(location);
            view_x = location[0];
            view_y = location[1];
            view_x = width / 2;
            Log.i(TAG, "view_x->" + view_x);
            Log.i(TAG, "view_y->" + view_y);
        }
        // Log.i(TAG, "iHeight->" + iHeight);
        for (int i = 0; i < chars.length; i++) {

            paint.setColor(Color.TRANSPARENT);
            //canvas.drawRect(0, (float) i * iHeight, getRight(), iHeight * (float) (i + 1), paint);
            paint.setTextSize(textSize * 0.93f);
            if (i == mIndex) {
                paint.setColor(Color.RED);
            } else {
                paint.setColor(Color.WHITE);
            }
            // cos30 = 0.866 cos60 = 0.5
            if (isCicle) {
                if (i == mIndex - 2) {
                    canvas.drawText(chars[i] + "", view_x + 20, (float) i * iHeight+12, paint);
                } else if (i == mIndex - 1) {
                    canvas.drawText(chars[i] + "", view_x + 30, (float) i * iHeight+12, paint);
                } else if (i == mIndex) {
                    canvas.drawText(chars[i] + "", view_x + 40, (float) i * iHeight+12, paint);
                } else if (i == mIndex + 1) {
                    canvas.drawText(chars[i] + "", view_x + 30, (float) i * iHeight+12, paint);
                } else if (i == mIndex + 2) {
                    canvas.drawText(chars[i] + "", view_x + 20, (float) i * iHeight+12, paint);
                } else {
                    canvas.drawText(chars[i] + "", view_x, (float) i * iHeight+12, paint);
                }
            } else {
                canvas.drawText(chars[i] + "", view_x, (float) i * iHeight+12, paint);
            }

            // if(i == 0){
            // Log.i(TAG,"y -> "+(getTop()+(float) i * iHeight));
            // }
        }

    }

    private float downX;
    private float downY;
    private boolean isCicle = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 记录按下的坐标
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = event.getRawX();
            downY = event.getRawY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // x，y移动的距离小于10就出发点击事件
            if (Math.abs(downX - event.getRawX()) < 10
                    && Math.abs(downY - event.getRawY()) < 10) {
                Log.i(TAG, "onclick here");
                int index = (int) ((event.getRawY() - view_y) / iHeight);
                Log.i(TAG, "onclick here" + index);
                setIndex(index);
                onClickChar(index);

            } else {
                isCicle = false;
                invalidate();
                onStopChanged(mIndex);
            }
            invalidate();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            isCicle = true;
            float x = event.getRawX();
            float y = event.getRawY();
            // Log.i(TAG, "; move x ->" + x + "; move y ->" +
            // y);

            if (x > getRight()) {
                Log.i(TAG, "IS OVER ");
                // do nothing
            } else {
                refreshIndex(x, y);
            }
        }
        return true;
    }

    private int view_x;
    private int view_y;

    // 刷新位置值，同时刷新视图
    private void refreshIndex(float x, float y) {
        // TODO Auto-generated method stub

        if (view_y + mIndex * iHeight < y
                && y < (mIndex + 1) * iHeight + view_y) {
            // index不需要改变，
            // do nothing
            // Log.i(TAG, "no need change");
        } else {
            float i = (y - view_y) / iHeight;
            // Log.i(TAG, "index will is  " + i);
            if (i < 0 || (int) i > chars.length - 1) {

            } else {
                mIndex = (int) i;
                invalidate();
                onIndexChanged(mIndex);
            }
        }
    }

    /**
     * 监听首字母定位事件
     */
    public interface OnIndexListener {
        public void onIndexChanged(int index, char c);

        public void onStopChanged(int index, char c);

        public void onClickChar(int index, char c);

    }


    private void onIndexChanged(int index) {
        isScroll = true;
        if (mChanged != null)
            mChanged.onIndexChanged(index, chars[index]);
    }

    private void onStopChanged(int index) {
        isScroll = false;
        if (mChanged != null)
            mChanged.onStopChanged(index, chars[index]);

    }

    private void onClickChar(int index) {
        isScroll = false;
        if (mChanged != null)
            mChanged.onClickChar(index, chars[index]);
    }

    public boolean isScroll() {
        return isScroll;
    }

}
