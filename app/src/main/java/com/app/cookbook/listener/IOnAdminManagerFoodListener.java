package com.app.cookbook.listener;

import com.app.cookbook.model.Food;

public interface IOnAdminManagerFoodListener {
    void onClickUpdateFood(Food food);
    void onClickDeleteFood(Food food);
    void onClickDetailFood(Food food);

}
