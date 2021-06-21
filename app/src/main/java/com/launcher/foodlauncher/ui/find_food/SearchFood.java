package com.launcher.foodlauncher.ui.find_food;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.launcher.foodlauncher.R;
import com.launcher.foodlauncher.api.ApiComplexSearchInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchFood extends BackableFragment {

    private Context context;
    private TextView textViewResult;

    String tagFindFood = "FindFoodFragmentTag";
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    private Button cuisineBtn, dietBtn, intolerancesBtn, mealBtn;
    private ImageButton btnFind;

    private EditText query, exclude, maxFat, maxCalories;
    private StringBuilder meal, cuisine, diet, intolerances;
    RecyclerView recyclerCuisine, recyclerDiet, recyclerMeal, recyclerInt, recycleFood;

    BottomSheetBehavior foodBehaviour;

    StringBuilder querySearch, excludeIngredients, maxfat, maxcalories;

    private final String apiKey = "2bd5395fcb6441b3a37139d3b0fab4eb";

    public SearchFood(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_search_food, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        cuisineBtn = getView().findViewById(R.id.cuisine_btn);
        dietBtn = getView().findViewById(R.id.diet_btn);
        intolerancesBtn = getView().findViewById(R.id.intolerances_btn);
        mealBtn = getView().findViewById(R.id.meal_btn);
        btnFind = getView().findViewById(R.id.btn_find);

        query = getView().findViewById(R.id.query);
        maxCalories = getView().findViewById(R.id.max_calories);
        maxFat = getView().findViewById(R.id.max_fat);
        exclude = getView().findViewById(R.id.exclude);

        meal = new StringBuilder();
        diet = new StringBuilder();
        intolerances = new StringBuilder();
        cuisine = new StringBuilder();
        querySearch = new StringBuilder();
        excludeIngredients = new StringBuilder();
        maxfat = new StringBuilder();
        maxcalories = new StringBuilder();

        if(savedInstanceState != null) {
            Log.i("SearchFood", "onActivityCreated: Instance saved!");
            meal.replace(0, meal.length(), savedInstanceState.getString("meal"));
            diet.replace(0, diet.length(), savedInstanceState.getString("diet"));
            intolerances.replace(0, intolerances.length(), savedInstanceState.getString("intolerances"));
            cuisine.replace(0, cuisine.length(), savedInstanceState.getString("cuisine"));
            querySearch.replace(0, querySearch.length(), savedInstanceState.getString("querySearch"));
            excludeIngredients.replace(0, excludeIngredients.length(), savedInstanceState.getString("excludeIngredients"));
            maxfat.replace(0, maxfat.length(), savedInstanceState.getString("maxfat"));
            maxcalories.replace(0, maxcalories.length(), savedInstanceState.getString("maxcalories"));

            searchFood(querySearch.toString(), cuisine.toString(), diet.toString(), meal.toString(), intolerances.toString()
                    , excludeIngredients.toString(), maxfat.toString(), maxcalories.toString());
        }

        BottomSheetBehavior mealBehaviour, dietBehaviour, cuisineBehaviour, intBehaviour;
        FrameLayout mealBottom = getView().findViewById(R.id.bottom_input_meal);
        mealBehaviour = BottomSheetBehavior.from(mealBottom);
        FrameLayout intBottom = getView().findViewById(R.id.bottom_input_intolerances);
        intBehaviour = BottomSheetBehavior.from(intBottom);
        FrameLayout cuisineBottom = getView().findViewById(R.id.bottom_input_cuisine);
        cuisineBehaviour = BottomSheetBehavior.from(cuisineBottom);
        FrameLayout dietBottom = getView().findViewById(R.id.bottom_input_diet);
        dietBehaviour = BottomSheetBehavior.from(dietBottom);
        FrameLayout foodBottom = getView().findViewById(R.id.bottom_complex_sheet);
        foodBehaviour = BottomSheetBehavior.from(foodBottom);

        String[] cuisineArray = getResources().getStringArray(R.array.cuisines);
        String[] dietArray = getResources().getStringArray(R.array.diets);
        String[] intolerancesArray = getResources().getStringArray(R.array.intolerances);
        String[] mealArray = getResources().getStringArray(R.array.meal_types);

        recyclerMeal = getView().findViewById(R.id.recycle_meal);
        recyclerMeal.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerDiet = getView().findViewById(R.id.recycle_diet);
        recyclerDiet.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerInt = getView().findViewById(R.id.recycle_intolerances);
        recyclerInt.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerCuisine = getView().findViewById(R.id.recycle_cuisine);
        recyclerCuisine.setLayoutManager(new LinearLayoutManager(getContext()));

        ListAdapterRadio mealAdapter = new ListAdapterRadio(mealArray, R.layout.radiobutton, getContext());
        ListAdapterRadio dietAdapter = new ListAdapterRadio(dietArray, R.layout.radiobutton, getContext());
        ListAdapterCheck cuisineAdapter = new ListAdapterCheck(cuisineArray, R.layout.checkbox, getContext());
        ListAdapterCheck intAdapter = new ListAdapterCheck(intolerancesArray, R.layout.checkbox, getContext());

        recyclerCuisine.setAdapter(cuisineAdapter);
        recyclerInt.setAdapter(intAdapter);
        recyclerDiet.setAdapter(dietAdapter);
        recyclerMeal.setAdapter(mealAdapter);

        mealBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mealBehaviour.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                dietBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                intBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                cuisineBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        dietBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dietBehaviour.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                mealBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                intBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                cuisineBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        intolerancesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intBehaviour.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                mealBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                dietBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                cuisineBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        cuisineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cuisineBehaviour.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                mealBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                dietBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
                intBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        query.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hideSoftKeyboard(query);
            }
        });
        exclude.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hideSoftKeyboard(exclude);
            }
        });
        maxFat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hideSoftKeyboard(maxFat);
            }
        });
        maxCalories.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hideSoftKeyboard(maxCalories);
            }
        });

        exclude.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT || event.getAction() == KeyEvent.KEYCODE_ENTER || event.getAction() == KeyEvent.ACTION_DOWN) {
                    maxCalories.requestFocus();
                }
                return false;
            }
        });

        maxCalories.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_NEXT || event.getAction() == KeyEvent.KEYCODE_ENTER || event.getAction() == KeyEvent.ACTION_DOWN) {
                    maxFat.requestFocus();
                }
                return false;
            }
        });

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    StringBuilder querySearch = new StringBuilder();
                    StringBuilder excludeIngredients = new StringBuilder();
                    excludeIngredients.append("null");
                    StringBuilder maxfat = new StringBuilder();
                    StringBuilder maxcalories = new StringBuilder();
                    if(query.getText().toString().matches("")){
                        Toast.makeText(context, "Search query is compulsory", Toast.LENGTH_SHORT).show();
                    } else if(maxCalories.getText().toString().matches("")) {
                        Toast.makeText(context, "Maximum calories is required", Toast.LENGTH_SHORT).show();
                    } else if(maxFat.getText().toString().matches("")){
                        Toast.makeText(context, "Maximum Fat is requires", Toast.LENGTH_SHORT).show();
                    } else {
                        querySearch.replace(0, querySearch.length(), query.getText().toString());
                        maxcalories.replace(0, maxcalories.length(), maxCalories.getText().toString());
                        maxfat.replace(0, maxfat.length(), maxFat.getText().toString());
                        Log.i("SearchFood", "onClick: querySearch: " + querySearch);
                        if(!exclude.getText().toString().matches(""))
                            excludeIngredients.replace(0, excludeIngredients.length(), exclude.getText().toString());
                            Log.i("SearchFood", "onClick: exclude: " + excludeIngredients);

                            diet.replace(0, diet.length(), dietAdapter.getCheckedValue());
                        Log.i("SearchFood", "onClick: diet: " + diet);
                            meal.replace(0, meal.length(), mealAdapter.getCheckedValue());
                        Log.i("SearchFood", "onClick: meal: " + meal);

                        cuisine.replace(0, cuisine.length(), cuisineAdapter.getCheckedValue());
                        Log.i("SearchFood", "onClick: cuisine: " + cuisine);
                            intolerances.replace(0, intolerances.length(), intAdapter.getCheckedValue());
                        Log.i("SearchFood", "onClick: intolerances: " + intolerances);

                        searchFood(querySearch.toString(), cuisine.toString(), diet.toString(), meal.toString(), intolerances.toString()
                                , excludeIngredients.toString(), maxfat.toString(), maxcalories.toString());
                    }

                } catch (Exception e) {
                    Log.i("SearchFood", "onClickBtnFind: EXCEPTION : " + e.getMessage());
                }
            }
        });
    }

    private void searchFood (String searchQuery, String cuisine, String diet, String meal,
                             String intolerances, String excludeIng, String maxF, String maxC) {

        textViewResult = getView().findViewById(R.id.text_view_result);

        Log.d("SearchFood", "searchFood: " + cuisine + " " + diet + " " + meal + " " +
                intolerances + " " + searchQuery + " " + excludeIng + " " + maxF + " " + maxC);

        recycleFood = (RecyclerView) getView().findViewById(R.id.recycle_food);
        recycleFood.setLayoutManager(new LinearLayoutManager(getContext()));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.spoonacular.com/recipes/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        if(cuisine.equals("null"))
            cuisine = "";
        if(diet.equals("null"))
            diet = "";
        if(meal.equals("null"))
            meal = "";
        if(intolerances.equals("null"))
            intolerances = "";
        if(excludeIng.equals("null"))
            excludeIng = "";


        ApiComplexSearchInterface apiComplexSearchInterface = retrofit.create(ApiComplexSearchInterface.class);

        Call<ComplexSearch> call = apiComplexSearchInterface.getSearchResult(apiKey, searchQuery, diet, intolerances, excludeIng,
                true, meal, true, maxF, 99, cuisine, maxC);

        call.enqueue(new Callback<ComplexSearch>() {
            @Override
            public void onResponse(Call<ComplexSearch> call, Response<ComplexSearch> response) {
                if(!response.isSuccessful()){
                    textViewResult.setText("Response code: " + response.code() + call.request().toString());
                    Log.i("SearchFood", "onResponse: url" + response.toString());
                    foodBehaviour.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                    return;
                }
                ComplexSearch complexSearch = response.body();

                if(complexSearch.getTotalResults() > 0) {
                    Log.i("SearchFood", "onResponse: url" + response.toString());
                    List<Result> result = complexSearch.getResults();

                    recycleFood.setAdapter(new ComplexAdapter(result, R.layout.food_adapter, getContext()));
                    foodBehaviour.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);

                } else {
                    androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setMessage("No food recipes available for searched filter!");
                    alertDialog.show();
                }
            }

            @Override
            public void onFailure(Call<ComplexSearch> call, Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }

    private void hideSoftKeyboard(View v) {
        Log.d("SearchFood", "hideSoftKeyboard: yes");
        InputMethodManager inputMethodManager =(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(getActivity().getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("cuisine", cuisine.toString());
        outState.putString("diet", diet.toString());
        outState.putString("meal", meal.toString());
        outState.putString("exclude", excludeIngredients.toString());
        outState.putString("intolerances", intolerances.toString());
        outState.putString("calories", maxcalories.toString());
        outState.putString("fat", maxfat.toString());
        super.onSaveInstanceState(outState);

        getParentFragmentManager().putFragment(outState, "SearchFood", this);
    }

    @Override
    public void onBackButtonPressed() {
        Log.i("FindFood", "onBackButtonPressed: Pressed!");
        FindFoodFragment fragment = new FindFoodFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("comeFrom", "search");
        fragment.setArguments(bundle);
        transaction.replace(R.id.nav_host_fragment, fragment, tagFindFood)
                .commit();
    }
}