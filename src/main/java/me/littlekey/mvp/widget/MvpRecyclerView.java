package me.littlekey.mvp.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import me.littlekey.base.ReadOnlyList;
import me.littlekey.base.utils.CollectionUtils;
import me.littlekey.mvp.adapter.HeaderFooterAdapter;
import me.littlekey.mvp.presenter.ViewGroupPresenter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by nengxiangzhou on 15/4/20.
 */
public class MvpRecyclerView extends RecyclerView {

  public MvpRecyclerView(Context context) {
    super(context);
    init();
  }

  public MvpRecyclerView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public MvpRecyclerView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init();
  }

  private void init() {
    setRecyclerListener(new RecyclerListener() {
      @Override
      public void onViewRecycled(RecyclerView.ViewHolder holder) {
        ((ViewHolder) holder).presenter.unbind();
      }
    });
  }

  public void setEmptyView(HeaderFooterAdapter.ViewData emptyView) {
    if (getAdapter() instanceof HeaderFooterAdapter) {
      ((HeaderFooterAdapter) getAdapter()).setEmptyView(emptyView);
    } else {
      Log.d(getClass().getName(), "this adapter can not set empty view.");
    }
  }

//  private float downY;
//  @Override
//  public boolean onInterceptTouchEvent(MotionEvent event) {
//    switch (event.getAction() & MotionEventCompat.ACTION_MASK) {
//      case MotionEvent.ACTION_DOWN:
//        downY = event.getY();
//        break;
//      case MotionEvent.ACTION_MOVE:
//        final float now_y = event.getY();
//        Log.d(getClass().getName(), String.valueOf(Math.abs(now_y - downY)));
//        if (Math.abs(now_y - downY) > 10) {
//          EventBus.getDefault().post(new TrailsViewPager.BlockInterceptTouchEvent(true));
//        }
//        break;
//      case MotionEvent.ACTION_UP:
//      case MotionEvent.ACTION_CANCEL:
//        EventBus.getDefault().post(new TrailsViewPager.BlockInterceptTouchEvent(false));
//    }
//    return super.onInterceptTouchEvent(event);
//  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    switch (event.getAction() & MotionEventCompat.ACTION_MASK) {
      case MotionEvent.ACTION_MOVE:
//        EventBus.getDefault().post(new TrailsViewPager.BlockInterceptTouchEvent(true));
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager &&
            ((LinearLayoutManager)layoutManager).findFirstCompletelyVisibleItemPosition() != 0) {
          break;
        }
      case MotionEvent.ACTION_UP:
      case MotionEvent.ACTION_CANCEL:
        getParent().requestDisallowInterceptTouchEvent(false);
