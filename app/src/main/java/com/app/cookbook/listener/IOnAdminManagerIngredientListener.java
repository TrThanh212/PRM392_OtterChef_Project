package com.app.cookbook.listener;

public interface IOnAdminManagerIngredientListener
{
    void onClickDeleteIngredient(String ingredient);
    void onClickEditIngredient(int position, String ingredient);
}
