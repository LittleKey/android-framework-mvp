package android.support.v4.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.view.View;

import com.yuanqi.base.utils.FormatUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by littlekey on 15/10/15.
 */
public class SwipeRefreshDrawable extends Drawable implements Animatable {


  @Retention(RetentionPolicy.CLASS)
  @IntDef({LARGE, DEFAULT})
  public @interface ProgressDrawableSize {}
  // Maps to ProgressBar.Large style
  static final int LARGE = 0;
  // Maps to ProgressBar default style
  static final int DEFAULT = 1;

  private Paint mBackgroundPaint;
  private Paint mForegroundPaint;
  private Resources mResources;
  private View mParent;
  private double mWidth;
  private double mHeight;
  private float mStartAngle;
  private float mEndAngle;
  private float mArrowScale;
  private float mRotation;
  private int mAlpha;

  public SwipeRefreshDrawable(Context context, View parent) {
    mForegroundPaint = new Paint();
    mForegroundPaint.setAntiAlias(true);
    mForegroundPaint.setStyle(Paint.Style.FILL);

    mBackgroundPaint = new Paint();
    mBackgroundPaint.setAntiAlias(true);
    mBackgroundPaint.setStyle(Paint.Style.FILL);

    mParent = parent;
    mResources = context.getResources();

    updateSizes(DEFAULT);
    setupAnimators();
  }

  @Override
  public void draw(Canvas canvas) {
    // TODO
    RectF bgRectF = new RectF(0, 0, (float) mWidth, (float) mHeight);
    canvas.drawARGB(0x00, 0x00, 0x00, 0x00);
    canvas.save();
    canvas.drawCircle(bgRectF.centerX(), bgRectF.centerY(), bgRectF.width() / 2, mBackgroundPaint);
    canvas.drawRect(bgRectF.width() / 4, bgRectF.height() / 4, bgRectF.width() * 3 / 4, bgRectF.height() * 3 / 4, mForegroundPaint);
    canvas.restore();
  }

  @Override
  public void setAlpha(int alpha) {
    mAlpha = alpha;
  }

  @Override
  public int getAlpha() {
    return mAlpha;
  }

  @Override
  public void setColorFilter(ColorFilter filter) {
    mBackgroundPaint.setColorFilter(filter);
    invalidateSelf();
  }

  @Override
  public int getOpacity() {
    return PixelFormat.TRANSLUCENT;
  }

  @Override
  public void start() {
    // TODO : start drawable animate
  }

  @Override
  public void stop() {
    // TODO : stop drawable animate
  }

  @Override
  public boolean isRunning() {
    // TODO : return drawable animate status
    return false;
  }

  private void setupAnimators() {
    // TODO : setup drawable animate
  }

  public void updateSizes(@MaterialProgressDrawable.ProgressDrawableSize int size) {
    // TODO : update drawable size
    if (size == LARGE) {
      mWidth = FormatUtils.dipsToPix(56);
      mHeight = FormatUtils.dipsToPix(56);
    } else {
      mWidth = FormatUtils.dipsToPix(40);
      mHeight = FormatUtils.dipsToPix(40);
    }
  }

  public void setBackgroundColor(int color) {
    mBackgroundPaint.setColor(color);
  }

  public void setColorSchemeColors(int... colors) {
    mForegroundPaint.setColor(colors[0]);
  }

  public void showArrow(boolean show) {
    // TODO : set draw arrow
  }

  public void setStartEndTrim(float startAngel, float endAngle) {
    mStartAngle = startAngel;
    mEndAngle = endAngle;
  }

  public void setArrowScale(float scale) {
    mArrowScale = scale;
  }

  public void setProgressRotation(float rotation) {
    mRotation = rotation;
  }
}
