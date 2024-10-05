package com.app.cookbook.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.cookbook.MyApplication;
import com.app.cookbook.R;
import com.app.cookbook.activity.admin.AdminAddFoodActivity;
import com.app.cookbook.activity.admin.AdminFoodDetailActivity;
import com.app.cookbook.adapter.admin.AdminFoodAdapter;
import com.app.cookbook.constant.Constant;
import com.app.cookbook.constant.GlobalFunction;
import com.app.cookbook.databinding.FragmentAdminFoodBinding;
import com.app.cookbook.listener.IOnAdminManagerFoodListener;
import com.app.cookbook.model.Food;
import com.app.cookbook.utils.StringUtil;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class AdminFoodFragment extends Fragment {

    private FragmentAdminFoodBinding binding;
    private List<Food> mListFood;
    private AdminFoodAdapter mAdminFoodAdapter;
    private ChildEventListener mChildEventListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminFoodBinding.inflate(inflater, container, false);

        initView();
        initListener();
        loadListFood("");

        return binding.getRoot();
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.rcvFood.setLayoutManager(linearLayoutManager);
        mListFood = new ArrayList<>();
        mAdminFoodAdapter = new AdminFoodAdapter(mListFood, new IOnAdminManagerFoodListener() {
            @Override
            public void onClickUpdateFood(Food food) {
                onClickEditFood(food);
            }

            @Override
            public void onClickDeleteFood(Food food) {
                deleteFoodItem(food);
            }

            @Override
            public void onClickDetailFood(Food food) {
                goToFoodDetail(food);
            }
        });
        binding.rcvFood.setAdapter(mAdminFoodAdapter);
        binding.rcvFood.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    binding.btnAddFood.hide();
                } else {
                    binding.btnAddFood.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void initListener() {
        binding.btnAddFood.setOnClickListener(v -> onClickAddFood());

        binding.imgSearch.setOnClickListener(view1 -> searchFood());

        binding.edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchFood();
                return true;
            }
            return false;
        });

        binding.edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    searchFood();
                }
            }
        });
    }

    private void goToFoodDetail(@NonNull Food food) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.OBJECT_FOOD, food);
        GlobalFunction.startActivity(getActivity(), AdminFoodDetailActivity.class, bundle);
    }

    private void onClickAddFood() {
        GlobalFunction.startActivity(getActivity(), AdminAddFoodActivity.class);
    }

    private void onClickEditFood(Food food) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.OBJECT_FOOD, food);
        GlobalFunction.startActivity(getActivity(), AdminAddFoodActivity.class, bundle);
    }

    private void deleteFoodItem(Food food) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.msg_delete_title))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) -> {
                    if (getActivity() == null) return;
                    MyApplication.get(getActivity()).foodDatabaseReference()
                            .child(String.valueOf(food.getId())).removeValue((error, ref) ->
                                    Toast.makeText(getActivity(),
                                            getString(R.string.msg_delete_food_successfully),
                                            Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void searchFood() {
        String strKey = binding.edtSearchName.getText().toString().trim();
        resetListFood();
        if (getActivity() != null) {
            MyApplication.get(getActivity()).foodDatabaseReference()
                    .removeEventListener(mChildEventListener);
        }
        loadListFood(strKey);
        GlobalFunction.hideSoftKeyboard(getActivity());
    }

    private void resetListFood() {
        if (mListFood != null) {
            mListFood.clear();
        } else {
            mListFood = new ArrayList<>();
        }
    }

    public void loadListFood(String keyword) {
        if (getActivity() == null) return;
        mChildEventListener = new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Food food = dataSnapshot.getValue(Food.class);
                if (food == null || mListFood == null) return;
                if (StringUtil.isEmpty(keyword)) {
                    mListFood.add(0, food);
                } else {
                    if (GlobalFunction.getTextSearch(food.getName()).toLowerCase().trim()
                            .contains(GlobalFunction.getTextSearch(keyword).toLowerCase().trim())) {
                        mListFood.add(0, food);
                    }
                }
                if (mAdminFoodAdapter != null) mAdminFoodAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                Food food = dataSnapshot.getValue(Food.class);
                if (food == null || mListFood == null || mListFood.isEmpty()) return;
                for (int i = 0; i < mListFood.size(); i++) {
                    if (food.getId() == mListFood.get(i).getId()) {
                        mListFood.set(i, food);
                        break;
                    }
                }
                if (mAdminFoodAdapter != null) mAdminFoodAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Food food = dataSnapshot.getValue(Food.class);
                if (food == null || mListFood == null || mListFood.isEmpty()) return;
                for (Food foodObject : mListFood) {
                    if (food.getId() == foodObject.getId()) {
                        mListFood.remove(foodObject);
                        break;
                    }
                }
                if (mAdminFoodAdapter != null) mAdminFoodAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        MyApplication.get(getActivity()).foodDatabaseReference()
                .addChildEventListener(mChildEventListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null && mChildEventListener != null) {
            MyApplication.get(getActivity()).foodDatabaseReference()
                    .removeEventListener(mChildEventListener);
        }
    }
}
