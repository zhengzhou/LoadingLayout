package com.zayn.loadingview.library;

import android.view.View;

/**
 * Created by zhou on 16-4-6.
 */
public interface IBehavior {

    void onStartOffset(View view, int offset);

    boolean startContentMove();

    void onEndOffset(View view, int offset);

    boolean endContentMove();
}
