package com.yezh.lockpattern.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.math.MathUtils;

import com.yezh.lockpattern.utils.MethUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 九宮格解鎖
 * 1、先绘制九宫格布局。
 * 2、处理触摸事件，可以连线
 * 3、给绘制点之间连线以及箭头
 */
public class LockPatternView extends View {
    private boolean mIsInit = false;

    //二维数组存放九个点对象
    private Point[][] mPoints = new Point[3][3];

    // 画笔
    private Paint mLinePaint;
    private Paint mPressedPaint;
    private Paint mErrorPaint;
    private Paint mNormalPaint;
    private Paint mArrowPaint;

    // 颜色
    private int mOuterPressedColor = 0xff8cbad8;
    private int mInnerPressedColor = 0xff0596f6;
    private int mOuterNormalColor = 0xffd9d9d9;
    private int mInnerNormalColor = 0xff929292;
    private int mOuterErrorColor = 0xff901032;
    private int mInnerErrorColor = 0xffea0945;

    //外圆半径大小
    private int mDotRadius = 0;
    // 按下的时候是否是按在一个点上
    private boolean mIsTouchPoint = false;
    // 选中的所有点
    private List<Point> mSelectPoints = new ArrayList<Point>();

    private String mPassword = "01347";

    public LockPatternView(Context context) {
        this(context, null);
    }

