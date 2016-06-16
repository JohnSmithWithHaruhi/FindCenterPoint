package com.JohnSmithWithHaruhi.wj.findcenterpoint2.PlaceDetail;


import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.JohnSmithWithHaruhi.wj.findcenterpoint2.MainActivity.MainActivityView;
import com.JohnSmithWithHaruhi.wj.findcenterpoint2.R;
import com.JohnSmithWithHaruhi.wj.findcenterpoint2.databinding.FragmentPlaceDetailBinding;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */

public class PlaceDetailView extends android.app.Fragment {

    public PlaceDetailView() {
    }

    private final static String TAG = "PlaceDetailView";
    private FragmentPlaceDetailBinding binding;
    private PlaceDetailViewModel viewModel;

    private Toolbar toolbar;
    private ImageView photo;

    private String placeID = "";
    private String photoUrl = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        viewModel = new PlaceDetailViewModel(this);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_place_detail, container, false);

        View view = binding.getRoot();

        binding.setViewModel(viewModel);
        photo = binding.placePhoto;
        toolbar = binding.placeToolbar;

        Drawable upArrow = ContextCompat.getDrawable(getActivity(), R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        ((MainActivityView) getActivity()).setSupportActionBar(toolbar);
        ((MainActivityView) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivityView) getActivity()).getSupportActionBar().setHomeAsUpIndicator(upArrow);
        ((MainActivityView) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);


        Bundle arg = getArguments();
        placeID = arg.getString("placeID");
        photoUrl = arg.getString("photoUrl", "");

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!photoUrl.equals("")) {
            setPhoto(photoUrl);
        } else {
            photo.setVisibility(View.GONE);
        }
        viewModel.onCreateView(placeID);
    }

    public void showTimeCardView() {
        binding.placeTimeCV.setVisibility(View.VISIBLE);
    }

    private void setPhoto(String s) {
        Picasso.with(getActivity())
                .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photoUrl + "&key=AIzaSyB6nijHVZRKORwvDtbC4Ux70w8k9mzW79c")
                .into(photo);
    }

}
