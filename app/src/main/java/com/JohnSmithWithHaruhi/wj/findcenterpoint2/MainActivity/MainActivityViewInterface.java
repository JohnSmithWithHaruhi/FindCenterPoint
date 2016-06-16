package com.JohnSmithWithHaruhi.wj.findcenterpoint2.MainActivity;

import android.content.Context;

import com.JohnSmithWithHaruhi.wj.findcenterpoint2.Model.Unit.NearbySearch.Photo;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

/**
 * Created by wj on 16/6/10.
 */
public interface MainActivityViewInterface {
    interface View {
        void setAdapter(List<String> list);

        void setGoogleMapInfoWindow(List<Marker> markerList, List<String> placeIDList, List<List<Photo>> photoList);

        void notifyAdapterChange();

        void showProgressDialog();

        void dismissProgressDialog();

        Context getContextCompat();
    }

    interface Presenter {
        void onViewCreate();

        void onMapReady(GoogleMap googleMap);

        void onListClick(Integer position);
    }
}
