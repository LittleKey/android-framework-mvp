package me.littlekey.mvp.presenter;

/**
 * Created by nengxiangzhou on 15/4/21.
 */
public class SerialPresenter extends Presenter {
  private Presenter[] mPresenters;

  public SerialPresenter(Presenter... presenters) {
    this.mPresenters = presenters;
  }

  @Override
  public void bind(Object model) {
    if (mPresenters == null || mPresenters.length == 0) {
      return;
    }
    for (Presenter presenter : mPresenters) {
      passBind(presenter, model);
    }
  }

  @Override
  public void unbind() {
    if (mPresenters == null || mPresenters.length == 0) {
      return;
    }
    for (Presenter presenter : mPresenters) {
      presenter.unbind();
    }
  }
}
