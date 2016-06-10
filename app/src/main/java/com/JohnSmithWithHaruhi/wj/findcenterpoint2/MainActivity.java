package com.JohnSmithWithHaruhi.wj.findcenterpoint2;

import android.Manifest;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static String TAG = "MainActivity";

    private GoogleMap googleMap;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private FloatingActionButton floatingActionButton;
    private ProgressDialog progressDialog;
    private DialogFragment dialogFragment;
    private ListView listView;

    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> nameList = new ArrayList<>();
    private ArrayList<String> placeIDList = new ArrayList<>();
    private ArrayList<Marker> markerList = new ArrayList<>();
    private ArrayList<ArrayList<String>> photoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.wtf("onCreate", "asdjfklasjdfkljlksadf");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.main_map);
        mapFragment.getMapAsync(this);

        dialogFragment = new SelectFragment();

        toolbar = (Toolbar) findViewById(R.id.main_Toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawerLayout = (DrawerLayout) findViewById(R.id.main_DL);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(drawerToggle);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.main_FAB);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragment.show(getFragmentManager(), "selectFragment");
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading");

        listView = (ListView) findViewById(R.id.main_LV);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, nameList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drawerLayout.closeDrawers();
                markerList.get(position).showInfoWindow();
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(Constant.getMarkerList().get(position).getPosition()));
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.info_window, null);

                TextView name = (TextView) view.findViewById(R.id.info_name);
                name.setText(marker.getTitle());

                RatingBar ratingBar = (RatingBar) view.findViewById(R.id.info_RB);
                ratingBar.setRating(Float.valueOf(marker.getSnippet()));

                return view;
            }
        });
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                for (int i = 0; i < markerList.size(); i++) {
                    if (markerList.get(i).equals(marker)) {
                        Log.d(TAG, placeIDList.get(i).toString());
                        Log.d(TAG, String.valueOf(photoList.get(i).size()));
                        PlaceDetailFragment placeDetailFragment = new PlaceDetailFragment();
                        Bundle arg = new Bundle();
                        arg.putString("placeID", placeIDList.get(i).toString());
                        arg.putStringArrayList("photo", photoList.get(i));
                        placeDetailFragment.setArguments(arg);
                        getFragmentManager().beginTransaction().addToBackStack(null).add(android.R.id.content, placeDetailFragment).commit();
                        break;
                    }
                }
            }
        });
        this.googleMap = googleMap;
    }

    private void setMarkers() {
        googleMap.clear();
        if (Constant.getmMarker() != null) {
            googleMap.addMarker(Constant.getmMarker());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constant.getmMarker().getPosition(), 15));
        }
        if (Constant.getuMarker() != null) {
            googleMap.addMarker(Constant.getuMarker());
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constant.getuMarker().getPosition(), 15));
        }
        if (Constant.getCenterPoint() != null) {
            googleMap.addMarker(Constant.getCenterPoint()).showInfoWindow();
            googleMap.addPolyline(new PolylineOptions().add(Constant.getmMarker().getPosition(), Constant.getuMarker().getPosition())
                    .color(ContextCompat.getColor(this, R.color.colorPrimary)));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Constant.getCenterPoint().getPosition(), 15));
        }
        if (Constant.getMarkerList() != null) {
            for (int i = 0; i < Constant.getMarkerList().size(); i++) {
                markerList.add(googleMap.addMarker(Constant.getMarkerList().get(i)));
            }
        }
        progressDialog.dismiss();
    }

    public void LoadingData() {
        progressDialog.show();
        new PlaceSearchAsyncTask().execute();
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount()>0){
            getFragmentManager().popBackStack();
        }
    }

    private class PlaceSearchAsyncTask extends AsyncTask<Void, Void, JSONObject> {

        String urlStr;

        @Override
        protected void onPreExecute() {
            urlStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                    + Constant.getCenterPoint().getPosition().latitude + "," + Constant.getCenterPoint().getPosition().longitude
                    + "&rankby=distance&type=" + Constant.getType() + "&key=AIzaSyB6nijHVZRKORwvDtbC4Ux70w8k9mzW79c";
            nameList.clear();
            placeIDList.clear();
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
            Log.d(TAG, jsonObject.toString());
            ArrayList<MarkerOptions> arrayList = new ArrayList<>();
            try {
                JSONArray results = jsonObject.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat")
                            , results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng")))
                            .title(results.getJSONObject(i).getString("name"))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    arrayList.add(markerOptions);

                    if (results.getJSONObject(i).has("rating")) {
                        markerOptions.snippet(results.getJSONObject(i).getString("rating"));
                    } else {
                        markerOptions.snippet("0");
                    }

                    ArrayList<String> tempList = new ArrayList<>();
                    if (results.getJSONObject(i).has("photos")) {
                        JSONArray photos = results.getJSONObject(i).getJSONArray("photos");
                        for (int j = 0; j < photos.length(); j++) {
                            tempList.add(photos.getJSONObject(j).getString("photo_reference"));
                        }
                    }

                    photoList.add(tempList);
                    nameList.add(results.getJSONObject(i).getString("name"));
                    placeIDList.add(results.getJSONObject(i).getString("place_id"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Constant.setMarkerList(arrayList);
            arrayAdapter.notifyDataSetChanged();
            setMarkers();
        }
    }






}

