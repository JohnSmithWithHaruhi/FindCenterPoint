package com.JohnSmithWithHaruhi.wj.findcenterpoint2.PlaceDetail;

import android.content.Intent;
import android.databinding.ObservableField;
import android.util.Log;
import android.view.View;

import com.JohnSmithWithHaruhi.wj.findcenterpoint2.Model.ApiClient;
import com.JohnSmithWithHaruhi.wj.findcenterpoint2.Model.Unit.Details.Details;
import com.JohnSmithWithHaruhi.wj.findcenterpoint2.Model.Unit.Details.Photo;
import com.JohnSmithWithHaruhi.wj.findcenterpoint2.Unit.Constant;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by wj on 16/6/14.
 */
public class PlaceDetailViewModel {

    private final static String TAG = "PlaceDetailViewModel";

    public final ObservableField<String> title = new ObservableField<>();
    public final ObservableField<String> address = new ObservableField<>();
    public final ObservableField<String> phone = new ObservableField<>();
    public final ObservableField<String> url = new ObservableField<>();
    public final ObservableField<String> time = new ObservableField<>();
    public final ObservableField<String> shareUrl = new ObservableField<>();

    private ApiClient apiClient;
    private PlaceDetailView view;

    public PlaceDetailViewModel(PlaceDetailView view) {
        this.view = view;
        apiClient = new ApiClient();
    }

    public void onCreateView(String placeId) {
        apiClient.getPlaceApi().getPlaceDetail("AIzaSyB6nijHVZRKORwvDtbC4Ux70w8k9mzW79c", "ja", placeId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Details>() {
                    @Override
                    public void onCompleted() {
                        //TODO here
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, e.toString());
                    }

                    @Override
                    public void onNext(Details details) {

                        checkNull(details.getResult().getName(), title);
                        checkNull(details.getResult().getFormattedPhoneNumber(), phone);
                        checkNull(details.getResult().getFormattedAddress(), address);
                        checkNull(details.getResult().getWebsite(), url);

                        if (details.getResult().getOpeningHours() != null && details.getResult().getOpeningHours().getWeekdayText() != null) {
                            StringBuilder tempStr = new StringBuilder();
                            List<String> list = details.getResult().getOpeningHours().getWeekdayText();
                            for (String s : list) {
                                tempStr.append(s).append("\n");
                            }
                            time.set(tempStr.toString().substring(0, tempStr.length() - 1));
                            view.showTimeCardView();
                        }

                        if (details.getResult().getPhotos() != null) {
                            List<Photo> photos = details.getResult().getPhotos();
                            for (Photo photo : photos) {
                                Log.d(TAG, photo.getPhotoReference());
                            }
                        }

                        if (details.getResult().getUrl() != null) {
                            shareUrl.set(details.getResult().getUrl());
                        }
                    }
                });
    }

    public void onFABClick(View view) {
        if (shareUrl.get() != null) {
            Log.d(TAG, "onFABClick " + shareUrl.get());
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareUrl.get());
            sendIntent.setType("text/plain");
            this.view.startActivity(sendIntent);
        }
    }

    private void checkNull(String s, ObservableField<String> observableField) {
        if (s != null) {
            observableField.set(s);
        } else {
            observableField.set(Constant.NULL_TEXT);
        }
    }
}
