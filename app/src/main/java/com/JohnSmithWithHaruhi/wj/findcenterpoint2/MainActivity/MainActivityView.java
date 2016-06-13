package com.JohnSmithWithHaruhi.wj.findcenterpoint2.MainActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.JohnSmithWithHaruhi.wj.findcenterpoint2.Model.Unit.NearbySearch.Photo;
import com.JohnSmithWithHaruhi.wj.findcenterpoint2.PlaceDetail.PlaceDetailFragment;
import com.JohnSmithWithHaruhi.wj.findcenterpoint2.R;
import com.JohnSmithWithHaruhi.wj.findcenterpoint2.SelectFragment;
import com.JohnSmithWithHaruhi.wj.findcenterpoint2.databinding.ActivityMainBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

public class MainActivityView extends AppCompatActivity implements MainActivityViewInterface.View, OnMapReadyCallback {

    private final static String TAG = "MainActivityView";
    private ActivityMainBinding binding;
    private MainActivityPresenter presenter;

    private MapFragment mapFragment;
    private GoogleMap googleMap;

    private ActionBarDrawerToggle drawerToggle;
    private ProgressDialog progressDialog;
    private SelectFragment selectFragment;
    private ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        presenter = new MainActivityPresenter(this);
        presenter.onViewCreate();

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.main_map);
        mapFragment.getMapAsync(this);

        selectFragment = new SelectFragment();
        selectFragment.setListener(presenter);


        setActionBar(binding.mainToolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(this, binding.mainDL, R.string.open_drawer, R.string.close_drawer);
        binding.mainDL.addDrawerListener(drawerToggle);

        binding.mainFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFragment.show(getFragmentManager(), "selectFragment");
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void setAdapter(List<String> list) {
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        binding.mainLV.setAdapter(arrayAdapter);
        binding.mainLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                binding.mainDL.closeDrawers();
                presenter.onListClick(position);
            }
        });
    }

    @Override
    public void setGoogleMap(final List<Marker> markerList, final List<String> placeIDList, final List<List<Photo>> photoList) {

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View mView = getLayoutInflater().inflate(R.layout.info_window, null);

                TextView name = (TextView) mView.findViewById(R.id.info_name);
                name.setText(marker.getTitle());

                RatingBar ratingBar = (RatingBar) mView.findViewById(R.id.info_RB);
                ratingBar.setRating(Float.valueOf(marker.getSnippet()));

                return mView;
            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                for (int i = 0; i < markerList.size(); i++) {
                    if (markerList.get(i).equals(marker)) {
                        Log.d(TAG, placeIDList.get(i));
                        PlaceDetailFragment placeDetailFragment = new PlaceDetailFragment();
                        Bundle arg = new Bundle();
                        arg.putString("placeID", placeIDList.get(i));
                        //arg.putStringArrayList("photo", (ArrayList<String>) photoList);
                        placeDetailFragment.setArguments(arg);
                        getFragmentManager().beginTransaction().addToBackStack(null).add(android.R.id.content, placeDetailFragment).commit();
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void showProgressDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    @Override
    public void dismissProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public Context getContextCompat() {
        return getApplicationContext();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        this.googleMap = googleMap;
        presenter.onMapReady(this.googleMap);
    }
}

