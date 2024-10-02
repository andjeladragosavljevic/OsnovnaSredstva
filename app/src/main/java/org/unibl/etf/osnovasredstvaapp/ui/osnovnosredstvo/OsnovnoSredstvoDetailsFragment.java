package org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.unibl.etf.osnovasredstvaapp.R;
import org.unibl.etf.osnovasredstvaapp.dao.LokacijaDao;
import org.unibl.etf.osnovasredstvaapp.dao.ZaposleniDao;
import org.unibl.etf.osnovasredstvaapp.database.AppDatabase;
import org.unibl.etf.osnovasredstvaapp.entity.Lokacija;
import org.unibl.etf.osnovasredstvaapp.entity.OsnovnoSredstvo;
import org.unibl.etf.osnovasredstvaapp.entity.Zaposleni;
import org.unibl.etf.osnovasredstvaapp.ui.lokacija.LokacijaFragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;


public class OsnovnoSredstvoDetailsFragment extends Fragment {
    private TextView textViewNaziv, textViewOpis, textViewBarkod, textViewCijena, textViewDatumKreiranja, textViewOsoba, textViewLokacija;
    private ImageView imageViewSlika;

    private OsnovnoSredstvo osnovnoSredstvo;

    private ZaposleniDao zaposleniDao;
    private LokacijaDao lokacijaDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_osnovno_sredstvo_details, container, false);

        textViewNaziv = view.findViewById(R.id.textViewNaziv);
        textViewOpis = view.findViewById(R.id.textViewOpis);
        textViewBarkod = view.findViewById(R.id.textViewBarkod);
        textViewCijena = view.findViewById(R.id.textViewCijena);
        textViewDatumKreiranja = view.findViewById(R.id.textViewDatumKreiranja);
        textViewOsoba = view.findViewById(R.id.textViewOsoba);
        textViewLokacija = view.findViewById(R.id.textViewLokacija);
        imageViewSlika = view.findViewById(R.id.imageViewSlika);

        AppDatabase db = AppDatabase.getInstance(getContext());
        zaposleniDao = db.zaposleniDao();
        lokacijaDao = db.lokacijaDao();


        osnovnoSredstvo = (OsnovnoSredstvo) getArguments().getSerializable("osnovnoSredstvo");
        if (osnovnoSredstvo != null) {
            prikaziDetalje(osnovnoSredstvo);
        }

        // Klik na lokaciju otvara mapu
        textViewLokacija.setOnClickListener(v -> otvoriLokacijuNaMapi());

        return view;
    }

    private void prikaziDetalje(OsnovnoSredstvo osnovnoSredstvo) {
        textViewNaziv.setText(osnovnoSredstvo.getNaziv());
        textViewOpis.setText(osnovnoSredstvo.getOpis());
        textViewBarkod.setText(osnovnoSredstvo.getBarkod());
        textViewCijena.setText(String.valueOf(osnovnoSredstvo.getCijena()));
        textViewDatumKreiranja.setText(osnovnoSredstvo.getDatumKreiranja());
        textViewOsoba.setText(String.valueOf(osnovnoSredstvo.getZaduzenaOsobaId()));
        textViewLokacija.setText(String.valueOf(osnovnoSredstvo.getZaduzenaLokacijaId()));

        Zaposleni zaposleni = zaposleniDao.getById(osnovnoSredstvo.getZaduzenaOsobaId());
        if (zaposleni != null) {
            textViewOsoba.setText(zaposleni.getIme() + " " + zaposleni.getPrezime());
        }

        // Dohvati lokaciju i postavi
        Lokacija lokacija = lokacijaDao.getById(osnovnoSredstvo.getZaduzenaLokacijaId());
        if (lokacija != null) {
            textViewLokacija.setText(lokacija.getAdresa() + ", " + lokacija.getGrad());
        }



        if (osnovnoSredstvo.getSlikaPath() != null) {
                String currentPhotoPath = osnovnoSredstvo.getSlikaPath();
                File imgFile = new File(currentPhotoPath);
                if (imgFile.exists()) {
                    imageViewSlika.setImageURI(Uri.fromFile(imgFile)); // Prikaz slike

            }
        }


    }
    private void loadImageFromPath(String imagePath) {
        Log.d("IMAGEPATH", imagePath);
        if (imagePath != null && !imagePath.isEmpty()) {
            Uri uri = Uri.parse(imagePath); // Kreiramo URI iz putanje

            if (uri != null && uri.getScheme() != null) {
                if (uri.getScheme().equals("content")) {

                    if (!isRestrictedProvider(uri)) {
                        try {
                            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            imageViewSlika.setImageBitmap(bitmap); // Prikaz slike
                            Log.d("IMAGE_LOADED", "Image loaded from URI.");
                        } catch (FileNotFoundException e) {
                            Log.e("IMAGE_ERROR", "Slika nije pronađena: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("PROVIDER_ERROR", "URI je iz nedostupnog provajdera, ne može se pristupiti.");
                    }
                } else {
                    // Ako je putanja fajl sistemska, koristimo BitmapFactory.decodeFile()
                    File imgFile = new File(imagePath);
                    if (imgFile.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        imageViewSlika.setImageBitmap(bitmap); // Prikaz slike
                        Log.d("IMAGE_LOADED", "Image loaded from file path.");
                    } else {
                        Log.e("FILE_ERROR", "Fajl ne postoji na putanji: " + imagePath);
                    }
                }
            } else {
                Log.e("IMAGEPATH_ERROR", "Putanja slike je null ili prazna.");
            }
        }
    }

    private boolean isRestrictedProvider(Uri uri) {
        // Ograničeni provajderi, dodajte po potrebi
        return "com.miui.gallery.provider".equals(uri.getAuthority()) ||
                "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority());
    }



    private void otvoriLokacijuNaMapi() {
        Lokacija lokacija = lokacijaDao.getById(osnovnoSredstvo.getZaduzenaLokacijaId());

        if (lokacija != null) {
            // Prosledite latitude i longitude vrednosti u Intent
            Intent intent = new Intent(getContext(), MapsActivity.class);
            intent.putExtra("LATITUDE", lokacija.getLatitude());
            intent.putExtra("LONGITUDE", lokacija.getLongitude());
            intent.putExtra("NAZIV_LOKACIJE", lokacija.getGrad() + ", " + lokacija.getAdresa());
            intent.putExtra("LOKACIJA_ID", lokacija.getId());

            startActivity(intent);  // Pokrenite MapsActivity
        } else {
            Toast.makeText(getContext(), "Lokacija nije pronađena", Toast.LENGTH_SHORT).show();
        }
    }



}