package com.launcher.foodlauncher.ui.favourites;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.launcher.foodlauncher.R;
import com.launcher.foodlauncher.ui.find_food.FoodAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FavouritesFragment extends Fragment {

    private FavouritesViewModel notificationsViewModel;

    private RecyclerView recycleRes, recycleFood;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser;

    Button restaurantBtn, foodBtn;
    TextView notAvailableFood, notAvailableRest;

    List<FavRestaurants> favRestaurants;
    List<FavFood> favFoods;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(FavouritesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_favourites, container, false);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        favRestaurants = new ArrayList<>();
        favFoods = new ArrayList<>();
        currentUser = fAuth.getCurrentUser();
        recycleFood = getView().findViewById(R.id.recycle_fav_food);
        recycleRes = getView().findViewById(R.id.recycle_fav_res);
        recycleFood.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleRes.setLayoutManager(new LinearLayoutManager(getContext()));

        restaurantBtn = getView().findViewById(R.id.fav_restaurants);
        foodBtn =  getView().findViewById(R.id.fav_food);

        notAvailableFood = getView().findViewById(R.id.not_available_food);
        notAvailableRest = getView().findViewById(R.id.not_available_rest);

        final DatabaseReference favListRef = FirebaseDatabase.getInstance().getReference().child("Fav List")
                .child("User").child(currentUser.getUid()).child("Restaurants");

        favListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for(DataSnapshot childSnapShot: dataSnapshot.getChildren()) {
                    Map<String, Object> td = (HashMap<String,Object>) childSnapShot.getValue();
                    FavRestaurants fav = new FavRestaurants();
                    if(td != null) {
                        fav.setResName(td.get("resName").toString());
                        fav.setAvgCostForTwo(td.get("avgCostForTwo").toString());
                        fav.setMenuLink(td.get("menuLink").toString());
                        fav.setResAddress(td.get("resAddress").toString());
                        fav.setResCuisines(td.get("resCuisines").toString());
                        fav.setResTimings(td.get("resTimings").toString());
                        fav.setTableRes(td.get("tableRes").toString());
                        fav.setResPhone(td.get("resPhone").toString());
                        fav.setResRating(td.get("resRating").toString());
                        fav.setRatingColor(td.get("ratingColor").toString());
                        fav.setResThumb(td.get("resThumb").toString());
                        fav.setResId(td.get("resId").toString());
                        favRestaurants.add(i, fav);
                        Log.i("FavouritesFragment", "onDataChange: favRestaurants: " + fav.toString());
                        i++;
                    }
                }

                recycleRes.setAdapter(new AdapterFavourite(favRestaurants, R.layout.restaurant_adapter, getContext()));
                if(favRestaurants.size() == 0) {
                    notAvailableRest.setText("No favourite restaurants found!!");
                    notAvailableRest.setVisibility(View.VISIBLE);
                    recycleRes.setVisibility(View.GONE);
                    notAvailableFood.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Adapter_HomeFragment", "Checking added favourites", databaseError.toException());
                // ...
            }
        });

        final DatabaseReference favFoodRef = FirebaseDatabase.getInstance().getReference().child("Fav List")
                .child("User").child(currentUser.getUid()).child("Food");

        favFoodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i = 0;
                for(DataSnapshot childSnapShot: dataSnapshot.getChildren()) {
                    Map<String, Object> td = (HashMap<String,Object>) childSnapShot.getValue();
                    FavFood fav = new FavFood();
                    if(td != null) {
                        fav.setFoodTitle(td.get("foodTitle").toString());
                        fav.setFoodCredits(td.get("foodCredits").toString());
                        fav.setCalories(td.get("calories").toString());
                        fav.setFat(td.get("fat").toString());
                        fav.setFoodId(td.get("foodId").toString());
                        fav.setFoodLikes(td.get("foodLikes").toString());
                        fav.setFoodScore(td.get("foodScore").toString());
                        fav.setFoodServings(td.get("foodServings").toString());
                        fav.setIngredients(td.get("ingredients").toString());
                        fav.setReadyInMinutes(td.get("readyInMinutes").toString());
                        fav.setFoodDiets(td.get("foodDiets").toString());
                        fav.setFoodCuisines(td.get("foodCuisines").toString());
                        fav.setVeg(td.get("veg").toString());
                        fav.setSummary(td.get("summary").toString());
                        ArrayList inst;
                        inst = (ArrayList) td.get("instructions");
                        Log.i("TAG", "onDataChange: inst: " + inst.toString());

                        List<Instructions> instructions = new ArrayList<>();
                        for(int k=0; k<inst.size()-1; k++) {
                            Instructions instruction = new Instructions();
                            instruction.setNumber(Integer.toString(k+1));
                            instruction.setStep(inst.get(k+1).toString());
                            Log.i("TAG", "onDataChange: instruction : " + instruction.getStep());
                            instructions.add(instruction);
                        }
                        fav.setInstructions(instructions);
                        favFoods.add(i, fav);
                        Log.i("FavouritesFragment", "onDataChange: favFood: " + fav.toString());
                        i++;
                    }
                }
                recycleFood.setAdapter(new FoodFavouriteAdapter(favFoods, R.layout.food_adapter, getContext()));
                if(favFoods.size() == 0) {
                    notAvailableFood.setText("No favourite recipes found!!");
//                    notAvailableFood.setVisibility(View.VISIBLE);
                    recycleFood.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Adapter_HomeFragment", "Checking added favourites", databaseError.toException());
                // ...
            }
        });

        restaurantBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favRestaurants.size() == 0) {
                    notAvailableRest.setText("No favourite restaurants found!!");
                    notAvailableRest.setVisibility(View.VISIBLE);
                    recycleRes.setVisibility(View.GONE);
                }
                recycleFood.setVisibility(View.GONE);
                recycleRes.setVisibility(View.VISIBLE);
                restaurantBtn.setBackgroundResource(R.drawable.chip_s);
                notAvailableFood.setVisibility(View.GONE);
                foodBtn.setBackgroundResource(R.drawable.chip_ns);
            }
        });

        foodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(favFoods.size() == 0) {
                    notAvailableFood.setText("No favourite recipes found!!");
                    notAvailableFood.setVisibility(View.VISIBLE);
                    recycleFood.setVisibility(View.GONE);
                }
                recycleFood.setVisibility(View.VISIBLE);
                recycleRes.setVisibility(View.GONE);
                restaurantBtn.setBackgroundResource(R.drawable.chip_ns);
                notAvailableRest.setVisibility(View.GONE);
                foodBtn.setBackgroundResource(R.drawable.chip_s);
            }
        });
    }
}
