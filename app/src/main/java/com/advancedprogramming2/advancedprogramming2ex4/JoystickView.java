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

import androidx.arch.core.util.Function;

import java.util.ArrayList;
import java.util.List;

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

    private float innerCircleCX;
    private float innerCircleCY;
    private float innerCircleRadius;

    private boolean isCircleDragged;

    private List<Function<String, Void>> updateFunctions;

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
        updateFunctions = new ArrayList<Function<String, Void>>();
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
        setInnerCircleCX((float)(w / 2));
        setInnerCircleCY((float)(h / 2));
        innerCircleRadius = (float)(radius * 2 * w);
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

        int action = event.getActionMasked();

        switch(action) {
            case MotionEvent.ACTION_DOWN: {
                float x = event.getX();
                float y = event.getY();
                if (isInCircle(x, y)) {
                    setIsCircleDragged(true);
                } else {
                    setIsCircleDragged(false);
                }
                performClick();
                break;
            }
            case MotionEvent.ACTION_UP: {
                setIsCircleDragged(false);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (isCircleDragged) {
                    float x = event.getX();
                    float y = event.getY();
                    if (isCircleInOval(x, y, 0)) {
                        setInnerCircleCX(x);
                        setInnerCircleCY(y);
                    }
                }
                break;
            }
        }
        invalidate();
        return true;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private boolean isInCircle(float posX, float posY) {
        return (Math.abs(posX - innerCircleCX) <= innerCircleRadius
                && Math.abs(posY - innerCircleCY) <= innerCircleRadius);
    }

    private void setIsCircleDragged(boolean value) {
        if (isCircleDragged == value) {
            return;
        }
        isCircleDragged = value;
        if (!isCircleDragged) {
            setInnerCircleCX((float)(backgroundWidth / 2));
            setInnerCircleCY((float)(backgroundHeight / 2));
        }
    }

    private void setInnerCircleCX(float newCX) {
        if (innerCircleCX == newCX) {
            return;
        }
        innerCircleCX = newCX;
        update("X");
    }

    private void setInnerCircleCY(float newCY) {
        if (innerCircleCY == newCY) {
            return;
        }
        innerCircleCY = newCY;
        update("Y");
    }

    private boolean isCircleInOval(float posX, float posY, float radius) {
        if (radius == 0) {
            return isDotInOval(posX, posY);
        }
        return isDotInOval(posX + radius, posY + radius) &&
                isDotInOval(posX + radius, posY - radius) &&
                isDotInOval(posX - radius, posY + radius) &&
                isDotInOval(posX - radius, posY - radius);
    }

    private boolean isDotInOval(float posX, float posY) {
        int widthRadius = (outsideOvalRectRight - outsideOvalRectLeft) / 2;
        int heightRadius = (outsideOvalRectBottom - outsideOvalRectTop) / 2;
        int x = outsideOvalRectLeft + widthRadius;
        int y = outsideOvalRectTop + heightRadius;
        float value1 = ((posX - x) * (posX - x)) / (widthRadius * widthRadius);
        float value2 = ((posY - y) * (posY - y)) / (heightRadius * heightRadius);
        return (value1 + value2) <= 1;
    }

    public float getInnerCircleCX() {
        return innerCircleCX;
    }

    public float getInnerCircleCY() {
        return innerCircleCY;
    }

    public void addUpdateFunction(Function<String, Void> updateFunction) {
        updateFunctions.add(updateFunction);
    }

    public void removeUpdateFunction(Function<String, Void> updateFunction) {
        if (!updateFunctions.contains(updateFunction)) {
            return;
        }
        updateFunctions.remove(updateFunction);
    }

    private void update(String data) {
        for (Function<String, Void> func: updateFunctions) {
            func.apply(data);
        }
    }

    public float getMaxX() {
        return outsideOvalRectRight;
    }

    public float getMinX() {
        return outsideOvalRectLeft;
    }

    public float getMaxY() {
        return outsideOvalRectBottom;
    }

    public float getMinY() {
        return outsideOvalRectTop;
    }

}
