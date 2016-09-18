package me.littlekey.mvp;

import java.util.List;

/**
 * Created by nengxiangzhou on 15/5/9.
 */
public interface DataProcessor<T> {
  List<T> generate(List<T> newData);
}
