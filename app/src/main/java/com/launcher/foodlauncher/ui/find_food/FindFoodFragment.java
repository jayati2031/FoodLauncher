package com.launcher.foodlauncher.ui.find_food;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;
import com.launcher.foodlauncher.R;
import com.launcher.foodlauncher.ui.home.HomeFragment;

import java.util.ArrayList;

public class FindFoodFragment extends Fragment {

    private FindFoodViewModel findFoodViewModel;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    private SearchFood fragmentSearch;
    private RandomSearchFragment fragmentRandom;
    public int fragSelected = 0;
    private String fragKey = "selected_frag";

    private Button searchFood, searchRandom;

    private String tagSearch = "SearchFragmentTag", tagRandom = "RandomSearchFragmentTag";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        findFoodViewModel =
                ViewModelProviders.of(this).get(FindFoodViewModel.class);
        View root = inflater.inflate(R.layout.fragment_find_food, container, false);
        final TextView textView = root.findViewById(R.id.text_find_food);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.i("FindFood", "onActivityCreated: savedInstance: " + (savedInstanceState!=null));
        Log.i("FindFood", "onActivityCreated: fragSelected: " + fragSelected);

        if(savedInstanceState != null) {
            String backFrom;
            if(this.getArguments() != null) {
                backFrom = this.getArguments().getString("comeFrom");
                Log.i("FindFood", "onActivityCreated: comeFrom: " + backFrom);
                if(backFrom.equals("search") || backFrom.equals("random")) {
                    fragSelected = 0;
                }
            } else {
                fragSelected = savedInstanceState.getInt(fragKey);
            }
            Log.i("FindFood", "onActivityCreated: fragSelected: " + fragSelected);
        }

        if(fragSelected == 1) {
            Log.i("FindFood", "onActivityCreated: fragSelected: " + fragSelected);
            fragmentSearch = (SearchFood) getParentFragmentManager().findFragmentByTag(tagSearch);
            if(fragmentSearch == null)
                fragmentSearch = new SearchFood(getContext());
            loadFragment(fragmentSearch, "FindFoodFragment", tagSearch);
        }

        if(fragSelected == 2) {
            Log.i("FindFood", "onActivityCreated: fragSelected: " + fragSelected);

            fragmentRandom = (RandomSearchFragment) getParentFragmentManager().findFragmentByTag(tagRandom);
            if(fragmentRandom == null)
                fragmentRandom = new RandomSearchFragment(getContext());
            loadFragment(fragmentRandom, "FindFoodFragment", tagRandom);
        }

        searchFood = getView().findViewById(R.id.search_btn);
        searchRandom = getView().findViewById(R.id.random_btn);

        searchFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragSelected = 1;
                Log.i("FindFood", "onActivityCreated: fragSelected: " + fragSelected);
                if(fragmentSearch != null) {
                    fragmentSearch = (SearchFood)
                            getParentFragmentManager().findFragmentByTag(tagSearch);
                }
                if(fragmentSearch == null)
                    fragmentSearch = new SearchFood(getContext());
                loadFragment(fragmentSearch, "FindFoodFragment", tagSearch);
            }
        });

        searchRandom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragSelected = 2;
                Log.i("FindFood", "onActivityCreated: fragSelected: " + fragSelected);
                if(fragmentRandom != null) {
                    fragmentRandom = (RandomSearchFragment)
                            getParentFragmentManager().findFragmentByTag(tagRandom);
                }
                if(fragmentRandom == null)
                    fragmentRandom = new RandomSearchFragment(getContext());
                loadFragment(fragmentRandom, "FindFoodFragment", tagRandom);
            }
        });


    }

    private void loadFragment(Fragment fragment, String comeFrom, String tag) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("come_from", comeFrom);
        fragment.setArguments(bundle);
        transaction.replace(R.id.nav_host_fragment, fragment, tag)
                .addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(fragSelected!=1 && fragSelected!=2)
            fragSelected = 0;
        Log.i("FindFood", "onSaveInstanceState: fragSelected: " + fragSelected);
        outState.putInt(fragKey, fragSelected);
        super.onSaveInstanceState(outState);
    }

}
