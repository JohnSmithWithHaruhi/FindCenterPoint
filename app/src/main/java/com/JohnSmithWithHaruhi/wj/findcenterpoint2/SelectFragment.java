package com.JohnSmithWithHaruhi.wj.findcenterpoint2;


import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class SelectFragment extends DialogFragment {

    private static String TAG = "SelectFragment";

    private PlaceAutocompleteFragment myAutocompleteFragment;
    private PlaceAutocompleteFragment youAutocompleteFragment;
    private Button button;
    private RadioGroup radioGroup;

    public SelectFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select, container, false);

        myAutocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.select_myPoint);
        myAutocompleteFragment.setHint(getString(R.string.my_position));
        myAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Constant.setmMarker(new MarkerOptions().position(place.getLatLng())
                        .title(place.getName().toString())
                        .snippet("0"/*place.getAddress().toString()*/)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        youAutocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.select_youPoint);
        youAutocompleteFragment.setHint(getString(R.string.your_position));
        youAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Constant.setuMarker(new MarkerOptions().position(place.getLatLng())
                        .title(place.getName().toString())
                        .snippet("0")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        button = (Button) view.findViewById(R.id.select_Button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Constant.getmMarker() != null && Constant.getuMarker() != null) {
                    Double lat = (Constant.getmMarker().getPosition().latitude + Constant.getuMarker().getPosition().latitude) / 2;
                    Double lng = (Constant.getmMarker().getPosition().longitude + Constant.getuMarker().getPosition().longitude) / 2;
                    Constant.setCenterPoint(new MarkerOptions().title("Center Point")
                            .position(new LatLng(lat, lng))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .snippet("0"));
                    MainActivity activity = (MainActivity) getActivity();
                    activity.LoadingData();
                }
                getDialog().dismiss();
            }
        });

        radioGroup = (RadioGroup) view.findViewById(R.id.select_RG);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.select_RB1:
                        Constant.setType("food");
                        break;
                    case R.id.select_RB2:
                        Constant.setType("restaurant");
                        break;
                    case R.id.select_RB3:
                        Constant.setType("cafe");
                        break;
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (myAutocompleteFragment != null) {
            getFragmentManager().beginTransaction().remove(myAutocompleteFragment).commit();
        }
        if (youAutocompleteFragment != null) {
            getFragmentManager().beginTransaction().remove(youAutocompleteFragment).commit();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Select two points.");
        return dialog;
    }
}