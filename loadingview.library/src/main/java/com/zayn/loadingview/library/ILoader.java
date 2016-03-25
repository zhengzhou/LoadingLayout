package com.zayn.loadingview.library;

/**
 * loader at begin load.
 *
 * Created by zhou on 16-3-24.
 */
public interface ILoader {

    /**
     * show loading animation.(hide data content view and others)
     */
    void startLoading();

    /**
     * stop loading animation. show data content.
     */
    void stopLoading();

    /**
     * stop loading animation. show error content.
     */
    void error();

    /**
     * stop loading animation. show empty view.
     */
    void empty();

}
