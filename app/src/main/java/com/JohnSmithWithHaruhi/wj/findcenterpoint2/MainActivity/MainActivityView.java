package com.JohnSmithWithHaruhi.wj.findcenterpoint2.MainActivity;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.JohnSmithWithHaruhi.wj.findcenterpoint2.R;
import com.JohnSmithWithHaruhi.wj.findcenterpoint2.Selector.SelectFragment;
import com.JohnSmithWithHaruhi.wj.findcenterpoint2.databinding.ActivityMainBinding;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import java.util.List;

public class MainActivityView extends AppCompatActivity implements MainActivityViewInterface.View {

    private final static String TAG = "MainActivityView";
    private ActivityMainBinding binding;
    private MainActivityPresenter presenter;

    private MapFragment mapFragment;
    private GoogleMap googleMap;

    private ActionBarDrawerToggle drawerToggle;
    private ProgressDialog progressDialog;
    private SelectFragment selectFragment;
    private ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        presenter = new MainActivityPresenter(this);
        presenter.onViewCreate();

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.main_map);
        mapFragment.getMapAsync(presenter);

        selectFragment = new SelectFragment();
        selectFragment.setListener(presenter);


        setActionBar(binding.mainToolbar);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(this, binding.mainDL, R.string.open_drawer, R.string.close_drawer);
        binding.mainDL.addDrawerListener(drawerToggle);

        binding.mainFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFragment.show(getFragmentManager(), "selectFragment");
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Loading");
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
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void setAdapter(List<String> list) {
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        binding.mainLV.setAdapter(arrayAdapter);
        binding.mainLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                binding.mainDL.closeDrawers();

                //TODO
                //presenter.onItemClick();
                /*markerList.get(position).showInfoWindow();
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(Constant.getMarkerList().get(position).getPosition()));*/
            }
        });
    }

    @Override
    public void setGoogleMap(GoogleMap googleMap) {

    }

    @Override
    public void showProgressDialog() {
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }
    }

    @Override
    public void dismissProgressDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /*private class PlaceSearchAsyncTask extends AsyncTask<Void, Void, JSONObject> {

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
    }*/


}

