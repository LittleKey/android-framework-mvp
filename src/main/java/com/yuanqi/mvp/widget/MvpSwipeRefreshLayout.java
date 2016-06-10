package com.yuanqi.mvp.widget;


import android.content.Context;
import android.support.v4.widget.BaseSwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.yuanqi.mvp.DataLoadObserver;
import com.yuanqi.mvp.R;
import com.yuanqi.mvp.adapter.HeaderFooterAdapter.ViewData;
import com.yuanqi.mvp.adapter.MvpAdapter;
import com.yuanqi.mvp.presenter.Presenter;
import com.yuanqi.mvp.presenter.ViewGroupPresenter;


/**
 * This view is used to swipe refresh and set load more animation.
 * Created by nengxiangzhou on 15/5/12.
 */
public class MvpSwipeRefreshLayout<T> extends BaseSwipeRefreshLayout
    implements DataLoadObserver<T> {
  private MvpRecyclerView mRecyclerView;
  private MvpAdapter<T> mAdapter;
  private ViewData mLoadMoreFooter;

  public MvpSwipeRefreshLayout(Context context) {
    super(context);
  }

  public MvpSwipeRefreshLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    mRecyclerView = (MvpRecyclerView) findViewById(R.id.recycler);
    setOnRefreshListener(new OnRefreshListener() {
      @Override
      public void onRefresh() {
        if (mAdapter != null && mAdapter.getList() != null) {
          mAdapter.getList().refresh();
        }
      }
    });
  }

  public void setAdapter(MvpAdapter<T> adapter) {
    mLoadMoreFooter = new ViewData() {
      @Override
      public ViewGroupPresenter createPresenter(ViewGroup parent) {
        return new ViewGroupPresenter(parent, R.layout.footer_loading_more)
            .add(R.id.loading, new Presenter() {
              @Override
              public void bind(Object model) {
                if (view() instanceof LoadingView) {
                  ((LoadingView) view()).start();
                }
              }

              @Override
              public void unbind() {
                if (view() instanceof LoadingView) {
                  ((LoadingView) view()).stop();
                }
              }
            });
      }
    };
    mLoadMoreFooter.setVisible(true);
    adapter.addFooter(mLoadMoreFooter);
    mAdapter = adapter;
    mRecyclerView.setAdapter(adapter);
  }

  @Override
  public void onLoadStart(Op op) {
    updateLoadingStatus(op, true);
  }

  @Override
  public void onLoadSuccess(Op op, OpData<T> opData) {
    switch (op) {
      case REFRESH:
        mRecyclerView.scrollToPosition(0);
        break;
      case ADD:
        if (opData.position == 0) {
          mRecyclerView.scrollToPosition(0);
        }
        break;
    }
    updateLoadingStatus(op, false);
  }

  @Override
  public void onLoadError(Op op, Exception e) {
    updateLoadingStatus(op, false);
  }

  private void updateLoadingStatus(Op op, boolean loading) {
    switch (op) {
      case REFRESH:
        if (loading != isRefreshing()) {
          setRefreshing(loading);
        }
        break;
      case ADD:
        if (isRefreshing()) {
          break;
        }
        setLoadMore(loading);
    }
  }

  private void setLoadMore(boolean loadMore) {
    if (mAdapter == null || mAdapter.getList() == null || mLoadMoreFooter == null) {
      return;
    }
    if (!mAdapter.getList().isLoading() && loadMore) {
      return;
    }
    mLoadMoreFooter.setVisible(loadMore);
  }
}
