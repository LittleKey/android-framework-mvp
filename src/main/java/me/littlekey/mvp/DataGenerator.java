package me.littlekey.mvp;

import android.support.annotation.NonNull;

import com.android.volley.Response;
import me.littlekey.network.ApiRequest;

import java.util.List;

/**
 * Created by nengxiangzhou on 15/8/27.
 */
public interface DataGenerator<R, T> {

  void setListener(Response.Listener<R> listener);

  void setErrorListener(Response.ErrorListener errorListener);

  ApiRequest<R> onCreateRequest();

  ApiRequest<R> getNextRequestFromResponse(R response);

  boolean getHasMoreFromResponse(R response);

  List<T> getItemsFromResponse(@NonNull R response);
}
