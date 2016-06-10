package com.yuanqi.mvp;

import java.util.List;

/**
 * Created by nengxiangzhou on 15/5/8.
 */
public interface DataLoadObserver<T> {
  enum Op {
    ADD, REMOVE, UPDATE, REFRESH
  }

  class OpData<T> {
    public final int position;
    public final T at;
    public final T ah;
    public final List<T> newData;

    public OpData(int position, T at, T ah, List<T> newData) {
      this.position = position;
      this.at = at;
      this.ah = ah;
      this.newData = newData;
    }
  }
  void onLoadStart(Op op);
  void onLoadSuccess(Op op, OpData<T> opData);
  void onLoadError(Op op, Exception e);
}
