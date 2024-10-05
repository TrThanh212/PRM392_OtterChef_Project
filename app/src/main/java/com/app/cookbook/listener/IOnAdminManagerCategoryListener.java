package com.app.cookbook.listener;

import com.app.cookbook.model.Category;

public interface IOnAdminManagerCategoryListener {
    void onClickUpdateCategory(Category category);
    void onClickDeleteCategory(Category category);
    void onClickDetailCategory(Category category);
}
