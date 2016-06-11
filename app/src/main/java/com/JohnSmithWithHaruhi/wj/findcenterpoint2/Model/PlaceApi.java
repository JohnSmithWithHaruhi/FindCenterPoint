package com.JohnSmithWithHaruhi.wj.findcenterpoint2.Model;

import com.JohnSmithWithHaruhi.wj.findcenterpoint2.Model.Unit.NearbySearch.NearbySearch;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by wj on 16/6/10.
 */
public interface PlaceApi {
    @GET("maps/api/place/nearbysearch/json")
    Observable<NearbySearch> getNearbySearch(@Query("key") String key,
                                             @Query("language") String language,
                                             @Query("location") String location,
                                             @Query("rankby") String rankBy,
                                             @Query("type") String type,
                                             @Query("pagetoken") String pageToken);

    /*@GET("place/details/json")
    Observable<Place> getPlaceDetail(@Query("language") String language,
                                     @Query("key") String key,
                                     @Query("placeid") String placeID);*/

}
