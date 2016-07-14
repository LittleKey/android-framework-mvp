package com.yuanqi.mvp.adapter;

import android.os.Handler;

import com.yuanqi.base.utils.CollectionUtils;
import com.yuanqi.mvp.DataList;
import com.yuanqi.mvp.DataLoadObserver;
import com.yuanqi.mvp.widget.MvpRecyclerView;


/**
 * Created by nengxiangzhou on 15/5/13.
 */
public abstract class MvpAdapter<T> extends HeaderFooterAdapter<T>
    implements DataLoadObserver<T> {
  private static final int DEFAULT_PRELOAD_OFFSET = 5;
  protected DataList<T> mList;
  private Handler mHandler;

  public MvpAdapter() {
    mHandler = new Handler();
  }

  @Override
  public void onViewAttachedToWindow(final MvpRecyclerView.ViewHolder holder) {
    super.onViewAttachedToWindow(holder);
    if (!emptyViewIsShown()) {
      mHandler.post(new Runnable() {
        @Override
        public void run() {
          tryPreLoad(holder.getAdapterPosition(), getItemCount());
        }
      });
    }
  }

  public DataList<T> getList() {
    return mList;
  }

  public void setList(DataList<T> list) {
    this.mList = list;
    syncAll();
    if (CollectionUtils.isEmpty(list.getItems())) {
      list.loadMore();
    }
  }

  private void syncAll() {
    setData(mList.getItems());
  }

  public void tryPreLoad(int position, int totalPosition) {
    if (mList != null && mList.hasMore()) {
      int offset = totalPosition - position - 1;
      if (offset == DEFAULT_PRELOAD_OFFSET || offset == 0) {
        mList.loadMore();
      }
    }
  }

  @Override
  public void onLoadStart(Op op) {

  }

  @Override
  public void onLoadSuccess(Op op, OpData<T> opData) {
    switch (op) {
      case ADD:
        appendData(opData.newData);
        break;
      case REFRESH:
        syncAll();
        break;
      default:
        break;
    }
  }

  @Override
  public void onLoadError(Op op, Exception e) {

  }
}