//        EventBus.getDefault().post(new TrailsViewPager.BlockInterceptTouchEvent(false));
        break;
    }
    return super.onTouchEvent(event);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    public final ViewGroupPresenter presenter;

    public ViewHolder(ViewGroupPresenter presenter) {
      super(presenter.view);
      this.presenter = presenter;
      this.presenter.setHolder(this);
    }
  }

  public abstract static class Adapter<T> extends RecyclerView.Adapter<ViewHolder>
      implements ReadOnlyList<T> {
    protected List<T> data;
    private int lastPosition = -1;
    private Interpolator mInterpolator = new LinearInterpolator();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return new ViewHolder(onCreateViewPresenter(parent, viewType));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
      T model = data.get(position);
      holder.presenter.bind(model);
//      setScrollItemAnimation(holder.itemView, position);
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
      super.onViewRecycled(holder);
      holder.presenter.unbind();
    }

    private void setScrollItemAnimation(View view, int position) {
      if (position > lastPosition)
      {
        for (ObjectAnimator anim: getAnimators(view)) {
          anim.setDuration(300).start();
          anim.setInterpolator(mInterpolator);
        }
        lastPosition = position;
      } else {
        clear(view);
      }
    }

    protected ObjectAnimator[] getAnimators(View view) {
      return new ObjectAnimator[]{
        ObjectAnimator.ofFloat(view, "alpha", 0f, 1f),
        ObjectAnimator.ofFloat(view, "scaleX", 0.5f, 1f),
        ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1f),
      };
    }

    private void clear(View v) {
      ViewCompat.setAlpha(v, 1);
      ViewCompat.setScaleY(v, 1);
      ViewCompat.setScaleX(v, 1);
      ViewCompat.setTranslationY(v, 0);
      ViewCompat.setTranslationX(v, 0);
      ViewCompat.setRotation(v, 0);
      ViewCompat.setRotationY(v, 0);
      ViewCompat.setRotationX(v, 0);
      ViewCompat.setPivotY(v, v.getMeasuredHeight() / 2);
      ViewCompat.setPivotX(v, v.getMeasuredWidth() / 2);
      ViewCompat.animate(v).setInterpolator(null);
    }

    protected int mapPosition(int position) {
      return position;
    }

    @Override
    public int getItemCount() {
      return size();
    }

    public T getItem(int position) {
      return data.get(position);
    }

    public int size() {
      return data == null ? 0 : data.size();
    }

    protected abstract ViewGroupPresenter onCreateViewPresenter(ViewGroup parent, int ViewType);

    public void setData(List<T> data) {
      this.data = new ArrayList<>(data);
      lastPosition = -1;
      notifyDataSetChanged();
    }

    public List<T> getData() {
      return this.data;
    }

    public boolean appendData(T item) {
      return insertData(size(), item);
    }

    public boolean appendData(List<T> newData) {
      return insertData(size(), newData);
    }

    public boolean insertData(int position, T item) {
      if (item == null) {
        return false;
      }
      if (data == null) {
        data = new ArrayList<>();
      }
      if (position < 0 || position > data.size()) {
        return false;
      }
      data.add(position, item);
      if (position <= lastPosition) {
        lastPosition++;
      }
      notifyItemInserted(mapPosition(position));
      return true;
    }

    public boolean insertData(int position, List<T> newData) {
      if (CollectionUtils.isEmpty(newData)) {
        return false;
      }
      if (data == null) {
        data = new ArrayList<>();
      }
      if (position < 0 || position > data.size()) {
        return false;
      }
      data.addAll(position, newData);
      if (position <= lastPosition) {
        lastPosition += newData.size();
      }
      notifyItemRangeInserted(mapPosition(position), newData.size());
      return true;
    }

    public boolean changeData(int position, T item) {
      if (item == null) {
        return false;
      }
      if (data == null) {
        data = new ArrayList<>();
      }
      if (position < 0 || position > data.size()) {
        return false;
      }
      data.set(position, item);
      notifyItemChanged(mapPosition(position));
      return true;
    }

    public boolean moveData(int fromPosition, int toPosition) {
      if (fromPosition == toPosition) {
        return false;
      }
      if (data == null) {
        data = new ArrayList<>();
      }
      if (fromPosition < 0 || fromPosition > data.size()
          || toPosition < 0 || toPosition > data.size()) {
        return false;
      }
      T item = data.remove(fromPosition);
      data.add(toPosition, item);
      if (fromPosition <= lastPosition) {
        lastPosition--;
      }
      if (toPosition <= lastPosition) {
        lastPosition++;
      }
      notifyItemMoved(mapPosition(fromPosition), mapPosition(toPosition));
      return true;
    }

    public boolean removeData(T item) {
      if (item == null) {
        return false;
      }
      if (CollectionUtils.isEmpty(data)) {
        return false;
      }
      int position = data.indexOf(item);
      if (position < 0) {
        return false;
      }
      data.remove(position);
      if (position <= lastPosition) {
        lastPosition--;
      }
      notifyItemRemoved(mapPosition(position));
      return true;
    }

    public boolean removeRangeData(T item, int count) {
      return item != null && !CollectionUtils.isEmpty(data)
          && removeRangeData(data.indexOf(item), count);
    }

    public boolean removeRangeData(int start, int count) {
      if (start < 0) {
        return false;
      }
      int i = 0;
      while (i++ < count && start < size()) {
        data.remove(start);
        if (start <= lastPosition) {
          lastPosition--;
        }
      }
      notifyItemRangeRemoved(mapPosition(start), i - 1);
      return true;
    }

    public int indexOf(T item) {
      return data == null ? -1 : data.indexOf(item);
    }
  }
}