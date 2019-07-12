package com.elegion.a5_9customviewprogressbar;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.Locale;

public class ProgressBarView extends View {


    private Paint mTextPaint;
    private Paint mPercentPaint;
    private float mTextWidth;

    private Rect mTextSize;
    private RectF mStandartBounds;
    private RectF mArcRect;
    private RectF mScaleRect;

    private float mTickValue;
    private float mSubTickValue;

    private float mIndeterminateSweep = 0;
    private Paint mProgressPaint;
    private Paint mProgressPaintEnd;

    private int mViewSize;
    private float mPersentSize;

    private float mProgressText;

    private int mStartColor;
    private int mEndColor;
    private float mIndeterminateSweepEnd;


    public ProgressBarView(Context context) {
        super(context);
    }

    public ProgressBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet set) {

        TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.ProgressBarView);
        mViewSize = ta.getDimensionPixelSize(R.styleable.ProgressBarView_viewSize, 24);
        mStartColor = ta.getColor(R.styleable.ProgressBarView_startColor, Color.GREEN);
        mEndColor = ta.getColor(R.styleable.ProgressBarView_endColor, Color.RED);
        ta.recycle();

        mPersentSize = mViewSize * 0.4f;

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setTextSize(mViewSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setAntiAlias(true);

        mPercentPaint = new Paint();
        mPercentPaint.setColor(Color.GRAY);
        mPercentPaint.setStyle(Paint.Style.STROKE);
        mPercentPaint.setTextSize(mPersentSize);
        mPercentPaint.setTextAlign(Paint.Align.CENTER);
        mPercentPaint.setAntiAlias(true);

        mProgressPaint = new Paint();
        mProgressPaint.setColor(mStartColor);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStrokeWidth(10);

        mProgressPaintEnd = new Paint();
        mProgressPaintEnd.setColor(mEndColor);
        mProgressPaintEnd.setStyle(Paint.Style.STROKE);
        mProgressPaintEnd.setAntiAlias(true);
        mProgressPaintEnd.setStrokeWidth(10);


        mTextSize = new Rect();
        mStandartBounds = new RectF();
        mScaleRect = new RectF();
        mArcRect = new RectF();

        mTickValue = mViewSize * 0.4f;
        mSubTickValue = mTickValue * 0.6f;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mTextWidth = mTextPaint.measureText("100.0 %");
        mTextPaint.getTextBounds("1", 0, 1, mTextSize);
        int desiredDiameter = (int) (mTextWidth * 2);
        int measureWidth = resolveSize(desiredDiameter, widthMeasureSpec);
        int measureHeight = resolveSize(desiredDiameter, heightMeasureSpec);

        mStandartBounds.set(0, 0, measureWidth, measureHeight);

        mArcRect.set(mStandartBounds);
        mArcRect.inset(mTextWidth * 0.25f, mTextWidth * 0.25f);

        mScaleRect.set(mArcRect);
        mScaleRect.inset(mTextWidth * 0.15f, mTextWidth * 0.15f);

        setMeasuredDimension(measureWidth, measureHeight);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        int cx = canvas.getWidth() / 2;
        int cy = canvas.getHeight() / 2;

        canvas.drawText(String.format(Locale.ENGLISH, "%.0f", mProgressText), cx, cy + (mTextSize.height()) / 2, mTextPaint);
        canvas.drawText("%", cx, cy + mTextSize.height(), mPercentPaint);
        canvas.drawArc(mArcRect, 135, 270, false, mTextPaint);


       float fraction = (float )mIndeterminateSweep / 270;
        int color = (Integer) new ArgbEvaluator().evaluate(
                fraction,
                mStartColor,
                mEndColor);
        mProgressPaint.setColor(color);

        canvas.drawArc(mArcRect, 135, mIndeterminateSweep, false, mProgressPaint);
        //canvas.drawArc(mArcRect, 320, mIndeterminateSweepEnd, false, mProgressPaintEnd);

        drawScale(canvas, cx, cy);
    }

    public void drawScale(Canvas canvas, int cx, int cy) {
        canvas.save();

        final int totalTicks = 31;
        for (int i = 0; i < totalTicks; i++) {

            final float value = getValueForTick(i);

            float mod = value % 30f;
            if ((Math.abs(mod - 0) < 0.001) || (Math.abs(mod - 30f) < 0.001)) {
                canvas.drawLine(mScaleRect.left, mScaleRect.bottom, mScaleRect.left - mTickValue, mScaleRect.bottom + mTickValue, mTextPaint);
            } else {
                canvas.drawLine(mScaleRect.left, mScaleRect.bottom, mScaleRect.left - mSubTickValue, mScaleRect.bottom + mSubTickValue, mTextPaint);
            }
            canvas.rotate(9f, cx, cy);
        }
        canvas.restore();
    }

    private float getValueForTick(int i) {
        return i * (30f / 5);
    }

    public void animateArch() {
        mIndeterminateSweep = 0;
        mIndeterminateSweepEnd = 0;
        mProgressText = 0;
        final ValueAnimator progress = ValueAnimator.ofFloat(0, 135);
        progress.setDuration(10000);
        progress.setInterpolator(new LinearInterpolator());
        progress.start();
        progress.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mIndeterminateSweep += 1;
                mProgressText += 0.37f;
                if (mIndeterminateSweep > 180)
                    //mIndeterminateSweepEnd += 1;

                if (mIndeterminateSweep > 270) {
                    progress.cancel();
                }
                invalidate();
            }
        });
    }
}
