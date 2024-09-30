package org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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


        // Prikaz slike
        if (osnovnoSredstvo.getSlika() != null) {
            prikaziSliku(osnovnoSredstvo.getSlika());
        }
    }

    private void prikaziSliku(String putanjaSlike) {
        // Slično ranijem načinu prikaza slike
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