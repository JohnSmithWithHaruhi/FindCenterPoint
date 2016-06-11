package com.JohnSmithWithHaruhi.wj.findcenterpoint2.MainActivity;

import com.google.android.gms.maps.GoogleMap;

import java.util.List;

/**
 * Created by wj on 16/6/10.
 */
public interface MainActivityViewInterface {
    interface View {
        void setAdapter(List<String> list);

        void setGoogleMap(GoogleMap googleMap);

        void showProgressDialog();

        void dismissProgressDialog();
    }

    interface Presenter {
        void onViewCreate();
    }
}
