package com.JohnSmithWithHaruhi.wj.findcenterpoint2.PlaceDetail;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.JohnSmithWithHaruhi.wj.findcenterpoint2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */

public class PlaceDetailFragment extends android.app.Fragment {

    public PlaceDetailFragment() {
        // Required empty public constructor
    }

    private static String TAG = "PlaceDetailFragment";

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private TextView address;
    private TextView phone;
    private TextView url;
    private TextView time;
    private ImageView photo;

    private String placeID;
    private ArrayList<String> photoList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_detail, container, false);
        collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.place_CTL);
        collapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(getActivity(), R.color.colorWhite));
        toolbar = (Toolbar) view.findViewById(R.id.place_Toolbar);

        address = (TextView) view.findViewById(R.id.place_place);
        phone = (TextView) view.findViewById(R.id.place_phone);
        url = (TextView) view.findViewById(R.id.place_url);
        time = (TextView) view.findViewById(R.id.place_time);
        photo = (ImageView) view.findViewById(R.id.place_IV);

        Bundle arg = getArguments();
        placeID = arg.getString("placeID");
        photoList = arg.getStringArrayList("photo");

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        if (!photoList.isEmpty()) {
            new PhotoAsyncTask(0).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        new PlaceSearchAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private class PlaceSearchAsyncTask extends AsyncTask<Void, Void, JSONObject> {

        String urlStr;

        @Override
        protected void onPreExecute() {
            urlStr = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + placeID + "&key=AIzaSyB6nijHVZRKORwvDtbC4Ux70w8k9mzW79c";
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            HttpURLConnection httpURLConnection = null;
            try {
                URL url = new URL(urlStr);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                BufferedInputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    if (length > 0) {
                        outputStream.write(buffer, 0, length);
                    }
                }
                return new JSONObject(new String(outputStream.toByteArray()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            try {
                JSONObject result = jsonObject.getJSONObject("result");

                toolbar.setTitle(result.getString("name"));
                address.setText(result.getString("formatted_address"));
                if (result.has("formatted_phone_number")) {
                    phone.setText(result.getString("formatted_phone_number"));
                }
                if (result.has("website")) {
                    url.setText(result.getString("website"));
                }

                if (result.has("opening_hours")) {
                    ArrayList<String> tempStr = new ArrayList<>();
                    JSONArray opening_hours = result.getJSONObject("opening_hours").getJSONArray("weekday_text");
                    for (int i = 0; i < opening_hours.length(); i++) {
                        tempStr.add(opening_hours.getString(i));
                        if (i != opening_hours.length() - 1) {
                            tempStr.add("\n");
                        }
                    }
                    time.setText(tempStr.toString().replace("[", "").replace("]", "").replace(",", "").replace(" ", ""));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class PhotoAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        private String urlStr;
        private int i;

        PhotoAsyncTask(int i) {
            this.i = i;
        }

        @Override
        protected void onPreExecute() {
            urlStr = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" + photoList.get(i)
                    + "&key=AIzaSyB6nijHVZRKORwvDtbC4Ux70w8k9mzW79c";
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();
                return BitmapFactory.decodeStream(httpURLConnection.getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            photo.setImageBitmap(bitmap);
        }
    }

}
