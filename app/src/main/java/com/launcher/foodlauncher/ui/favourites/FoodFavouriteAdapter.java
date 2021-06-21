package com.launcher.foodlauncher.ui.favourites;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.launcher.foodlauncher.R;

import java.util.List;

public class FoodFavouriteAdapter extends RecyclerView.Adapter<FoodFavouriteAdapter.ViewHolder>{

    private List<FavFood> result;
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

    public FoodFavouriteAdapter(List<FavFood> result, int rowLayout, Context context) {
        this.result = result;
        this.rowLayout = rowLayout;
        this.context = context;
        fAuth = FirebaseAuth.getInstance();
        currentUser = fAuth.getCurrentUser();
    }

    @NonNull
    @Override
    public FoodFavouriteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new FoodFavouriteAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(FoodFavouriteAdapter.ViewHolder holder, int position) {
        if(result.get(position).getFoodDiets().length() > 0) {
            holder.textTwo.setText("Diets: " + result.get(position).getFoodDiets());
        } else
            holder.textTwo.setVisibility(View.GONE);
        if(result.get(position).getFoodCuisines().length() > 0) {
           holder.textThree.setText("Cuisines: " + result.get(position).getFoodCuisines());
        } else
            holder.textThree.setVisibility(View.GONE);

        holder.textOne.setText(result.get(position).getFoodTitle());
        holder.textFive.setText(result.get(position).getFoodCredits());
        holder.textFour.setText("Ready in " + result.get(position).getReadyInMinutes() + " minutes");

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
                TextView summary = dialogView.findViewById(R.id.food_summary);

                ImageButton addFav = dialogView.findViewById(R.id.add_favfood);
                ImageButton addedFav = dialogView.findViewById(R.id.added_favfood);

                addFav.setVisibility(View.GONE);
                addedFav.setVisibility(View.VISIBLE);

                summary.setText(result.get(position).getSummary());
                title.setText(result.get(position).getFoodTitle());
                credits.setText("Credits: " + result.get(position).getFoodCredits());
                servings.setText("Servings: " + result.get(position).getFoodServings());
                likes.setText("Likes: " + result.get(position).getFoodLikes());
                score.setText("Health score: " + result.get(position).getFoodScore());
                if(result.get(position).getVeg().equals("true"))
                    veg.setText("Vegetarian");
                else
                    veg.setText("Non-vegetarian");
                ready.setText("Ready in " + result.get(position).getReadyInMinutes() + " minutes");
                calories.setText("Total calories: " + result.get(position).getCalories() + " cal");
                fat.setText("Total Fat: " + result.get(position).getFat() + " g");

                ingredients.setText("Ingredients: " + result.get(position).ingredients);

                int totalSteps = result.get(position).getInstructions().size();

                for(int i=0; i<totalSteps; i++) {
                    TextView t = new TextView(context);
                    t.setText("Step " + result.get(position).getInstructions().get(i).getNumber()
                            + ": " + result.get(position).getInstructions().get(i).getStep());
                    t.setPadding(10, 5, 10, 10);
                    t.setTextSize(15);
                    stepsLayout.addView(t);
                }

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

    private void removeFromFav (int position) {
        final DatabaseReference favListRef = FirebaseDatabase.getInstance().getReference().child("Fav Food List")
                .child("User").child(currentUser.getUid()).child("Recipes")
                .child(result.get(position).getFoodId());

        favListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
                Toast.makeText(context, "Food removed from Favourites", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FoodAdapter_Search", "onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    public int getItemCount() {
        return result.size();
    }
}
