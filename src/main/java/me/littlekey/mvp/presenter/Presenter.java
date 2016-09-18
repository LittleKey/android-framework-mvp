package me.littlekey.mvp.presenter;

import android.view.View;

/**
 * Created by nengxiangzhou on 15/4/21.
 */
public abstract class Presenter {
  int id;
  View view;
  ViewGroupPresenter group;

  public abstract void bind(Object model);

  public void unbind() {}

  public final ViewGroupPresenter group() {
    if (group == null) {
      throw new IllegalArgumentException("Please ensure this view binder is called bind()!");
    }
    return group;
  }

  public final View view() {
    if (view == null) {
      throw new IllegalArgumentException("Please ensure this view binder is called bind()!");
    }
    return view;
  }

  public final int id() {
    return id;
  }

  public void passBind(Presenter presenter, Object model) {
    passBind(presenter, view, model);
  }

  private void passBind(Presenter presenter, View view, Object model) {
    if (presenter == null) {
      return;
    }
    if ((presenter.group == null || presenter.group == group)
        && (presenter.view == null || presenter.view == view)) {
      presenter.group = group;
      presenter.view = view;
      presenter.id = id;
      presenter.bind(model);
    } else {
      throw new IllegalStateException("Different view can't pass bind.");
    }
  }
}
