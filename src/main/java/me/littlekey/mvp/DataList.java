package me.littlekey.mvp;

import me.littlekey.base.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by nengxiangzhou on 15/5/8.
 */
public abstract class DataList<T> {
  private List<DataLoadObserver<T>> mObservers;
  private DataProcessor<T> mProcessor;
  private boolean mIsLoading;

  public DataList() {
    mObservers = new ArrayList<>();
    mIsLoading = false;
  }

  public void registerDataLoadObserver(DataLoadObserver<T> observer) {
    mObservers.add(observer);
  }

  public void unregisterDataLoadObservers() {
    mObservers.clear();
  }

  public DataProcessor<T> getProcessor() {
    return mProcessor;
  }

  public void setProcessor(DataProcessor<T> processor) {
    if (this.mProcessor == null || CollectionUtils.isEmpty(getItems())) {
      this.mProcessor = processor;
    } else {
      throw new IllegalStateException("Already set processor.");
    }
  }

  protected final List<T> processItems(List<T> newData) {
    return mProcessor != null ? mProcessor.generate(newData) : newData;
  }

  protected void notifyLoadStart(DataLoadObserver.Op op) {
    mIsLoading = true;
    for (DataLoadObserver<T> observer : mObservers) {
      observer.onLoadStart(op);
    }
  }

  protected void notifyLoadSuccess(DataLoadObserver.Op op, DataLoadObserver.OpData<T> opData) {
    mIsLoading = false;
    for (DataLoadObserver<T> observer : mObservers) {
      observer.onLoadSuccess(op, opData);
    }
  }

  protected void notifyLoadError(DataLoadObserver.Op op, Exception e) {
    mIsLoading = false;
    for (DataLoadObserver<T> observer : mObservers) {
      observer.onLoadError(op, e);
    }
  }

  public abstract List<T> getItems();

  public abstract T getItem(int position);

  public abstract boolean hasMore();

  protected abstract void doLoadMore();

  protected abstract void doRefresh();

  public boolean isLoading() {
    return mIsLoading;
  }

  public final void loadMore() {
    if (!mIsLoading && hasMore()) {
      doLoadMore();
    }
  }

  public final void refresh() {
    if (!mIsLoading) {
      doRefresh();
    }
  }
}
