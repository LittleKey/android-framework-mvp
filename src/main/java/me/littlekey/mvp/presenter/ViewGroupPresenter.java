package me.littlekey.mvp.presenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.littlekey.mvp.PageContext;
import me.littlekey.mvp.widget.MvpRecyclerView;

/**
 * Created by nengxiangzhou on 15/4/20.
 */
public class ViewGroupPresenter {
  private static final String TAG = ViewGroupPresenter.class.getSimpleName();
  public final ViewGroup view;
  public final Context context;
  private final SparseArray<Presenter> mPresenters;
  public RecyclerView.ViewHolder holder;
  public PageContext pageContext;

  public ViewGroupPresenter(ViewGroup parent, int layout, MvpRecyclerView.Adapter adapter) {
    this((ViewGroup) LayoutInflater.from(parent.getContext()).inflate(layout, parent, false));
    this.pageContext.adapter = adapter;
  }

  public ViewGroupPresenter(ViewGroup parent, int layout, PageContext pageContext) {
    this((ViewGroup) LayoutInflater.from(parent.getContext()).inflate(layout, parent, false), pageContext);
    if (this.pageContext.layoutManager == null && parent instanceof RecyclerView) {
      this.pageContext.layoutManager = ((RecyclerView) parent).getLayoutManager();
    }
  }

  public ViewGroupPresenter(ViewGroup parent, int layout) {
    this(parent, layout, new PageContext());
  }

  public ViewGroupPresenter(ViewGroup view) {
    this(view, new PageContext());
  }

  public ViewGroupPresenter(ViewGroup view, PageContext pageContext) {
    this.view = view;
    this.context = view.getContext();
    this.mPresenters = new SparseArray<>();
    this.pageContext = pageContext;
  }

  public void setHolder(RecyclerView.ViewHolder holder) {
    this.holder = holder;
  }

  public void bind(Object model) {
    for (int i = 0; i < mPresenters.size(); i++) {
      Presenter presenter = findPresenter(i);
      if (presenter != null) {
        presenter.bind(model);
      }
    }
  }

  public void unbind() {
    for (int i = 0; i < mPresenters.size(); i++) {
      Presenter presenter = findPresenter(i);
      if (presenter != null) {
        presenter.unbind();
      }
    }
  }

  public ViewGroupPresenter add(int id, Presenter presenter) {
    return set(id, presenter, false);
  }

  public ViewGroupPresenter add(Presenter presenter) {
    return add(0, presenter);
  }

  public ViewGroupPresenter replace(int id, Presenter presenter) {
    return set(id, presenter, true);
  }

  public ViewGroupPresenter replace(Presenter presenter) {
    return replace(0, presenter);
  }

  public ViewGroupPresenter remove(int id) {
    return set(id, null, true);
  }

  public ViewGroupPresenter removeAll() {
    mPresenters.clear();
    return this;
  }

  private Presenter findPresenter(int index) {
    View elementView;
    int viewId = mPresenters.keyAt(index);
    if (viewId == 0) {
      elementView = view;
    } else {
      elementView = view.findViewById(viewId);
    }
    if (elementView == null) {
      Log.w(TAG, "View not find with id:" + String.valueOf(mPresenters.keyAt(index)));
      return null;
    } else {
      Presenter presenter = mPresenters.valueAt(index);
      presenter.view = elementView;
      presenter.id = viewId;
      return presenter;
    }
  }

  private ViewGroupPresenter set(int id, Presenter presenter, boolean replace) {
    if (presenter == null) {
      if (replace) {
        mPresenters.remove(id);
      }
      return this;
    }
    Presenter exist = mPresenters.get(id);
    if (exist != null && !replace) {
      presenter = new SerialPresenter(exist, presenter);
    }
    presenter.group = this;
    mPresenters.put(id, presenter);
    return this;
  }
}
