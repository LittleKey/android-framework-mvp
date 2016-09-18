package me.littlekey.mvp;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Environment;

import me.littlekey.network.ImageConfig;
import me.littlekey.network.ImageManager;

import java.io.File;

/**
 * Created by nengxiangzhou on 15/9/7.
 */
public class BaseApplication extends Application {
  private static final int M = 1024 * 1024;
  private static final int BITMAP_FILE_CACHE_SIZE = 64 * M; // 64M
  private static final int SMALL_BITMAP_FILE_CACHE_SIZE = 16 * M; // 16M
  private static final float BITMAP_MEMORY_CACHE_SIZE_SCALE_BELOW_64 = 0.05f;
  private static final float BITMAP_MEMORY_CACHE_SIZE_SCALE_ABOVE_64 = 0.1f;
  private static final int IMAGE_NETWORK_THREAD_POOL_SIZE = 3;
  private ImageManager mImageManager;

  @Override
  public void onCreate() {
    super.onCreate();
    initializeImage();
  }

  public ImageManager getImageManager() {
    return mImageManager;
  }

  public void initializeImage() {
    if (mImageManager != null) {
      mImageManager.shutdown();
    }
    mImageManager = new ImageManager(newImageConfig());
  }

  protected ImageConfig newImageConfig() {
    return new ImageConfig() {
      private File mCacheFile;

      @Override
      public File getFileCacheDir() {
        if (mCacheFile == null) {
          mCacheFile = createCacheFile();
        }
        return mCacheFile;
      }

      @Override
      public int getFileCacheSize() {
        return BITMAP_FILE_CACHE_SIZE;
      }

      @Override
      public int getThreadPoolSize() {
        return IMAGE_NETWORK_THREAD_POOL_SIZE;
      }

      @Override
      public int getMemoryCacheSize() {
        int memoryClass =
            ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        return memoryClass <= 64
            ? Math.round(memoryClass * M * BITMAP_MEMORY_CACHE_SIZE_SCALE_BELOW_64)
            : Math.round(memoryClass * M * BITMAP_MEMORY_CACHE_SIZE_SCALE_ABOVE_64);
      }

      @Override
      public int getMemoryCacheEntries() {
        return 50;
      }

      @Override
      public Context getContext() {
        return BaseApplication.this;
      }

      @Override
      public int getSmallFileCacheSize() {
        return SMALL_BITMAP_FILE_CACHE_SIZE;
      }
    };
  }

  private File createCacheFile() {
    File cacheDir = null;
    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
      cacheDir = this.getExternalCacheDir();
    }
    if (cacheDir == null) {
      cacheDir = this.getCacheDir();
    }
    return cacheDir;
  }
}
