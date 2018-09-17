/*
 * Copyright (c) 2017-2018 PlayerOne.
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.example.jeliu.eos.ui.base;


import android.app.Activity;
import android.content.Context;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;

import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

/**
 * Created by swapnibble on 2017-11-10.
 */

public class RxCallbackWrapper<T> extends DisposableObserver<T> {

    private WeakReference<Context> mPresenterRef;

    public RxCallbackWrapper(Context context) {
        mPresenterRef = new WeakReference<>(context);
    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable e) {

        Context presenter = mPresenterRef.get();
        if (null == presenter) {
            return;
        }
        if (e instanceof HttpException) {
            ResponseBody responseBody = ((HttpException) e).response().errorBody();
        } else if (e instanceof SocketTimeoutException) {
        } else if (e instanceof IOException) {
        } else {
            e.printStackTrace();
        }
    }

    @Override
    public void onComplete() {
        Context presenter = mPresenterRef.get();
        if (null == presenter) {
            return;
        }
    }

    private String getErrorMessage(ResponseBody responseBody) {
        try {
            return responseBody.string();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
