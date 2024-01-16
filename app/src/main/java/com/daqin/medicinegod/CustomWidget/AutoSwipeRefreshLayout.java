package com.daqin.medicinegod.CustomWidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AutoSwipeRefreshLayout extends SwipeRefreshLayout {

	public AutoSwipeRefreshLayout(Context context) {
		super(context);
	}

	public AutoSwipeRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	/**
	 * 自动刷新
	 */
	public void autoRefresh() {
		try {
			Field mCircleView = SwipeRefreshLayout.class.getDeclaredField("mCircleView");
			mCircleView.setAccessible(true);
			View progress = (View) mCircleView.get(this);
			progress.setVisibility(VISIBLE);

			Method setRefreshing = SwipeRefreshLayout.class.getDeclaredMethod("setRefreshing", boolean.class, boolean.class);
			setRefreshing.setAccessible(true);
			setRefreshing.invoke(this, true, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
/* 优点:封装后代码优雅,不需要手动请求数据 */
