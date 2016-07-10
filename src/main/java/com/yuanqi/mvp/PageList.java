package com.yuanqi.mvp;

import android.support.annotation.NonNull;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.yuanqi.base.utils.LinkedHashTreeSet;
import com.yuanqi.mvp.DataLoadObserver.Op;
import com.yuanqi.mvp.DataLoadObserver.OpData;
import com.yuanqi.network.ApiRequest;
import com.yuanqi.network.RequestStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nengxiangzhou on 15/5/9.
 */
public class PageList<R, T> extends DataList<T>
    implements Response.Listener<R>, Response.ErrorListener {

  public static class PageInfo<R> {
    ApiRequest<R> request;
    boolean ongoing = false;
    boolean clearData = false;
    boolean hasMore = true;
    RequestStatus status = RequestStatus.NOT_READY;

    public boolean ongoing() {
      return ongoing;
    }

    public Op nextOp() {
      return clearData ? Op.REFRESH : Op.ADD;
    }
  }

  private PageInfo<R> mNextPage;
  private List<T> mProcessedItems;
  private DataGenerator<R, T> mDataGenerator;

  public PageList(DataGenerator<R, T> dataGenerator) {
    super();
    mNextPage = new PageInfo<>();
    mProcessedItems = new ArrayList<>();
    mDataGenerator = dataGenerator;
    mDataGenerator.setListener(this);
    mDataGenerator.setErrorListener(this);
    setProcessor(new DataProcessor<T>() {
      @Override
      public List<T> generate(List<T> newData) {
        LinkedHashTreeSet<T> list = new LinkedHashTreeSet<>();
        if (newData != null) {
          list.addAll(newData);
        }
        LinkedHashTreeSet.LinkedTreeIterator<T> iterator = list.listIterator();
        while (iterator.hasNext()) {
          if (mProcessedItems.contains(iterator.next())) {
            iterator.remove();
          }
        }
        return list;
      }
    });
  }

  @Override
  public List<T> getItems() {
    return mProcessedItems;
  }

  @Override
  public T getItem(int position) {
    return mProcessedItems.get(position);
  }

  @Override
  public boolean hasMore() {
    return mNextPage.hasMore;
  }

  @Override
  protected void doLoadMore() {
    loadData(false);
  }

  @Override
  protected void doRefresh() {
    loadData(true);
  }

  private ApiRequest<R> onCreateRequest() {
    return mDataGenerator.onCreateRequest();
  }

  protected void onRequestCreated(ApiRequest<R> request, boolean clearData) {}

  private ApiRequest<R> getNextRequestFromResponse(R response) {
    return mDataGenerator.getNextRequestFromResponse(response);
  }

  private boolean getHasMoreFromResponse(R response) {
    return mDataGenerator.getHasMoreFromResponse(response);
  }

  private List<T> getItemsFromResponse(@NonNull R response) {
    return mDataGenerator.getItemsFromResponse(response);
  }

  private void loadData(boolean clearData) {
    if (mNextPage.ongoing()) {
      return;
    }
    if (!clearData && !hasMore()) {
      return;
    }
    mNextPage.clearData = clearData;
    if (clearData) {
      mNextPage.request = null;
    }
    if (mNextPage.request == null) {
      mNextPage.request = onCreateRequest();
    }
    mNextPage.ongoing = true;
    onRequestCreated(mNextPage.request, clearData);
    if (mNextPage.request == null) {
      mNextPage.hasMore = false;
      return;
    }
    mNextPage.status = RequestStatus.ONGOING;
    notifyLoadStart(mNextPage.nextOp());
    mNextPage.request.submit();
  }

  @Override
  public final void onErrorResponse(VolleyError error) {
    if (mNextPage.status == RequestStatus.HIT_CACHE_AND_NEED_REFRESH) {
      mNextPage.status = RequestStatus.HIT_CACHE_AND_LOAD_FAIL;
    } else {
      mNextPage.status = RequestStatus.MISS_CACHE_AND_LOAD_FAIL;
    }
    notifyLoadError(mNextPage.nextOp(), error);
    mNextPage.request = null;
    mNextPage.ongoing = false;
    mNextPage.clearData = false;
  }

  @Override
  public final void onResponse(R response) {
    if (response == null) {
      onErrorResponse(new VolleyError("No response."));
      return;
    }
    final PageInfo<R> currentPage = mNextPage;

    if (currentPage.status == RequestStatus.HIT_CACHE_AND_NEED_REFRESH) {
      currentPage.status = RequestStatus.HIT_CACHE_AND_LOAD_SUCCESS;
    } else if (currentPage.request.getCacheEntry() == null) {
      currentPage.status = RequestStatus.MISS_CACHE_AND_LOAD_SUCCESS;
    } else if (currentPage.request.getCacheEntry().isExpired()) {
      currentPage.status = RequestStatus.MISS_CACHE_AND_LOAD_SUCCESS;
    } else if (currentPage.request.getCacheEntry().refreshNeeded()) {
      currentPage.status = RequestStatus.HIT_CACHE_AND_NEED_REFRESH;
    }
    Op op = currentPage.nextOp();

    if (op == Op.REFRESH) {
      mProcessedItems.clear();
    }
    List<T> newItems = getItemsFromResponse(response);
    List<T> newProcessedItems = processItems(newItems);
    T lastItem = mProcessedItems.isEmpty() ? null : mProcessedItems.get(mProcessedItems.size() - 1);
    OpData<T> opData = new OpData<>(mProcessedItems.size(), null, lastItem, newProcessedItems);
    mProcessedItems.addAll(newProcessedItems);
    notifyLoadSuccess(op, opData);
    if (currentPage.status == RequestStatus.HIT_CACHE_AND_NEED_REFRESH) {
      if (!currentPage.clearData) {
        currentPage.clearData = true;
        notifyLoadStart(currentPage.nextOp());
      }
    } else {
      mNextPage = new PageInfo<>();
      mNextPage.request = getNextRequestFromResponse(response);
      mNextPage.hasMore = getHasMoreFromResponse(response);
    }
  }
}
