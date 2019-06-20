package com.advancedprogramming2.advancedprogramming2ex4;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.arch.core.util.Function;

import java.util.ArrayList;
import java.util.List;

/**
 * The JoystickView class extends the View class
 * is the view of the joystick.
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
    private boolean wasInCenterX;
    private boolean wasInCenterY;

    private List<Function<String, Void>> updateFunctions;

    /**
     * The JoystickView constructor gets as a parameter a Context context.
     * @param context The Context of the view.
     */
    public JoystickView(Context context) {
        super(context);
        init(context);
    }

    /**
     * The JoystickView constructor gets as parameters a Context context, an AttributeSet attrs.
     * @param context The Context of the view.
     * @param attrs The AttributeSet of the view.
     */
    public JoystickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * The JoystickView constructor gets as parameters a Context context, an AttributeSet attrs,
     * int defStyle.
     * @param context The Context of the view.
     * @param attrs The AttributeSet of the view.
     * @param defStyle The int defStyle of the view.
     */
    public JoystickView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /**
     * The init function of the view which initializes the view.
     * @param context The Context of the view.
     */
    private void init(Context context) {
        context.getApplicationContext();
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(Color.CYAN);
        backgroundPaint.setStyle(Paint.Style.FILL);
        outsideOvalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outsideOvalPaint.setColor(Color.LTGRAY);
        outsideOvalPaint.setStyle(Paint.Style.FILL);
        innerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerCirclePaint.setColor(Color.GREEN);
        innerCirclePaint.setStyle(Paint.Style.FILL);
        updateFunctions = new ArrayList<>();
        wasInCenterX = true;
        wasInCenterY = true;
    }

    /**
     * The onSizeChanged function which runs every time that the size of the screen changes
     * changes the view according to it.
     * @param w New width of the view.
     * @param h New height of the view.
     * @param oldw Old width of the view.
     * @param oldh Old height of the view.
     */
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
        double minWidthHeight = Math.min(w, h);
        innerCircleRadius = (float)(radius * 2 * minWidthHeight);
    }

    /**
     * The onDraw function gets as a parameter a Canvas canvas.
     * @param canvas The Canvas to draw the view on.
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBackground(canvas);

        drawOutsideOval(canvas);

        drawInnerCircle(canvas);

    }

    /**
     * The drawBackground function gets as a parameter Canvas canvas
     * and draws on it the background of the view.
     * @param canvas Canvas to draw the background of the view on.
     */
    protected void drawBackground(Canvas canvas) {
        Rect rect = new Rect(0,0,backgroundWidth, backgroundHeight);
        Paint paint = backgroundPaint;
        canvas.drawRect(rect, paint);
    }

    /**
     * The drawOutsideOval function gets as a parameter Canvas canvas
     * and draws the outside oval on it.
     * @param canvas Canvas to draw the outside oval on.
     */
    protected void drawOutsideOval(Canvas canvas) {
        RectF rectF = new RectF(outsideOvalRectLeft, outsideOvalRectTop, outsideOvalRectRight, outsideOvalRectBottom);
        Paint paint = outsideOvalPaint;
        canvas.drawOval(rectF, paint);
    }

    /**
     * The drawInnerCircle function gets as a parameter Canvas canvas
     * and draws the inner circle on it.
     * @param canvas Canvas to draw the inner circle on.
     */
    protected void drawInnerCircle(Canvas canvas) {
        Paint paint = innerCirclePaint;
        canvas.drawCircle(innerCircleCX,innerCircleCY,innerCircleRadius,paint);
    }

    /**
     * The onTouchEvent gets a parameter MotionEvent event and handles it.
     * @param event The MotionEvent that has occured.
     * @return true.
     */
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
                    if (isDotInOval(x, y)) {
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

    /**
     * The performClick function which is performed on click.
     * @return the base performClick boolean.
     */
    @Override
    public boolean performClick() {
        return super.performClick();
    }

    /**
     * The function isInCircle gets as parameters a float posX and a float posY
     * which symbolizes a point and returns true if the the point is inside the circle
     * else it returns false.
     * @param posX float x of the point.
     * @param posY float y of the point.
     * @return true if the point is in the circle else false.
     */
    private boolean isInCircle(float posX, float posY) {
        return (Math.abs(posX - innerCircleCX) <= innerCircleRadius
                && Math.abs(posY - innerCircleCY) <= innerCircleRadius);
    }

    /**
     * The setIsCircleDragged gets as a parameter a boolean value
     * and sets the isCircleDragged member's value to it.
     * And resets the place of the circle if the value is false
     * and the isCircleDragged member is true.
     * @param value The value to put in the isCircleDragged member.
     */
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

    /**
     * The function setInnerCircleCX is the setter function of the innerCircleCX member.
     * It gets as a parameter a float newCX and sets the innerCircleCX member's value to it.
     * @param newCX The value to set the innerCircleCX member's value to.
     */
    private void setInnerCircleCX(float newCX) {
        if (innerCircleCX == newCX) {
            return;
        }
        innerCircleCX = newCX;
        if (!(wasInCenterX && isInCenter())) {
            update("X");
        }
        wasInCenterX = isInCenter();
    }

    /**
     * The function setInnerCircleCY is the setter function of the innerCircleCY member.
     * It gets as a parameter a float newCY and sets the innerCircleCY member's value to it.
     * @param newCY The value to set the innerCircleCY member's value to.
     */
    private void setInnerCircleCY(float newCY) {
        if (innerCircleCY == newCY) {
            return;
        }
        innerCircleCY = newCY;
        if (!(wasInCenterY && isInCenter())) {
            update("Y");
        }
        wasInCenterY = isInCenter();
    }

    /**
     * The isDotInOval function gets as parameters a float posX and a float posY
     * and returns true if the dot with these coordinates is inside the outside oval
     * else it returns false.
     * @param posX The x value of the dot.
     * @param posY The y value of the dot.
     * @return true if the dot with the coordinates from the parameters is inside the outside oval
     * else false.
     */
    private boolean isDotInOval(float posX, float posY) {
        int widthRadius = (outsideOvalRectRight - outsideOvalRectLeft) / 2;
        int heightRadius = (outsideOvalRectBottom - outsideOvalRectTop) / 2;
        int x = outsideOvalRectLeft + widthRadius;
        int y = outsideOvalRectTop + heightRadius;
        float value1 = ((posX - x) * (posX - x)) / (widthRadius * widthRadius);
        float value2 = ((posY - y) * (posY - y)) / (heightRadius * heightRadius);
        return (value1 + value2) <= 1;
    }

    /**
     * The getInnerCircleCX function is the getter function of the innerCircleCX member's value.
     * @return innerCircleCX member's value.
     */
    public float getInnerCircleCX() {
        return innerCircleCX;
    }

    /**
     * The getInnerCircleCY function is the getter function of the innerCircleCY member's value.
     * @return innerCircleCY member's value.
     */
    public float getInnerCircleCY() {
        return innerCircleCY;
    }

    /**
     * The function addUpdateFunction gets as a parameter a Function<String, Void> updateFunction
     * and adds it to the updateFunctions.
     * @param updateFunction updateFunction to add to the updateFunctions.
     */
    public void addUpdateFunction(Function<String, Void> updateFunction) {
        updateFunctions.add(updateFunction);
    }

//    /**
//     * The removeUpdateFunction function gets as a parameter a Function<String, Void> updateFunction
//     * and removes it from tue updateFunctionos if it is there.
//     * @param updateFunction
//     */
//    public void removeUpdateFunction(Function<String, Void> updateFunction) {
//        if (!updateFunctions.contains(updateFunction)) {
//            return;
//        }
//        updateFunctions.remove(updateFunction);
//    }

    /**
     * The update function gets as parameters a String data and calls all the updateFunctions
     * apply function with this dataa.
     * @param data The String data to update the updateFunctions.
     */
    private void update(String data) {
        for (Function<String, Void> func: updateFunctions) {
            func.apply(data);
        }
    }

    /**
     * The getMaxX function returns the max x value of the joystick.
     * @return max x value of the joystick.
     */
    public float getMaxX() {
        return outsideOvalRectRight;
    }

    /**
     * The getMinX function returns the min x value of the joystick.
     * @return min x value of the joystick.
     */
    public float getMinX() {
        return outsideOvalRectLeft;
    }

    /**
     * The getMaxY function returns the max y value of the joystick.
     * @return max y value of the joystick.
     */
    public float getMaxY() {
        return outsideOvalRectBottom;
    }

    /**
     * The getMinY function returns the min y value of the joystick.
     * @return min y value of the joystick.
     */
    public float getMinY() {
        return outsideOvalRectTop;
    }

    /**
     * The isInCenter function returns true if the circle of the joystick is in the center
     * else false.
     * @return true if the circle of the joystick is in the center else false.
     */
    public boolean isInCenter() {
        return !isCircleDragged;
    }

}
