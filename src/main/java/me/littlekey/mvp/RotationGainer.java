package me.littlekey.mvp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by littlekey on 15/8/13.
 */
public class RotationGainer<T> {

  private int index = 0;
  private ArrayList<T> mDataList;

  @SafeVarargs
  public RotationGainer(T... dataList) {
    mDataList = new ArrayList<>();
    Collections.addAll(mDataList, dataList);
  }

  public RotationGainer(List<T> dataList) {
    mDataList = new ArrayList<>(dataList);
  }

  public synchronized T obtain() {
    if (mDataList.size() == 0) {
      return null;
    }
    return mDataList.get(index++ % mDataList.size());
  }
  public synchronized void add (T data) {
    mDataList.add(data);
  }

  public synchronized T remove(int i) {
    return mDataList.remove(i);
  }

  public synchronized int size() {
    return mDataList.size();
  }
}
