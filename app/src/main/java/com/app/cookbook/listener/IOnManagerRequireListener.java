package com.app.cookbook.listener;

import com.app.cookbook.model.RequestFood;

public interface IOnManagerRequireListener {
    void onClickUpdateRequire(RequestFood requestFood);
    void onClickDeleteRequire(RequestFood requestFood);
}
