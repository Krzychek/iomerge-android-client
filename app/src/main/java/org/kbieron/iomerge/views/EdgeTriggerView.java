package org.kbieron.iomerge.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;


@SuppressLint("ViewConstructor")
public class EdgeTriggerView extends View implements View.OnHoverListener {

    private final Runnable trigMe;

    public EdgeTriggerView(Context context, Runnable trigMe) {
        super(context);
        this.trigMe = trigMe;
        setOnHoverListener(this);
    }

    public static WindowManager.LayoutParams getDefaultLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams( //
                WindowManager.LayoutParams.WRAP_CONTENT, //
                WindowManager.LayoutParams.WRAP_CONTENT, //
                WindowManager.LayoutParams.TYPE_PHONE, //
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, //
                PixelFormat.TRANSLUCENT);

        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        layoutParams.x = 0;
        layoutParams.y = 0;

        return layoutParams;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(1, heightMeasureSpec);
    }

    @Override
    public boolean onHover(View v, MotionEvent event) {
        if (MotionEvent.ACTION_HOVER_ENTER == event.getAction()) {
            trigMe.run();
        }
        return false;
    }
}