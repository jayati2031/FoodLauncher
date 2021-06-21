package com.launcher.foodlauncher.ui.home;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.firebase.auth.FirebaseAuth;
import com.launcher.foodlauncher.R;
import com.launcher.foodlauncher.adapter.PlaceAutoSuggestAdapter;
import com.launcher.foodlauncher.api.ApiSearchInterface;
import com.skyfishjy.library.RippleBackground;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private double lat, lon;
    private final String latitude_key = "latitude", longitude_key = "longitude", sort_key = "sort";
    private String sort = "";
    private HomeViewModel homeViewModel;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    private BottomSheetBehavior bottomSheetBehavior;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private PlacesClient placesClient;
    private Location mLastKnownLocation;
    private LocationCallback locationCallback;
    private ImageButton btnFind, btnNearby;
    private Button sortRating, sortDistance, sortCost;
    private RippleBackground rippleBg;
    private View mapView;
    private double latitude = -1;
    private double longitude = -1;
    private float DEFAULT_ZOOM = 16;

    AutoCompleteTextView autoCompleteTextView;

    private TextView textViewResult;
    private final String apiKey = "08990901fb962c7394216bd47b7a613a";

    private String sortByRealDistance = "real_distance";
    private String sortByRating = "rating";
    private String sortByCost = "cost";

    RecyclerView recyclerView;

    List<Restaurant> restaurants1;

    Call<Search> call;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            lat = savedInstanceState.getDouble(latitude_key);
            lon = savedInstanceState.getDouble(longitude_key);
            if(savedInstanceState.getString(sort_key).length() != 0)
                sort = savedInstanceState.getString(sort_key);
            else
                sort = "";
            latitude = lat;
            longitude = lon;
            // Do something with value if needed
        }
        Log.i("HomeFragment", "onActivityCreated: saveInstanceState: " + (savedInstanceState != null));

        if(!(latitude == -1 && longitude == -1)) {
            showRestaurants(latitude, longitude);
        }

        String googleApiKey = "AIzaSyDNSYlMnfy-MUNl3MUoRjZDZWYD3WLg8AQ";

        sortCost = getView().findViewById(R.id.sort_cost);
        sortDistance = getView().findViewById(R.id.sort_distance);
        sortRating = getView().findViewById(R.id.sort_rating);

        FrameLayout bottomSheetLayout = getView().findViewById(R.id.standardBottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);

        autoCompleteTextView = getView().findViewById(R.id.autocomplete);
        autoCompleteTextView.setAdapter(new PlaceAutoSuggestAdapter(getActivity(), android.R.layout.simple_list_item_1));

        btnNearby = getView().findViewById(R.id.btn_nearby_find);
        btnFind = getView().findViewById(R.id.btn_find);
        rippleBg = getView().findViewById(R.id.ripple_bg);

        btnFind.setVisibility(View.INVISIBLE);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapView = mapFragment.getView();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if(!Places.isInitialized()) {
            Places.initialize(getContext(), googleApiKey);
        }

        placesClient = Places.createClient(getActivity());

        btnNearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rippleBg.startRippleAnimation();
                getDeviceLocation();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rippleBg.stopRippleAnimation();
                        lat = latitude;
                        lon = longitude;
                        showRestaurants(latitude, longitude);
                    }
                }, 2000);
            }
        });


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 180, 40, 0);
        }

        // Check if GPS is enabled or not and then request user to enable it
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if(latitude == -1 && longitude == -1)
                    getDeviceLocation();
                else {
                    moveCamera(new LatLng(latitude, longitude), DEFAULT_ZOOM, "Default");
                    geoLocate();
                }
            }
        });

        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof ResolvableApiException) {
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(getActivity(), 51);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        init();
    }

    private void init() {
        Log.d("HomeFragment", "init: Initializing");

        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN || event.getAction() == KeyEvent.KEYCODE_ENTER) {
                    // execute our method for searching
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    geoLocate();
                }
                return false;
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                btnNearby.setVisibility(View.VISIBLE);
                btnFind.setVisibility(View.INVISIBLE);
                getDeviceLocation();
                return true;
            }
        });
    }

    private void geoLocate() {
        Log.d("HomeFragment", "geoLocate: geolocating");

        String searchString = autoCompleteTextView.getText().toString();

        Geocoder geocoder= new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            Log.e("HomeFragment", "geoLocate: IOException: " + e.getMessage() );
        }

        if(list.size() > 0) {
            Address address = list.get(0);

            Log.d("HomeFragment", "geoLocate: found a location: " + address.toString());

            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), DEFAULT_ZOOM, address.getAddressLine(0));

            btnNearby.setVisibility(View.INVISIBLE);
            btnFind.setVisibility(View.VISIBLE);

            btnFind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rippleBg.startRippleAnimation();
                    latitude = address.getLatitude();
                    longitude = address.getLongitude();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            rippleBg.stopRippleAnimation();
                            lat = latitude;
                            lon = longitude;
                            showRestaurants(latitude, longitude);
                        }
                    }, 2000);
                }
            });

        }
    }

    private void moveCamera (LatLng latlng, float zoom, String title) {
        Log.d("HomeFragment", "moveCamera: Moving the camera to: lat: " + latlng.latitude + ", lng: " + latlng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom));

        if(!title.equals("My location")){
            MarkerOptions options = new MarkerOptions()
                    .position(latlng)
                    .title(title);
            mMap.addMarker(options);
        }

        hideSoftKeyboard();
    }

    private void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if(mLastKnownLocation != null) {
                                Log.d("HomeFragment", "onComplete: ID: " + mLastKnownLocation.getLatitude() + " " + mLastKnownLocation.getLongitude());
                                moveCamera(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM, "Current Location");
                                latitude = mLastKnownLocation.getLatitude();
                                longitude = mLastKnownLocation.getLongitude();
                                showRestaurants(latitude, longitude);
                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if (locationResult == null) {
                                            return;
                                        }
                                        mLastKnownLocation = locationResult.getLastLocation();
                                        Log.d("HomeFragment", "onComplete: ID: " + mLastKnownLocation.getLatitude() + " " + mLastKnownLocation.getLongitude());
                                        moveCamera(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM, "Current Location");
                                        latitude = mLastKnownLocation.getLatitude();
                                        longitude = mLastKnownLocation.getLongitude();
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                            lat = latitude;
                            lon = longitude;
                        } else {
                            Toast.makeText(getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void hideSoftKeyboard() {
        Log.d("HomeFragment", "hideSoftKeyboard: yes");
        InputMethodManager inputMethodManager =(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(getActivity().getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    void showRestaurants(double lat, double lon) {

        textViewResult = getView().findViewById(R.id.text_view_result);

        Log.d("HomeFragment", "onCreate: " + latitude + " " + longitude);

        recyclerView = (RecyclerView) getView().findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://developers.zomato.com/api/v2.1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiSearchInterface searchApi = retrofit.create(ApiSearchInterface.class);

        if(sort.length() == 0) {
            call = searchApi.getResultBy(apiKey, lat, lon, sortByRating);
            sort = sortByRating;
        } else {
            call = searchApi.getResultBy(apiKey, lat, lon, sort);
        }
        callEnqueue(call);

        sortRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortRating.setBackgroundResource(R.drawable.chip_s);
                sortDistance.setBackgroundResource(R.drawable.chip_ns);
                sortCost.setBackgroundResource(R.drawable.chip_ns);
                call = searchApi.getResultBy(apiKey, lat, lon, sortByRating);
                callEnqueue(call);
            }
        });

        sortDistance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortRating.setBackgroundResource(R.drawable.chip_ns);
                sortDistance.setBackgroundResource(R.drawable.chip_s);
                sortCost.setBackgroundResource(R.drawable.chip_ns);
                call = searchApi.getResultBy(apiKey, lat, lon, sortByRealDistance);
                callEnqueue(call);
            }
        });

        sortCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortRating.setBackgroundResource(R.drawable.chip_ns);
                sortDistance.setBackgroundResource(R.drawable.chip_ns);
                sortCost.setBackgroundResource(R.drawable.chip_s);
                call = searchApi.getResultBy(apiKey, lat, lon, sortByCost);
                callEnqueue(call);
            }
        });

    }

    private void callEnqueue (Call call){
        call.enqueue(new Callback<Search>() {
            @Override
            public void onResponse(Call<Search> call, Response<Search> response) {
                Log.i("HomeFragment", "onResponse: Restaurants call called");
                if (!response.isSuccessful()) {
                    textViewResult.setText("Response code: " + response.code());
                    return;
                }
                Search search = response.body();
                response.headers();

                restaurants1 = search.getRestaurants();

                if(restaurants1.size() == 0) {
                    textViewResult.setText("No restaurants found!!");
                    textViewResult.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setAdapter(new Adapter(restaurants1, R.layout.restaurant_adapter, getContext()));
                }

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
            }

            @Override
            public void onFailure(Call<Search> call, Throwable t) {
                textViewResult.setText(t.getMessage());
                textViewResult.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putDouble(latitude_key, lat);
        outState.putDouble(longitude_key, lon);
        outState.putString(sort_key, sort);
        super.onSaveInstanceState(outState);

        getParentFragmentManager().putFragment(outState, "HomeFragment", this);
    }
}
