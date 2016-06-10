package com.yuanqi.mvp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.yuanqi.mvp.presenter.ViewGroupPresenter;
import com.yuanqi.mvp.widget.MvpRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * This adapter is used for add header and footer.<br/>
 * Notice that you can only add header or footer, but not delete them. You can call
 * setVisible(false) to hide them.<br/>
 * Created by nengxiangzhou on 15/5/12.
 */
public abstract class HeaderFooterAdapter<T> extends MvpRecyclerView.Adapter<T> {
  private static final int HEADER_VIEW_TYPE_FLAG = 0x1 << 14;
  private static final int FOOTER_VIEW_TYPE_FLAG = 0x2 << 14;
  private static final int VIEW_TYPE_FILTER = (0x1 << 14) - 1;
  private static final int FLAG_FILTER = 0x3 << 14;

  private final ViewDataAdapter mHeaderAdapter;
  private final ViewDataAdapter mFooterAdapter;

  private ViewData mEmptyView;

  protected HeaderFooterAdapter() {
    mHeaderAdapter = new ViewDataAdapter();
    mFooterAdapter = new ViewDataAdapter();
    mHeaderAdapter.registerAdapterDataObserver(new HeaderFooterDataObserver(new PositionMapping() {
      @Override
      public int mapPosition(int position) {
        return position;
      }
    }));
    mFooterAdapter.registerAdapterDataObserver(new HeaderFooterDataObserver(new PositionMapping() {
      @Override
      public int mapPosition(int position) {
        return getHeaderCount() + getDataCount() + position;
      }
    }));
    registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
      @Override
      public void onChanged() {
        checkIfEmpty();
      }

      public void onItemRangeChanged(int positionStart, int itemCount) {
        checkIfEmpty();
      }

      @Override
      public void onItemRangeInserted(int positionStart, int itemCount) {
        checkIfEmpty();
      }

      @Override
      public void onItemRangeRemoved(int positionStart, int itemCount) {
        checkIfEmpty();
      }
    });
  }

  public static class CheckEmptyListener {
    protected boolean onCheckEmpty(ViewData emptyView, int headerCount, int dataCount,
        int footerCount) {
      return dataCount == 0 && footerCount == 0 &&
          headerCount == (emptyView.visible ? 1 : 0);
    }
  }

  private CheckEmptyListener mEmptyCheckListener = new CheckEmptyListener();

  public void setOnCheckEmptyListener(@NonNull CheckEmptyListener listener) {
    mEmptyCheckListener = listener;
  }

  void checkIfEmpty() {
    if (mEmptyView == null) {
      return;
    }
    final boolean isEmpty = mEmptyCheckListener.onCheckEmpty(mEmptyView,
        getHeaderCount(), getDataCount(), getFooterCount());
    if (mEmptyView.visible != isEmpty) {
      mEmptyView.setVisible(isEmpty);
    }
  }

  public boolean emptyViewIsShown() {
    return mEmptyView != null && mEmptyView.visible;
  }

  public void addHeader(ViewData viewData) {
    mHeaderAdapter.addDada(viewData);
  }

  public void addFooter(ViewData viewData) {
    mFooterAdapter.addDada(viewData);
  }

  public void setEmptyView(ViewData emptyView) {
    if (mEmptyView != emptyView) {
      if (mEmptyView != null) {
        mEmptyView.setVisible(false);
        mHeaderAdapter.removeData(mEmptyView);
      }
      if (emptyView != null) {
        emptyView.setVisible(false);
        addHeader(emptyView);
      }
      mEmptyView = emptyView;
    }
  }

  public int getHeaderCount() {
    return mHeaderAdapter.getItemCount();
  }

  public int getFooterCount() {
    return mFooterAdapter.getItemCount();
  }

  public int getDataCount() {
    return data == null ? 0 : data.size();
  }

  protected final boolean isHeader(int position) {
    final int finalPosition = positionHeader(position);
    return finalPosition >= 0 && finalPosition < getHeaderCount();
  }

  protected final boolean isData(int position) {
    final int finalPosition = positionData(position);
    return finalPosition >= 0 && finalPosition < getDataCount();
  }

  protected final boolean isFooter(int position) {
    final int finalPosition = positionFooter(position);
    return finalPosition >= 0 && finalPosition < getFooterCount();
  }

  protected final int positionHeader(int position) {
    return position;
  }

  protected final int positionData(int position) {
    return position - getHeaderCount();
  }

  protected final int positionFooter(int position) {
    return position - getHeaderCount() - getDataCount();
  }

  @Override
  protected int mapPosition(int position) {
    return position + getHeaderCount();
  }

  @Override
  public int getItemCount() {
    return getHeaderCount() + getFooterCount() + getDataCount();
  }

  @Override
  public void onBindViewHolder(MvpRecyclerView.ViewHolder holder, int position) {
    if (isHeader(position)) {
      mHeaderAdapter.onBindViewHolder(holder, positionHeader(position));
    } else if (isData(position)) {
      onBindDataViewHolder(holder, positionData(position));
    } else {
      mFooterAdapter.onBindViewHolder(holder, positionFooter(position));
    }
  }

  @Override
  protected ViewGroupPresenter onCreateViewPresenter(ViewGroup parent, int viewType) {
    final int innerViewType = viewType & VIEW_TYPE_FILTER;
    final int flag = viewType & FLAG_FILTER;
    switch (flag) {
      case HEADER_VIEW_TYPE_FLAG:
        return mHeaderAdapter.onCreateViewPresenter(parent, innerViewType);
      case FOOTER_VIEW_TYPE_FLAG:
        return mFooterAdapter.onCreateViewPresenter(parent, innerViewType);
      default:
        return onCreateDataViewPresenter(parent, viewType);
    }
  }

  @Override
  public void onViewAttachedToWindow(MvpRecyclerView.ViewHolder holder) {
    super.onViewAttachedToWindow(holder);
  }

  @Override
  public void onViewDetachedFromWindow(MvpRecyclerView.ViewHolder holder) {
    super.onViewDetachedFromWindow(holder);
  }

  @Override
  public int getItemViewType(int position) {
    if (isHeader(position)) {
      return HEADER_VIEW_TYPE_FLAG | mHeaderAdapter.getItemViewType(positionHeader(position));
    } else if (isData(position)) {
      return getDataItemViewType(positionData(position));
    } else {
      return FOOTER_VIEW_TYPE_FLAG | mFooterAdapter.getItemViewType(positionFooter(position));
    }
  }

  private void onBindDataViewHolder(MvpRecyclerView.ViewHolder holder, int position) {
    super.onBindViewHolder(holder, position);
  }

  protected abstract ViewGroupPresenter onCreateDataViewPresenter(ViewGroup parent, int ViewType);

  public abstract int getDataItemViewType(int position);

  public interface ViewDataObserver {
    void onDataChanged(ViewData viewData);

    void onVisibilityChanged(ViewData viewData);
  }

  private interface PositionMapping {
    int mapPosition(int position);
  }

  private class HeaderFooterDataObserver extends RecyclerView.AdapterDataObserver {
    private PositionMapping mapping;

    public HeaderFooterDataObserver(PositionMapping mapping) {
      this.mapping = mapping;
    }

    @Override
    public void onChanged() {
      notifyDataSetChanged();
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
      if (itemCount == 1) {
        notifyItemChanged(mapping.mapPosition(positionStart));
      } else {
        notifyItemRangeChanged(mapping.mapPosition(positionStart), itemCount);
      }
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
      if (itemCount == 1) {
        notifyItemInserted(mapping.mapPosition(positionStart));
      } else {
        notifyItemRangeInserted(mapping.mapPosition(positionStart), itemCount);
      }
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
      if (itemCount == 1) {
        notifyItemRemoved(mapping.mapPosition(positionStart));
      } else {
        notifyItemRangeRemoved(mapping.mapPosition(positionStart), itemCount);
      }
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
      if (itemCount == 1) {
        notifyItemMoved(mapping.mapPosition(fromPosition), mapping.mapPosition(toPosition));
      } else {
        notifyDataSetChanged();
      }
    }
  }

  public static abstract class ViewData {
    private Object data;
    private boolean visible;
    private int position;
    private ViewDataObserver observer;

    public abstract ViewGroupPresenter createPresenter(ViewGroup parent);

    public void onViewAttachedToWindow() {}

    public void onViewDetachedFromWindow() {}

    public void setVisible(boolean visible) {
      if (this.visible != visible) {
        this.visible = visible;
        if (observer != null) {
          observer.onVisibilityChanged(this);
        }
      }
    }

    public void setData(Object data) {
      if (this.data == null || this.data.equals(data)) {
        this.data = data;
        if (observer != null) {
          observer.onDataChanged(this);
        }
      }
    }

    public void setViewDataObserver(ViewDataObserver observer) {
      this.observer = observer;
    }
  }

  public static class ViewDataAdapter extends MvpRecyclerView.Adapter<ViewData>
      implements
        ViewDataObserver {
    private List<ViewData> allData;

    public ViewDataAdapter() {
      allData = new ArrayList<>();
    }

    @Override
    protected ViewGroupPresenter onCreateViewPresenter(ViewGroup parent, int viewType) {
      return getItem(viewType).createPresenter(parent);
    }

    @Override
    public void onViewAttachedToWindow(MvpRecyclerView.ViewHolder holder) {
      getItem(holder.getAdapterPosition()).onViewAttachedToWindow();
    }

    @Override
    public void onViewDetachedFromWindow(MvpRecyclerView.ViewHolder holder) {
      getItem(holder.getAdapterPosition()).onViewDetachedFromWindow();
    }

    @Override
    public int getItemViewType(int position) {
      return position;
    }

    public void addDada(ViewData viewData) {
      viewData.position = allData.size();
      viewData.setViewDataObserver(this);
      allData.add(viewData);
      if (viewData.visible) {
        appendData(viewData);
      }
    }

    @Override
    public void onDataChanged(ViewData viewData) {
      if (data == null) {
        return;
      }
      for (int i = 0; i < data.size(); i++) {
        if (data.get(i).position == viewData.position) {
          changeData(i, viewData);
        }
      }
    }

    @Override
    public void onVisibilityChanged(ViewData viewData) {
      if (viewData.visible) {
        int position = 0;
        for (int i = 0; i < allData.size(); i++) {
          if (allData.get(i).position == viewData.position) {
            break;
          }
          if (allData.get(i).visible) {
            position++;
          }
        }
        insertData(position, viewData);
      } else {
        removeData(viewData);
      }
    }
  }
}
