package com.launcher.foodlauncher.ui.find_food;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.launcher.foodlauncher.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder>{

    private List<Recipe> recipes;
    private Context context;
    private int rowLayout;
    FirebaseAuth fAuth;
    FirebaseUser currentUser;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textOne;
        TextView textTwo;
        TextView textThree;
        TextView textFour;
        TextView textFive;
        RelativeLayout relativeLayout;
        ViewGroup viewGroup;
        public ViewHolder(View v) {
            super(v);
            textOne = (TextView) v.findViewById(R.id.textView1);
            textTwo = (TextView) v.findViewById(R.id.textView2);
            textThree = (TextView) v.findViewById(R.id.textView3);
            textFour = (TextView) v.findViewById(R.id.textView4);
            textFive = (TextView) v.findViewById(R.id.textView5);
            relativeLayout = v.findViewById(R.id.food_details);
        }
    }

    public FoodAdapter(List<Recipe> recipes, int rowLayout, Context context) {
        this.recipes = recipes;
        this.rowLayout = rowLayout;
        this.context = context;
        fAuth = FirebaseAuth.getInstance();
        currentUser = fAuth.getCurrentUser();
    }

    @NonNull
    @Override
    public FoodAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(FoodAdapter.ViewHolder holder, int position) {
        if(recipes.size() > 0) {
            StringBuilder diets = new StringBuilder();
            StringBuilder cuisines = new StringBuilder();
            if (recipes.get(position).getDiets().size() > 0) {
                for (int i = 0; i < recipes.get(position).getDiets().size(); i++) {
                    diets.append(recipes.get(position).getDiets().get(i)).append(",");
                }
                diets.deleteCharAt(diets.lastIndexOf(","));
                holder.textTwo.setText("Diets: " + diets.toString());
            } else
                holder.textTwo.setVisibility(View.GONE);
            if (recipes.get(position).getCuisines().size() > 0) {
                for (int i = 0; i < recipes.get(position).getCuisines().size(); i++) {
                    cuisines.append(recipes.get(position).getCuisines().get(i)).append(", ");
                }
                cuisines.deleteCharAt(cuisines.lastIndexOf(","));
                holder.textThree.setText("Cuisines: " + cuisines.toString());
            } else
                holder.textThree.setVisibility(View.GONE);

            holder.textOne.setText(recipes.get(position).getTitle());
            holder.textFive.setText(recipes.get(position).getCreditsText());
            holder.textFour.setText("Ready in " + recipes.get(position).getReadyInMinutes() + " minutes");

            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                    View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.full_recipe, holder.viewGroup, false);

                    LinearLayout stepsLayout = dialogView.findViewById(R.id.steps);
                    TextView title = dialogView.findViewById(R.id.food_title);
                    TextView credits = dialogView.findViewById(R.id.food_credits);
                    TextView servings = dialogView.findViewById(R.id.food_servings);
                    TextView likes = dialogView.findViewById(R.id.food_likes);
                    TextView score = dialogView.findViewById(R.id.food_score);
                    TextView veg = dialogView.findViewById(R.id.food_veg);
                    TextView ready = dialogView.findViewById(R.id.food_ready_in);
                    TextView calories = dialogView.findViewById(R.id.food_calories);
                    TextView fat = dialogView.findViewById(R.id.food_fat);
                    TextView ingredients = dialogView.findViewById(R.id.food_ingredients);
                    List<String> ingr = new ArrayList<>();
                    TextView summary = dialogView.findViewById(R.id.food_summary);

                    ImageButton addFav = dialogView.findViewById(R.id.add_favfood);
                    ImageButton addedFav = dialogView.findViewById(R.id.added_favfood);

                    String summ = recipes.get(position).getSummary();
                    summ = summ.replaceAll("\\[.*?\\]", "");
                    summ = summ.replaceAll("\\<.*?\\>", "");

                    summary.setText(summ);
                    title.setText(recipes.get(position).getTitle());
                    credits.setText("Credits: " + recipes.get(position).getCreditsText());
                    servings.setText("Servings: " + recipes.get(position).getServings());
                    likes.setText("Likes: " + recipes.get(position).getAggregateLikes());
                    score.setText("Health score: " + recipes.get(position).getHealthScore());
                    if(recipes.get(position).isVegetarian())
                        veg.setText("Vegetarian");
                    else
                        veg.setText("Non-vegetarian");
                    ready.setText("Ready in " + recipes.get(position).getReadyInMinutes() + " minutes");
                    calories.setVisibility(View.GONE);
                    fat.setVisibility(View.GONE);

                    StringBuilder ing = new StringBuilder();
                    for (int i = 0; i < recipes.get(position).getAnalyzedInstructions().get(0).getSteps().size(); i++) {
                        for (int j = 0; j < recipes.get(position).getAnalyzedInstructions().get(0)
                                .getSteps().get(i).getIngredients().size(); j++) {
                            if(!ingr.contains(recipes.get(position).getAnalyzedInstructions().get(0).
                                    getSteps().get(i).getIngredients().get(j).getName())) {
                                ingr.add(recipes.get(position).getAnalyzedInstructions().get(0).
                                        getSteps().get(i).getIngredients().get(j).getName());
                                ing.append(recipes.get(position).getAnalyzedInstructions().get(0).
                                        getSteps().get(i).getIngredients().get(j).getName()).append(", ");

                            }
                        }
                    }
                    try {
                        ing.deleteCharAt(ing.lastIndexOf(","));
                    } catch (Exception e) {
                        Log.i("FoodAdapter", "onClick: exception ing.deleteCharAt(ing.lastIndexPf(\",\")" + e.getMessage());
                    }
                    ingredients.setText("Ingredients: " + ing.toString());

                    int totalSteps = recipes.get(position).getAnalyzedInstructions().get(0).getSteps().size();

                    HashMap<String, Object> steps = new HashMap<>();
                    for (int i = 0; i < totalSteps; i++) {
                        TextView t = new TextView(context);
                        t.setText("Step " + recipes.get(position).getAnalyzedInstructions().get(0).getSteps().get(i).getNumber()
                                + ": " + recipes.get(position).getAnalyzedInstructions().get(0).getSteps().get(i).getStep());
                        steps.put(Long.toString(recipes.get(position).getAnalyzedInstructions().get(0).getSteps().get(i).getNumber()),
                                recipes.get(position).getAnalyzedInstructions().get(0).getSteps().get(i).getStep());
                        t.setPadding(10, 5, 10, 10);
                        t.setTextSize(15);
                        stepsLayout.addView(t);
                    }

                    String finalSumm = summ;
                    addFav.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addToFavList(position, steps, ing.toString(), diets.toString(), cuisines.toString(), finalSumm);
                            addFav.setVisibility(View.GONE);
                            addedFav.setVisibility(View.VISIBLE);
                        }
                    });

                    addedFav.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeFromFav(position);
                            addFav.setVisibility(View.VISIBLE);
                            addedFav.setVisibility(View.GONE);
                        }
                    });
                    builder.setView(dialogView);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });
        }

    }

    private void addToFavList(int position, HashMap<String, Object> steps, String ingredients, String diets, String cuisines, String summary) {

        String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        String foodId = Long.toString(recipes.get(position).getId());
        final DatabaseReference favListRef = FirebaseDatabase.getInstance().getReference().child("Fav List");

        final HashMap<String,Object> favMap = new HashMap<>();
        favMap.put("foodTitle", recipes.get(position).getTitle());
        favMap.put("foodCredits", recipes.get(position).getCreditsText());
        favMap.put("date", saveCurrentDate + saveCurrentTime);
        favMap.put("foodServings", recipes.get(position).getServings());
        favMap.put("foodId", recipes.get(position).getId());
        favMap.put("readyInMinutes", recipes.get(position).getReadyInMinutes());
        favMap.put("foodLikes", recipes.get(position).getAggregateLikes());
        favMap.put("foodScore", recipes.get(position).getHealthScore());
        favMap.put("veg", recipes.get(position).isVegetarian());
        favMap.put("summary", summary);
        favMap.put("fat", "Not available");
        if(diets.length() != 0)
            favMap.put("foodDiets", diets);
        else
            favMap.put("foodDiets", "Not Available");
        if(cuisines.length() != 0)
            favMap.put("foodCuisines", cuisines);
        else
            favMap.put("foodCuisines", "Not Available");
        favMap.put("calories", "Not available");
        favMap.put("instructions", steps);
        favMap.put("ingredients", ingredients);

        if (currentUser != null) {
            favListRef.child("User").child(currentUser.getUid()).child("Food")
                    .child(foodId).updateChildren(favMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(context.getApplicationContext(), "Food added to Favourites", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void removeFromFav (int position) {
        final DatabaseReference favListRef = FirebaseDatabase.getInstance().getReference().child("Fav Food List")
                .child("User").child(currentUser.getUid()).child("Recipes")
                .child(Long.toString(recipes.get(position).getId()));

        favListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
                Toast.makeText(context, "Food removed from Favourites", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FoodAdapter_Random", "onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }
}