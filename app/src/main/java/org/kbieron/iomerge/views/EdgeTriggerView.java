package org.kbieron.iomerge.views;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import com.github.krzychek.iomerge.server.model.Edge;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.UiThread;
import org.kbieron.iomerge.services.ConnectionHandler;


@EBean(scope = EBean.Scope.Singleton)
public class EdgeTriggerView extends View implements View.OnHoverListener {

	@SystemService
	WindowManager windowManager;

	@Bean
	ConnectionHandler connectionHandler;

	private WindowManager.LayoutParams windowLayoutParams;

	public EdgeTriggerView(Context context) {
		super(context);
		setOnHoverListener(this);

		windowLayoutParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_ERROR, // needed to display over lockscreen
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, // show above system elements, e.g. status bar
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

	@UiThread
	public void hide() {
		if (isAttachedToWindow())
			windowManager.removeView(this);
	}

	@Override
	public boolean onHover(View v, MotionEvent event) {
		if (MotionEvent.ACTION_HOVER_ENTER == event.getAction()) {
			connectionHandler.sendExit(v.getHeight() / event.getAxisValue(MotionEvent.AXIS_Y));
		}
		return false;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(1, heightMeasureSpec);
	}
}