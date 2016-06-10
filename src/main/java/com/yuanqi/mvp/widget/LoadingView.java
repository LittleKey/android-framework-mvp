package com.yuanqi.mvp.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.widget.MvpProgressDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by nengxiangzhou on 15/5/13.
 */
public class LoadingView extends ImageView {

  private MvpProgressDrawable mDrawable;

  public LoadingView(Context context) {
    super(context);
    init();
  }

  public LoadingView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public LoadingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  private void init() {
    mDrawable = new MvpProgressDrawable(getContext(), this);
    mDrawable.showArrow(true);
    mDrawable.setAlpha(255);
    int[] colors = new int[] {0xffff0000, 0xff00ff00, 0xff0000ff};
    mDrawable.setColorSchemeColors(colors);
    setImageDrawable(mDrawable);
  }

  public void setColorSchemeColors(int[] colors) {
    mDrawable.setColorSchemeColors(colors);
  }

  public void start() {
    if (!mDrawable.isRunning()) {
      mDrawable.start();
    }
  }

  public void stop() {
    mDrawable.stop();
  }
}
