package com.launcher.foodlauncher.ui.user;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.launcher.foodlauncher.LoginActivity;
import com.launcher.foodlauncher.R;
import com.launcher.foodlauncher.ui.find_food.FindFoodViewModel;

import java.util.Map;

public class UserFragment extends Fragment {

    private UserViewModel userViewModel;

    private Button btnLogOut;

    FirebaseAuth fAuth;
    FirebaseUser currentUser;

    TextView uname, email, phone, favFood, favRes;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        userViewModel =
                ViewModelProviders.of(this).get(UserViewModel.class);
        View root = inflater.inflate(R.layout.fragment_user, container, false);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        btnLogOut = getView().findViewById(R.id.logout_btn);

        email = getView().findViewById(R.id.user_email);
        phone = getView().findViewById(R.id.user_phone);
        uname = getView().findViewById(R.id.username);
        favFood = getView().findViewById(R.id.user_favfood);
        favRes = getView().findViewById(R.id.user_favrest);

        fAuth = FirebaseAuth.getInstance();
        currentUser = fAuth.getCurrentUser();

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        final DatabaseReference userData = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(currentUser.getUid());

        userData.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uname.setText(dataSnapshot.child("username").getValue().toString());
                phone.setText(dataSnapshot.child("phone").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("UserFragment", "onCancelled: databaseError user data: " + databaseError.toString());
            }
        });

        email.setText(currentUser.getEmail());

        final DatabaseReference favFoodRef = FirebaseDatabase.getInstance().getReference().child("Fav List")
                .child("User").child(currentUser.getUid()).child("Food");

        final DatabaseReference favListRef = FirebaseDatabase.getInstance().getReference().child("Fav List")
                .child("User").child(currentUser.getUid()).child("Restaurants");

        favFoodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favFood.setText(dataSnapshot.getChildrenCount() + " favourite recipes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("UserFragment", "onCancelled: favourite recipes: " + databaseError.getMessage());
            }
        });

        favListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favRes.setText(dataSnapshot.getChildrenCount() + " favourite restaurants");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("UserFragment", "onCancelled: favourite restaurants: " + databaseError.getMessage());
            }
        });

    }
}
