package ca.obrassard.inquirio.activities;

import android.app.ProgressDialog;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ca.obrassard.inquirio.R;

public class MapDetail extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    ProgressDialog progressDialog ;

    private void beginLoading() {
        if (progressDialog == null)
        progressDialog = ProgressDialog.show(MapDetail.this,
                "Veuillez patienter",null,true);
    }


    private void endLoading() {
        if (progressDialog != null){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        beginLoading();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_detail);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Bundle i = getIntent().getExtras();
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        mMap.setMinZoomPreference(17);
        LatLng position = new LatLng(i.getDouble("lat"), i.getDouble("lng"));
        mMap.addMarker(new MarkerOptions().position(position).title("Lost item position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
        endLoading();
    }
}
