package com.zayn.loadingview.library;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Created by zhou on 16-4-22.
 */
@Retention(CLASS)
@Target({TYPE})
public @interface LoadingBehavior {

    Class<? extends IBehavior> value();
}