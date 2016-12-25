package me.littlekey.mvp;

import android.support.v7.widget.RecyclerView;

import me.littlekey.mvp.widget.MvpRecyclerView;

/**
 * Created by nengxiangzhou on 15/7/22.
 */
public class PageContext {
  public RecyclerView.LayoutManager layoutManager;
  public MvpRecyclerView.Adapter adapter;
  public String userName;
  public String favoriteId;
}
