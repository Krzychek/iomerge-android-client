package org.kbieron.iomerge.views;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import com.github.krzychek.server.model.Edge;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;


@EBean(scope = EBean.Scope.Singleton)
public class EdgeTriggerView extends View implements View.OnHoverListener {

	@SystemService
	WindowManager windowManager;

	private OnTrigListener trigMe;

	private WindowManager.LayoutParams windowLayoutParams;

	public EdgeTriggerView(Context context) {
		super(context);
		setOnHoverListener(this);

		windowLayoutParams = new WindowManager.LayoutParams( //
				WindowManager.LayoutParams.WRAP_CONTENT, //
				WindowManager.LayoutParams.WRAP_CONTENT, //
				WindowManager.LayoutParams.TYPE_PRIORITY_PHONE, //
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, //
				PixelFormat.TRANSPARENT);
	}

	@UiThread
	public void showOrMove(Edge edge) {
		switch (edge) {
			case LEFT:
				windowLayoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
				break;
			default:
			case RIGHT:
				windowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
		}

		if (getWindowToken() != null) {
			windowManager.updateViewLayout(this, windowLayoutParams);
		} else {
			windowManager.addView(this, windowLayoutParams);
		}
	}

	@Override
	public boolean onHover(View v, MotionEvent event) {
		if (MotionEvent.ACTION_HOVER_ENTER == event.getAction()) {
			trigMe.onTrig(v.getHeight() / event.getAxisValue(MotionEvent.AXIS_Y));
		}
		return false;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(1, heightMeasureSpec);
	}

	public void setOnTrigListener(OnTrigListener trigMe) {
		this.trigMe = trigMe;
	}

	public interface OnTrigListener {

		void onTrig(float v);
	}
}