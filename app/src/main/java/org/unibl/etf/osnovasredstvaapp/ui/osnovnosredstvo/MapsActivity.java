package org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,  GoogleMap.OnInfoWindowClickListener{

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        mMap = googleMap;
        Log.d("MAPS", getIntent().getStringExtra("LATITUDE"));
        double latitude = Double.parseDouble(getIntent().getStringExtra("LATITUDE"));
        double longitude = Double.parseDouble(getIntent().getStringExtra("LONGITUDE"));
        String nazivLokacije = getIntent().getStringExtra("NAZIV_LOKACIJE");
        int lokacijaId = getIntent().getIntExtra("LOKACIJA_ID", 0); // Preuzmite lokacija ID iz Intent-a


        LatLng lokacija = new LatLng(latitude, longitude);

       Marker marker = mMap.addMarker(new MarkerOptions().position(lokacija).title(nazivLokacije));
        marker.setTag(lokacijaId);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokacija, 15));

        mMap.setOnInfoWindowClickListener(this);

    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker){
        int lokacijaId = (int) marker.getTag();


        Bundle args = new Bundle();
        args.putInt("LOKACIJA_ID", lokacijaId);

        // kREIRANJE Intent-a za prelazak iz ove aktivnosti ka aktivnosti koja hostuje fragmente
        Intent intent = new Intent(MapsActivity.this, MainActivity.class);  // MainActivity je host za fragmente
        intent.putExtras(args);  // Prosledjivanje argumenata
        startActivity(intent);
    }


}