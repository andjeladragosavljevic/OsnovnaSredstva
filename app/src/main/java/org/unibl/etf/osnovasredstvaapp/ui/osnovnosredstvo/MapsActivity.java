package org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.unibl.etf.osnovasredstvaapp.MainActivity;
import org.unibl.etf.osnovasredstvaapp.databinding.ActivityMapsBinding;
import org.unibl.etf.osnovasredstvaapp.R;

import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,  GoogleMap.OnInfoWindowClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        org.unibl.etf.osnovasredstvaapp.databinding.ActivityMapsBinding binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
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
        double latitude = Double.parseDouble(Objects.requireNonNull(getIntent().getStringExtra("LATITUDE")));
        double longitude = Double.parseDouble(Objects.requireNonNull(getIntent().getStringExtra("LONGITUDE")));
        String nazivLokacije = getIntent().getStringExtra("NAZIV_LOKACIJE");
        int lokacijaId = getIntent().getIntExtra("LOKACIJA_ID", 0); // Preuzmite lokacija ID iz Intent-a


        LatLng lokacija = new LatLng(latitude, longitude);

        Marker marker = googleMap.addMarker(new MarkerOptions().position(lokacija).title(nazivLokacije));
        assert marker != null;
        marker.setTag(lokacijaId);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokacija, 15));

        googleMap.setOnInfoWindowClickListener(this);

    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker){
        Object tag = marker.getTag();
        int lokacijaId = 0;

        if (tag != null)
            lokacijaId = (int) tag;

        Bundle args = new Bundle();
        args.putInt("LOKACIJA_ID", lokacijaId);

        // Kreiranje Intent-a za prelazak iz ove aktivnosti ka aktivnosti koja hostuje fragmente
        Intent intent = new Intent(MapsActivity.this, MainActivity.class);  // MainActivity je host za fragmente
        intent.putExtras(args);
        startActivity(intent);
    }


}