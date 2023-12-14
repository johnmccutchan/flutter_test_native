package io.flutter.flutter_test_native;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.flutter_test_native.Messages.SelectorMessage;
import android.util.Log;
import android.os.SystemClock;
import java.util.ArrayList;
import android.view.MotionEvent;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

/** FlutterTestNativePlugin */
public class FlutterTestNativePlugin implements FlutterPlugin, Messages.FlutterTestNativeApi, ActivityAware {
  private static final String TAG = "FlutterTestNativePlugin";

  private Activity attachedActivity;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
    Messages.FlutterTestNativeApi.setUp(binding.getBinaryMessenger(), this);
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    Messages.FlutterTestNativeApi.setUp(binding.getBinaryMessenger(), null);
  }

  @Override
  public void	onAttachedToActivity​(ActivityPluginBinding binding) {
    Log.e(TAG, "onAttachedToActivity​");
    attachedActivity = binding.getActivity();
  }

  @Override
  public void	onDetachedFromActivity() {
    attachedActivity = null;
  }

  @Override
  public void	onDetachedFromActivityForConfigChanges() {
    attachedActivity = null;
  }

  @Override
  public void	onReattachedToActivityForConfigChanges​(ActivityPluginBinding binding) {
    Log.e(TAG, "onReattachedToActivityForConfigChanges​");
    attachedActivity = binding.getActivity();
  }

  protected View getRootView() {
    if (attachedActivity == null) {
      return null;
    }
    return attachedActivity.getWindow().getDecorView().getRootView();
  }

  protected ArrayList<View> findViewWithText(String text) {
    View rootView = this.getRootView();
    ArrayList<View> views = new ArrayList<View>();
    if (rootView == null) {
      return views;
    }
    rootView.findViewsWithText(views, text, View.FIND_VIEWS_WITH_TEXT);
    return views;
  }

  protected ArrayList<View> findViewById(int id) {
    View rootView = this.getRootView();
    ArrayList<View> views = new ArrayList<View>();
    if (rootView == null) {
      return views;
    }
    View foundView = rootView.findViewById(id);
    if (foundView != null) {
      views.add(foundView);
    }
    return views;
  }

  private void findViewByClass(ArrayList<View> views, ViewGroup group, String className) {
    final int n = group.getChildCount();
    for (int i = 0; i < n; i++) {
      View view = group.getChildAt(i);
      if (view.getClass().getCanonicalName().equals(className)) {
        views.add(view);
      }
      if (view instanceof ViewGroup) {
        findViewByClass(views, (ViewGroup) view, className);
      }
    }
  }

  protected ArrayList<View> findViewByClass(String className) {
    View rootView = this.getRootView();
    ArrayList<View> views = new ArrayList<View>();
    if (rootView == null) {
      return views;
    }
    // Check the root view.
    if (rootView.getClass().getCanonicalName().equals(className)) {
      views.add(rootView);
    }
    if (rootView instanceof ViewGroup) {
      // Descend the tree.
      findViewByClass(views, (ViewGroup) rootView, className);
    }
    return views;
  }

  protected ArrayList<View> findViewWithFocus() {
    ArrayList<View> views = new ArrayList<View>();
    if (attachedActivity == null) {
      return views;
    }
    View view = attachedActivity.getCurrentFocus();
    if (view != null) {
      views.add(view);
    }
    return views;
  }

  protected View findViewBySelector(@NonNull Messages.SelectorMessage selector) {
    // Default to index 0.
    int index = selector.getIndex() != null ? selector.getIndex().intValue() : 0;
    ArrayList<View> views = null;
    if (selector.getContainsText() != null) {
      views = findViewWithText(selector.getContainsText());
    } else if (selector.getClassName() != null) {
      views = findViewByClass(selector.getClassName());
    } else if (selector.getId() != null) {
      views = findViewById(selector.getId().intValue());
    } else if (selector.getFocused() != null && selector.getFocused()) {
      views = findViewWithFocus();  
    }
    if (views == null || index < 0 || index >= views.size()) {
      // Index out of range.
      return null;
    }
    return views.get(index);
  }
  @Override
  public void tap(@NonNull Messages.SelectorMessage selector, @NonNull Messages.Result<Void> result) {
    View view = findViewBySelector(selector);
    if (view == null) {
      result.error(new Exception("Could not find view that matched selector."));
      return;
    }
    float x = view.getX() + (float)(view.getWidth() / 2);
    float y = view.getY() + (float)(view.getHeight() / 2);
    long downTime = SystemClock.uptimeMillis();
    MotionEvent down = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0);
    MotionEvent up = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_UP, x, y, 0);
    view.dispatchTouchEvent(down);
    view.dispatchTouchEvent(up);
    result.success(null);
  }
}

