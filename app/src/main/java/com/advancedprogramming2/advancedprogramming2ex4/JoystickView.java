package com.advancedprogramming2.advancedprogramming2ex4;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class JoystickView extends View {
    private Paint backgroundPaint;
    private Paint outsideOvalPaint;
    private Paint innerCirclePaint;

    private int backgroundWidth;
    private int backgroundHeight;

    private int outsideOvalRectRight;
    private int outsideOvalRectTop;
    private int outsideOvalRectLeft;
    private int outsideOvalRectBottom;

    private int innerCircleCX;
    private int innerCircleCY;
    private int innerCircleRadius;

    public JoystickView(Context context) {
        super(context);
        init(context);
    }

    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public JoystickView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.CYAN);
        backgroundPaint.setStyle(Paint.Style.FILL);
        outsideOvalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outsideOvalPaint.setColor(Color.LTGRAY);
        outsideOvalPaint.setStyle(Paint.Style.FILL);
        innerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerCirclePaint.setColor(Color.GREEN);
        innerCirclePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        backgroundWidth = w;
        backgroundHeight = h;

        double dh = 0.087;
        double dw = 0.082;
        outsideOvalRectTop = (int)(dh * h);
        outsideOvalRectRight = (int)(w - dw * w);
        outsideOvalRectLeft = (int)(dw * w);
        outsideOvalRectBottom = (int)(h - dh * h);

        double radius = 0.1;
        innerCircleCX = w / 2;
        innerCircleCY = h / 2;
        innerCircleRadius = (int)(radius * 2 * w);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBackground(canvas);

        drawOutsideOval(canvas);

        drawInnerCircle(canvas);

    }

    protected void drawBackground(Canvas canvas) {
        Rect rect = new Rect(0,0,backgroundWidth, backgroundHeight);
        Paint paint = backgroundPaint;
        canvas.drawRect(rect, paint);
    }

    protected void drawOutsideOval(Canvas canvas) {
        RectF rectF = new RectF(outsideOvalRectLeft, outsideOvalRectTop, outsideOvalRectRight, outsideOvalRectBottom);
        Paint paint = outsideOvalPaint;
        canvas.drawOval(rectF, paint);
    }

    protected void drawInnerCircle(Canvas canvas) {
        Paint paint = innerCirclePaint;
        canvas.drawCircle(innerCircleCX,innerCircleCY,innerCircleRadius,paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        

        return true;
    }

}
