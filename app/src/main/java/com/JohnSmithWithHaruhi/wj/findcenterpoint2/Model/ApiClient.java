package com.JohnSmithWithHaruhi.wj.findcenterpoint2.Model;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by wj on 16/6/10.
 */
public class ApiClient {

    private Retrofit retrofit;
    private PlaceApi placeApi;

    public ApiClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        placeApi = retrofit.create(PlaceApi.class);
    }

    public PlaceApi getPlaceApi() {
        return placeApi;
    }
}
