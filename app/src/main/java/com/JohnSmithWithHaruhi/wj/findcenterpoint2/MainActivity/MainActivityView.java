package com.JohnSmithWithHaruhi.wj.findcenterpoint2.MainActivity;

import android.app.FragmentManager;
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
import com.JohnSmithWithHaruhi.wj.findcenterpoint2.PlaceDetail.PlaceDetailView;
import com.JohnSmithWithHaruhi.wj.findcenterpoint2.R;
import com.JohnSmithWithHaruhi.wj.findcenterpoint2.Unit.SelectFragment;
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

        setSupportActionBar(binding.mainToolbar);

        drawerToggle = new ActionBarDrawerToggle(this, binding.mainDL, R.string.open_drawer, R.string.close_drawer);
        binding.mainDL.addDrawerListener(drawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


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
        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack();
                    break;
                } else {
                    drawerToggle.onOptionsItemSelected(item);
                }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
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
    public void notifyAdapterChange() {
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void setGoogleMapInfoWindow(final List<Marker> markerList, final List<String> placeIDList, final List<List<Photo>> photoList) {

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
                        PlaceDetailView placeDetailView = new PlaceDetailView();

                        Bundle arg = new Bundle();
                        arg.putString("placeID", placeIDList.get(i));

                        if (photoList.get(i).size() != 0) {
                            arg.putString("photoUrl", photoList.get(i).get(0).getPhotoReference());
                            Log.d(TAG, "photoUrl " + photoList.get(i).get(0).getPhotoReference());
                        }

                        placeDetailView.setArguments(arg);
                        getFragmentManager().beginTransaction().addToBackStack(null).add(android.R.id.content, placeDetailView).commit();
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
        googleMap.setIndoorEnabled(false);

        this.googleMap = googleMap;
        presenter.onMapReady(this.googleMap);
    }
}