    public LockPatternView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LockPatternView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //初始化九个宫格  onDraw会调用多次
        if (!mIsInit) {
            mIsInit = true;
            initDot();
            initPoint();
        }
        //绘制九个宫格
        drawShow(canvas);
    }

    /**
     * 初始化绘制九个宫格
     */
    private void drawShow(Canvas canvas) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Point point = mPoints[i][j];
                if (point.stateIsNomal()) {
                    //先绘制外圆
                    mNormalPaint.setColor(mOuterNormalColor);
                    canvas.drawCircle(point.centerX, point.centerY, mDotRadius, mNormalPaint);

                    //再绘制内圆
                    mNormalPaint.setColor(mInnerNormalColor);
                    canvas.drawCircle(point.centerX, point.centerY, mDotRadius / 6, mNormalPaint);
                }

                if (point.stateIsPressed()) {
                    //先绘制外圆
                    mPressedPaint.setColor(mOuterPressedColor);
                    canvas.drawCircle(point.centerX, point.centerY, mDotRadius, mPressedPaint);

                    //再绘制内圆
                    mPressedPaint.setColor(mInnerPressedColor);
                    canvas.drawCircle(point.centerX, point.centerY, mDotRadius / 6, mPressedPaint);
                }

                if (point.stateIsError()) {
                    //先绘制外圆
                    mErrorPaint.setColor(mOuterErrorColor);
                    canvas.drawCircle(point.centerX, point.centerY, mDotRadius, mErrorPaint);

                    //再绘制内圆
                    mErrorPaint.setColor(mOuterErrorColor);
                    canvas.drawCircle(point.centerX, point.centerY, mDotRadius / 6, mErrorPaint);
                }
            }
        }

        drwaLine(canvas);
    }

    /**
     * 绘制两点之间的连线
     * 写在onDraw方法内，每次dowm、move、up事件都会触发invalidate();重新进行一次绘制
     *
     * @param canvas
     */
    private void drwaLine(Canvas canvas) {
        if (mSelectPoints.size() > 1) {
            for (int i = 0; i < mSelectPoints.size() - 1; i++) {
                Point pointPre = mSelectPoints.get(i);
                Point pointNext = mSelectPoints.get(i + 1);
                //两个点之间绘制一条线
                canvas.drawLine(pointPre.centerX, pointPre.centerY, pointNext.centerX, pointNext.centerY, mLinePaint);
                //两个点之间绘制一个箭头
                drawArrow(canvas, mArrowPaint, pointPre, pointNext, (float) (mDotRadius / 5), 38);
            }
        }
    }

    /**
     * 画箭头
     */
    private void drawArrow(Canvas canvas, Paint paint, Point start, Point end, Float arrowHeight, int angle) {
        double d = (float) MethUtil.distance(start.centerX, start.centerY, end.centerX, end.centerY);
        float sin_B = (float) ((end.centerX - start.centerX) / d);
        float cos_B = (float) ((end.centerY - start.centerY) / d);
        float tan_A = (float) Math.tan(Math.toRadians(angle));
        float h = (float) (d - arrowHeight - mDotRadius * 1.1);
        float l = arrowHeight * tan_A;
        float a = l * sin_B;
        float b = l * cos_B;
        float x0 = h * sin_B;
        float y0 = h * cos_B;
        float x1 = start.centerX + (h + arrowHeight) * sin_B;
        float y1 = start.centerY + (h + arrowHeight) * cos_B;
        float x2 = start.centerX + x0 - b;
        float y2 = start.centerY + y0 + a;
        float x3 = start.centerX + x0 + b;
        float y3 = start.centerY + y0 - a;
        Path path = new Path();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        path.lineTo(x3, y3);
        path.close();
        canvas.drawPath(path, paint);
    }

    /**
     * 初始化 画笔
     */
    private void initPoint() {
        // new Paint 对象 ，设置 paint 颜色
        // 线的画笔
        mLinePaint = new Paint();
        mLinePaint.setColor(mInnerPressedColor);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth((mDotRadius / 9));
        // 按下的画笔
        mPressedPaint = new Paint();
        mPressedPaint.setStyle(Paint.Style.STROKE);
        mPressedPaint.setAntiAlias(true);
        mPressedPaint.setStrokeWidth((float) (mDotRadius / 6));
        // 错误的画笔
        mErrorPaint = new Paint();
        mErrorPaint.setStyle(Paint.Style.STROKE);
        mErrorPaint.setAntiAlias(true);
        mErrorPaint.setStrokeWidth(mDotRadius / 6);
        // 默认的画笔
        mNormalPaint = new Paint();
        mNormalPaint.setStyle(Paint.Style.STROKE);
        mNormalPaint.setAntiAlias(true);
        mNormalPaint.setStrokeWidth(mDotRadius / 9);
        // 箭头的画笔
        mArrowPaint = new Paint();
        mArrowPaint.setColor(mInnerPressedColor);
        mArrowPaint.setStyle(Paint.Style.FILL);
        mArrowPaint.setAntiAlias(true);
    }

    /**
     * http://mantis.ipanel.cn/bug_view_page.php?bug_id=0189779
     * 初始化点
     */
    private void initDot() {
        // 九个宫格，存到集合  mPoints[3][3] 的二维数组中
        // 不断绘制的时候这几个点都有状态，而且后面肯定需要回调密码点都有下标 点肯定是一个对象
        // 计算中心位置

        int width = getWidth();
        int height = getHeight();
        Log.d("TAG", "getWidth() = " + getWidth() + " getMeasuredWidth = " + getMeasuredWidth());

        // 兼容横竖屏
        int offsetX = 0;
        int offsetY = 0;
        if (height > width) {
            offsetY = (height - width) / 2;
            height = width;
        } else {
            offsetX = (width - height) / 2;
            width = height;
        }
        int squareWidth = width / 3;

        // 外圆的大小，根据宽度来
        mDotRadius = width / 12;

        // 计算和指定点的中心点位置
        mPoints[0][0] = new Point(offsetX + squareWidth / 2, offsetY + squareWidth / 2, 0);
        mPoints[0][1] = new Point(offsetX + squareWidth * 3 / 2, offsetY + squareWidth / 2, 1);
        mPoints[0][2] = new Point(offsetX + squareWidth * 5 / 2, offsetY + squareWidth / 2, 2);
        mPoints[1][0] = new Point(offsetX + squareWidth / 2, offsetY + squareWidth * 3 / 2, 3);
        mPoints[1][1] = new Point(offsetX + squareWidth * 3 / 2, offsetY + squareWidth * 3 / 2, 4);
        mPoints[1][2] = new Point(offsetX + squareWidth * 5 / 2, offsetY + squareWidth * 3 / 2, 5);
        mPoints[2][0] = new Point(offsetX + squareWidth / 2, offsetY + squareWidth * 5 / 2, 6);
        mPoints[2][1] = new Point(offsetX + squareWidth * 3 / 2, offsetY + squareWidth * 5 / 2, 7);
        mPoints[2][2] = new Point(offsetX + squareWidth * 5 / 2, offsetY + squareWidth * 5 / 2, 8);
    }

    public class Point {
        //圆心 的x坐标
        private int centerX;
        //圆心 的y坐标
        private int centerY;
        //密码相关
        private int index;

        public int STATUS_NORMAL = 1;
        private int STATUS_PRESSED = 2;
        private int STATUS_ERROR = 3;

        private int currentState = STATUS_NORMAL;

        Point(int x, int y, int index) {
            this.centerX = x;
            this.centerY = y;
            this.index = index;
        }

        public void setSTATUS_NORMAL() {
            this.currentState = STATUS_NORMAL;
        }

        public void setSTATUS_PRESSED() {
            this.currentState = STATUS_PRESSED;
        }

        public void setSTATUS_ERROR() {
            this.currentState = STATUS_ERROR;
        }

        public boolean stateIsNomal() {
            return currentState == STATUS_NORMAL;
        }

        public boolean stateIsPressed() {
            return currentState == STATUS_PRESSED;
        }

        public boolean stateIsError() {
            return currentState == STATUS_ERROR;
        }
    }

    // 手指触摸的位置
    private float mMovingX = 0f;
    private float mMovingY = 0f;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mMovingX = event.getX();
        mMovingY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下时判断是否在一个宫格内
                //如何判断---点到圆心的距离小于半径。
                Point point = isInPoints();
                if (point != null) {
                    mIsTouchPoint = true;//设置当前点为选中
                    mSelectPoints.add(point);//将当前点添加到集合中
                    //改变当前点的状态
                    point.setSTATUS_PRESSED();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                //效果是连线，前提必须是按下的时候必须在宫格内
                if (mIsTouchPoint) {
                    //不断触摸移动的时候要不断的去判断点
                    Point movingPoint = isInPoints();
                    if (movingPoint != null) {
                        //并且之前集合中不包含此个宫格
                        if (!mSelectPoints.contains(movingPoint)) {
                            mSelectPoints.add(movingPoint);//将当前点添加到集合中
                        }
                        //改变状态
                        movingPoint.setSTATUS_PRESSED();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                //抬起时mIsTouchPoint = false；
                mIsTouchPoint = false;
                // 回调密码获取监听
                if (isCorrectPSD()) {
                    //密码正确
                } else {
                    //密码错误 让Activity弹框 错误显示完之后要清空恢复默认
                }
                break;
            default:
                break;
        }
        invalidate();
        return true;
    }

    public Point isInPoints() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Point point = mPoints[i][j];
                if (MethUtil.checkInRound(mMovingX, mMovingY, mDotRadius, point.centerX, point.centerY)) {
                    return mPoints[i][j];
                }
            }
        }
        return null;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    private boolean isCorrectPSD() {
        String changeStr = "";
        for (Point point : mSelectPoints) {
            changeStr += point.index + "";
        }
        return changeStr == mPassword;
    }


}
