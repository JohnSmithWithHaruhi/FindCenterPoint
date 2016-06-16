package com.JohnSmithWithHaruhi.wj.findcenterpoint2.MainActivity;

import android.support.v4.content.ContextCompat;

import com.JohnSmithWithHaruhi.wj.findcenterpoint2.Model.ApiClient;
import com.JohnSmithWithHaruhi.wj.findcenterpoint2.Model.Unit.NearbySearch.NearbySearch;
import com.JohnSmithWithHaruhi.wj.findcenterpoint2.Model.Unit.NearbySearch.Photo;
import com.JohnSmithWithHaruhi.wj.findcenterpoint2.Model.Unit.NearbySearch.Result;
import com.JohnSmithWithHaruhi.wj.findcenterpoint2.R;
import com.JohnSmithWithHaruhi.wj.findcenterpoint2.Unit.Constant;
import com.JohnSmithWithHaruhi.wj.findcenterpoint2.Unit.SelectFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wj on 16/6/10.
 */
public class MainActivityPresenter implements MainActivityViewInterface.Presenter, SelectFragment.SelectFragmentListener {

    private final static String TAG = "MainActivityPresenter";
    private MainActivityViewInterface.View view;
    private ApiClient apiClient;

    private final static float MarkerColor = BitmapDescriptorFactory.HUE_YELLOW;
    private GoogleMap googleMap;
    private Marker myMarker;
    private Marker yourMarker;
    private Marker centerMarker;
    private Polyline polyline;

    private List<String> placeIDList = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();
    private List<Marker> markerList = new ArrayList<>();
    private List<List<Photo>> photoList = new ArrayList<>();

    public MainActivityPresenter(MainActivityViewInterface.View view) {
        this.view = view;
        apiClient = new ApiClient();
    }

    private void loadData(final MarkerOptions markerOptions, String type) {

        if (!markerList.isEmpty()) {
            for (Marker marker : markerList) {
                marker.remove();
            }
            markerList.clear();
            nameList.clear();
            placeIDList.clear();
            photoList.clear();
        }

        apiClient.getPlaceApi().getNearbySearch(
                Constant.GOOGLE_KEY, "ja",
                markerOptions.getPosition().latitude + "," + markerOptions.getPosition().longitude, "distance", type, null)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<NearbySearch>() {
                    @Override
                    public void onCompleted() {
                        view.notifyAdapterChange();
                        view.setGoogleMapInfoWindow(markerList, placeIDList, photoList);
                        view.dismissProgressDialog();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(NearbySearch nearbySearch) {
                        for (Result result : nearbySearch.getResults()) {
                            MarkerOptions tempMarkerOptions = new MarkerOptions()
                                    .position(new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng()))
                                    .title(result.getName())
                                    .icon(BitmapDescriptorFactory.defaultMarker(MarkerColor));

                            if (result.getRating() != null) {
                                tempMarkerOptions.snippet(result.getRating().toString());
                            } else {
                                tempMarkerOptions.snippet("0");
                            }

                            markerList.add(googleMap.addMarker(tempMarkerOptions));
                            nameList.add(result.getName());
                            placeIDList.add(result.getPlaceId());
                            photoList.add(result.getPhotos());
                        }
                    }
                });
    }

    @Override
    public void onViewCreate() {
        view.setAdapter(nameList);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    public void onListClick(Integer position) {
        markerList.get(position).showInfoWindow();
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(markerList.get(position).getPosition()));
    }

    @Override
    public void onMyMarkerSet(MarkerOptions markerOptions) {
        if (myMarker != null) {
            myMarker.remove();
        }
        myMarker = googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(markerOptions.getPosition()));
    }

    @Override
    public void onYouMarkerSet(MarkerOptions markerOptions) {
        if (yourMarker != null) {
            yourMarker.remove();
        }
        yourMarker = googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(markerOptions.getPosition()));
    }

    @Override
    public void onButtonClick(MarkerOptions markerOptions, String type) {
        view.showProgressDialog();
        if (centerMarker != null) {
            centerMarker.remove();
        }
        centerMarker = googleMap.addMarker(markerOptions);

        if (polyline != null) {
            polyline.remove();
        }
        polyline = googleMap.addPolyline(new PolylineOptions().add(yourMarker.getPosition(), myMarker.getPosition()).color(ContextCompat.getColor(view.getContextCompat(), R.color.colorPrimary)));

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(markerOptions.getPosition(), 15));
        loadData(markerOptions, type);
    }
}
