package com.JohnSmithWithHaruhi.wj.findcenterpoint2.Selector;


import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import com.JohnSmithWithHaruhi.wj.findcenterpoint2.Constant;
import com.JohnSmithWithHaruhi.wj.findcenterpoint2.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class SelectFragment extends DialogFragment {

    private final static String TAG = "SelectFragment";
    private final static float myMarkerColor = BitmapDescriptorFactory.HUE_ORANGE;
    private final static float yourMarkerColor = BitmapDescriptorFactory.HUE_ORANGE;
    private final static float centerMarkerColor = BitmapDescriptorFactory.HUE_RED;

    private SelectFragmentListener listener;

    private PlaceAutocompleteFragment myAutocompleteFragment;
    private PlaceAutocompleteFragment yourAutocompleteFragment;

    private MarkerOptions myMarker;
    private MarkerOptions yourMarker;
    private RadioGroup radioGroup;
    private Button button;

    public SelectFragment() {
    }

    public interface SelectFragmentListener {
        void onMyMarkerSet(MarkerOptions markerOptions);

        void onYouMarkerSet(MarkerOptions markerOptions);

        void onButtonClick(MarkerOptions markerOptions);
    }

    public void setListener(SelectFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_select, container, false);

        myAutocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.select_myPoint);
        myAutocompleteFragment.setHint(getString(R.string.my_position));
        myAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                myMarker = new MarkerOptions().position(place.getLatLng())
                        .title(place.getName().toString())
                        .snippet("0"/*place.getAddress().toString()*/)
                        .icon(BitmapDescriptorFactory.defaultMarker(myMarkerColor));
                listener.onMyMarkerSet(myMarker);
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        yourAutocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.select_youPoint);
        yourAutocompleteFragment.setHint(getString(R.string.your_position));
        yourAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                yourMarker = new MarkerOptions().position(place.getLatLng())
                        .title(place.getName().toString())
                        .snippet("0")
                        .icon(BitmapDescriptorFactory.defaultMarker(yourMarkerColor));
                listener.onYouMarkerSet(yourMarker);
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
                if (myMarker != null && yourMarker != null) {
                    Double lat = (myMarker.getPosition().latitude + yourMarker.getPosition().latitude) / 2;
                    Double lng = (myMarker.getPosition().longitude + yourMarker.getPosition().longitude) / 2;

                    listener.onButtonClick(new MarkerOptions().title("Center Point")
                            .position(new LatLng(lat, lng))
                            .icon(BitmapDescriptorFactory.defaultMarker(centerMarkerColor))
                            .snippet("0"));
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
        if (yourAutocompleteFragment != null) {
            getFragmentManager().beginTransaction().remove(yourAutocompleteFragment).commit();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Select two points.");
        return dialog;
    }

}