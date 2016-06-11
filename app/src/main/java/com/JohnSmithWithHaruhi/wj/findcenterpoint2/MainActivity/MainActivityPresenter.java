package com.JohnSmithWithHaruhi.wj.findcenterpoint2.MainActivity;

import android.os.Bundle;
import android.util.Log;

import com.JohnSmithWithHaruhi.wj.findcenterpoint2.Model.ApiClient;
import com.JohnSmithWithHaruhi.wj.findcenterpoint2.PlaceDetail.PlaceDetailFragment;
import com.JohnSmithWithHaruhi.wj.findcenterpoint2.Selector.SelectFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wj on 16/6/10.
 */
public class MainActivityPresenter implements OnMapReadyCallback, MainActivityViewInterface.Presenter, SelectFragment.SelectFragmentListener {

    private final static String TAG = "MainActivityPresenter";
    private MainActivityViewInterface.View view;
    private ApiClient apiClient;

    private GoogleMap googleMap;
    private List<String> stringList = new ArrayList<>();
    private List<String> placeIDList = new ArrayList<>();
    private List<Marker> markerList = new ArrayList<>();
    private List<List<String>> photoList = new ArrayList<>();

    MainActivityPresenter(MainActivityViewInterface.View view) {
        this.view = view;
        apiClient = new ApiClient();
    }

    @Override
    public void onViewCreate() {
        view.setAdapter(stringList);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        /*if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }*/
        //googleMap.setMyLocationEnabled(true);
        /*googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
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
        });*/
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                for (int i = 0; i < markerList.size(); i++) {
                    if (markerList.get(i).equals(marker)) {
                        Log.d(TAG, placeIDList.get(i).toString());
                        Log.d(TAG, String.valueOf(photoList.get(i).size()));
                        PlaceDetailFragment placeDetailFragment = new PlaceDetailFragment();
                        Bundle arg = new Bundle();
                        arg.putString("placeID", placeIDList.get(i).toString());
                        arg.putStringArrayList("photo", (ArrayList<String>) photoList.get(i));
                        placeDetailFragment.setArguments(arg);
                        //getFragmentManager().beginTransaction().addToBackStack(null).add(android.R.id.content, placeDetailFragment).commit();
                        break;
                    }
                }
            }
        });
        this.googleMap = googleMap;
    }


    @Override
    public void onMyMarkerSet(MarkerOptions markerOptions) {
        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(markerOptions.getPosition()));
    }

    @Override
    public void onYouMarkerSet(MarkerOptions markerOptions) {
        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(markerOptions.getPosition()));
    }

    @Override
    public void onButtonClick(MarkerOptions markerOptions) {
        view.showProgressDialog();
        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(markerOptions.getPosition()));

    }
}
