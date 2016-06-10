package com.JohnSmithWithHaruhi.wj.findcenterpoint2;

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class Constant {

    public Constant() {
    }

    public static MarkerOptions mMarker;
    public static MarkerOptions uMarker;
    public static MarkerOptions centerPoint;
    public static ArrayList<MarkerOptions> markerList = new ArrayList<>();
    public static String Type = "food";

    public static String getType() {
        return Type;
    }

    public static void setType(String type) {
        Type = type;
    }

    public static MarkerOptions getmMarker() {
        return mMarker;
    }

    public static void setmMarker(MarkerOptions mMarker) {
        Constant.mMarker = mMarker;
    }

    public static MarkerOptions getuMarker() {
        return uMarker;
    }

    public static void setuMarker(MarkerOptions uMarker) {
        Constant.uMarker = uMarker;
    }

    public static MarkerOptions getCenterPoint() {
        return centerPoint;
    }

    public static void setCenterPoint(MarkerOptions centerPoint) {
        Constant.centerPoint = centerPoint;
    }

    public static ArrayList<MarkerOptions> getMarkerList() {
        return markerList;
    }

    public static void setMarkerList(ArrayList<MarkerOptions> markerList) {
        Constant.markerList = markerList;
    }
}
