package com.launcher.foodlauncher.ui.favourites;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.launcher.foodlauncher.R;

import java.util.List;

public class AdapterFavourite extends RecyclerView.Adapter<AdapterFavourite.ViewHolder>{

    private List<FavRestaurants> restaurants;
    private Context context;
    private int rowLayout;
    GradientDrawable shape = new GradientDrawable();
    FirebaseAuth fAuth;
    FirebaseUser currentUser;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textOne;
        TextView textTwo;
        TextView textThree;
        ImageView resImage;
        ViewGroup viewGroup;
        RelativeLayout relativeLayout;

        public ViewHolder(View v) {
            super(v);
            textOne = (TextView) v.findViewById(R.id.textView1);
            textTwo = (TextView) v.findViewById(R.id.textView2);
            textThree = (TextView) v.findViewById(R.id.textView3);
            resImage = v.findViewById(R.id.res_image);
            relativeLayout = v.findViewById(R.id.rest_details);
            viewGroup = v.findViewById(android.R.id.content);

        }
    }

    public AdapterFavourite(List<FavRestaurants> restaurants, int rowLayout, Context context) {
        this.restaurants = restaurants;
        this.rowLayout = rowLayout;
        this.context = context;
        fAuth = FirebaseAuth.getInstance();
        currentUser = fAuth.getCurrentUser();
    }

    @NonNull
    @Override
    public AdapterFavourite.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(AdapterFavourite.ViewHolder holder, int position) {
        holder.textOne.setText(restaurants.get(position).getResName());
        holder.textTwo.setText(restaurants.get(position).getResAddress());
        holder.textThree.setText("Rating: " + restaurants.get(position).getResRating());

        if(restaurants.get(position).getResThumb() != "") {
            Glide.with(context)
                    .load(restaurants.get(position).getResThumb())
                    .into(holder.resImage);
        }

//        double lat = Double.parseDouble(restaurants.get(position).getRestaurant().getLocation().getLatitude());
//        double lon = Double.parseDouble(restaurants.get(position).getRestaurant().getLocation().getLongitude());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.restaurant_details, holder.viewGroup, false);

                TextView resName = dialogView.findViewById(R.id.res_name);
                TextView resCuisines = dialogView.findViewById(R.id.res_cuisines);
                TextView resTimings = dialogView.findViewById(R.id.res_timings);
                TextView resRating = dialogView.findViewById(R.id.res_rating);
                TextView resAddress = dialogView.findViewById(R.id.res_address);
                TextView resMenu = dialogView.findViewById(R.id.res_menu);
                TextView resPhone = dialogView.findViewById(R.id.res_phone);
                Button callBtn = dialogView.findViewById(R.id.call_now);
                Button inviteBtn = dialogView.findViewById(R.id.invite);
                TextView costForTwo = dialogView.findViewById(R.id.res_cost_for_two);
                TextView tableReservation = dialogView.findViewById(R.id.res_table);
                RelativeLayout rate = dialogView.findViewById(R.id.rel_rating);

                ImageButton addFav = dialogView.findViewById(R.id.add_fav);
                addFav.setVisibility(View.GONE);
                ImageButton addedFav = dialogView.findViewById(R.id.added_fav);

                resName.setText(restaurants.get(position).getResName());
                resAddress.setText(restaurants.get(position).getResAddress());
                resCuisines.setText(restaurants.get(position).getResCuisines());
                resTimings.setText("Open Hours: " + restaurants.get(position).getResTimings());
                resRating.setText(restaurants.get(position).getResRating());
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setCornerRadii(new float[] { 16, 16, 16, 16, 16, 16, 16, 16 });
                shape.setColor(Color.parseColor("#" + restaurants.get(position).getRatingColor()));
                rate.setBackground(shape);
                costForTwo.setText("Average cost for two: Rs. " + restaurants.get(position).getAvgCostForTwo());
                if(restaurants.get(position).getTableRes().equals("0")) {
                    tableReservation.setText("Table reservation is not supported.");
                } else {
                    tableReservation.setText("Table reservation is not supported.");
                }

                final DatabaseReference favListRef = FirebaseDatabase.getInstance().getReference().child("Fav List")
                        .child("User").child(currentUser.getUid()).child("Restaurants");
                ValueEventListener postListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long totalChildren = dataSnapshot.getChildrenCount();
                        for(long i=0; i<totalChildren; i++) {
                            if(dataSnapshot.hasChild(restaurants.get(position).getResId())) {
                                addFav.setVisibility(View.GONE);
                                addedFav.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.w("Adapter_HomeFragment", "Checking added favourites", databaseError.toException());
                        // ...
                    }
                };
                favListRef.addValueEventListener(postListener);

                inviteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openWhatsApp(v);
                    }
                });

                addedFav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeFromFav(position);
                        addedFav.setVisibility(View.GONE);
                    }
                });

                resMenu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uriUrl = Uri.parse(restaurants.get(position).getMenuLink());
                        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                        launchBrowser.addCategory(Intent.CATEGORY_BROWSABLE);
                        context.startActivity(launchBrowser);
                    }
                });

                if(!restaurants.get(position).getResPhone().equals("Not available for this place")) {
                    resPhone.setText("Phone number: " + restaurants.get(position).getResPhone());
                    Uri u = Uri.parse("tel:" + restaurants.get(position).getResPhone());
                    callBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(Intent.ACTION_DIAL, u);
                            try
                            {
                                // Launch the Phone app's dialer with a phone
                                // number to dial a call.
                                context.startActivity(i);
                            }
                            catch (SecurityException s)
                            {
                                // show() method display the toast with
                                // exception message.
                                Toast.makeText(context, "Error: " + s.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else {
                    resPhone.setText("Phone number not available for this place.");
                    callBtn.setVisibility(View.GONE);
                }

                builder.setView(dialogView);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void removeFromFav (int position) {
        final DatabaseReference favListRef = FirebaseDatabase.getInstance().getReference().child("Fav List")
                .child("User").child(currentUser.getUid()).child("Restaurants")
                .child(restaurants.get(position).getResId());

        favListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue();
                Toast.makeText(context, "Restaurant removed from Favourites", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Adapter_HomeFragment", "onCancelled", databaseError.toException());
            }
        });
    }

    void openWhatsApp(View view){
        PackageManager pm = context.getPackageManager();
        try {
            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = "I'm inviting you to my party."; // Replace with your own message.

            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            context.startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(context, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }catch(Exception e){
            e.getMessage();
        }

    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }
}