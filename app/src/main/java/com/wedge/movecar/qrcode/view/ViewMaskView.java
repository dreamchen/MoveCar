package com.wedge.movecar.qrcode.view;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.wedge.movecar.R;
import com.wedge.movecar.qrcode.camera.CameraManager;

/**
 * Created by chenerlei on 15/7/16.
 */
public class ViewMaskView extends View {

    private boolean isMask = false;
    private Paint mPaint;
    private Canvas mCanvas;

    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     */
    public ViewMaskView(Context context) {
        super(context);
    }

    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     * <p/>
     * <p/>
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     *                access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     * @see #//View(Context, AttributeSet, int)
     */
    public ViewMaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of View allows subclasses to use their own base style when
     * they are inflating. For example, a Button class's constructor would call
     * this version of the super class constructor and supply
     * <code>R.attr.buttonStyle</code> for <var>defStyle</var>; this allows
     * the theme's button style to modify all of the base view attributes (in
     * particular its background) as well as the Button class's attributes.
     *
     * @param context  The Context the view is running in, through which it can
     *                 access the current theme, resources, etc.
     * @param attrs    The attributes of the XML tag that is inflating the view.
     * @param defStyle The default style to apply to this view. If 0, no style
     *                 will be applied (beyond what is included in the theme). This may
     *                 either be an attribute resource, whose value will be retrieved
     *                 from the current theme, or an explicit style resource.
     * @see #//View(Context, AttributeSet)
     */
    public ViewMaskView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Implement this to do your drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        Rect frame = CameraManager.get().getFramingRect();
//        if (frame == null) {
//            return;
//        }

        mPaint = new Paint();
        mCanvas = canvas;
    }

    public void showMask(){
        Log.e("MASK","show");
        int width = mCanvas.getWidth();
        int height = mCanvas.getHeight();
        mPaint.setStyle(Paint.Style.FILL);//设置填满
        // Draw the exterior (i.e. outside the framing rect) darkened
        mPaint.setColor(getResources().getColor(R.color.black));
        mPaint.setAlpha(220);
        mCanvas.drawRect(0, 0, width, height / 6 * 4, mPaint);
        mCanvas.drawRect(0, height/6*5, width, height, mPaint);
        invalidate();
    }

    public void closeMask(){
        Log.e("MASK","close");
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCanvas.drawPaint(mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        invalidate();
    }
}
