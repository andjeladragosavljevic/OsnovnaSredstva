package org.unibl.etf.osnovasredstvaapp.ui.popisnastavka;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.unibl.etf.osnovasredstvaapp.R;
import org.unibl.etf.osnovasredstvaapp.dao.LokacijaDao;
import org.unibl.etf.osnovasredstvaapp.dao.OsnovnoSredstvoDao;
import org.unibl.etf.osnovasredstvaapp.dao.PopisnaStavkaDao;
import org.unibl.etf.osnovasredstvaapp.dao.ZaposleniDao;
import org.unibl.etf.osnovasredstvaapp.database.AppDatabase;
import org.unibl.etf.osnovasredstvaapp.entity.Lokacija;
import org.unibl.etf.osnovasredstvaapp.entity.OsnovnoSredstvo;
import org.unibl.etf.osnovasredstvaapp.entity.PopisnaStavka;
import org.unibl.etf.osnovasredstvaapp.entity.Zaposleni;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddPopisnaStavkaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPopisnaStavkaFragment extends Fragment {
    private EditText trenutnaOsobaEditText, trenutnaLokacijaEditText;
    private AutoCompleteTextView novaOsobaEditText, novaLokacijaEditText, osnovnoSredstvoAutoCompleteTextView;
    private Button saveStavkaButton;
    private int popisnaListaId;
    private int popisnaStavkaId;
    private boolean isSkeniranje;
    private OsnovnoSredstvoDao osnovnoSredstvoDao;
    private PopisnaStavka trenutnaStavka;
    private PopisnaStavkaDao popisnaStavkaDao;
    private ZaposleniDao zaposleniDao;
    private LokacijaDao lokacijaDao;

    // Privatne promenljive za čuvanje ID vrednosti
    private int trenutnaOsobaId;
    private int novaOsobaId;
    private int trenutnaLokacijaId;
    private int novaLokacijaId;

    private PopisnaStavka popisnaStavkaZaAzuriranje;
    private static final String ARG_PS = "popisnaStavka";

    public static AddPopisnaStavkaFragment newInstance(int popisnaListaId, boolean isSkeniranje) {
        AddPopisnaStavkaFragment fragment = new AddPopisnaStavkaFragment();
        Bundle args = new Bundle();
        args.putInt("popisnaListaId", popisnaListaId);
        args.putBoolean("isSkeniranje", isSkeniranje);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            popisnaListaId = getArguments().getInt("popisnaListaId");
            isSkeniranje = getArguments().getBoolean("isSkeniranje");
            popisnaStavkaZaAzuriranje = (PopisnaStavka) getArguments().getSerializable(ARG_PS);
        }

        osnovnoSredstvoDao = AppDatabase.getInstance(getContext()).osnovnoSredstvoDao();
        popisnaStavkaDao = AppDatabase.getInstance(getContext()).popisnaStavkaDao();
        zaposleniDao = AppDatabase.getInstance(getContext()).zaposleniDao();
        lokacijaDao = AppDatabase.getInstance(getContext()).lokacijaDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_popisna_stavka, container, false);

        osnovnoSredstvoAutoCompleteTextView = view.findViewById(R.id.osnovnoSredstvoEditText);
        trenutnaOsobaEditText = view.findViewById(R.id.trenutnaOsobaEditText);
        novaOsobaEditText = view.findViewById(R.id.novaOsobaEditText);
        trenutnaLokacijaEditText = view.findViewById(R.id.trenutnaLokacijaEditText);
        novaLokacijaEditText = view.findViewById(R.id.novaLokacijaEditText);
        saveStavkaButton = view.findViewById(R.id.saveStavkaButton);
        trenutnaOsobaEditText.setEnabled(false);
        trenutnaLokacijaEditText.setEnabled(false);

        postaviAutoCompleteAdapterZaOsnovnaSredstva();
        postaviAutoCompleteAdapterZaOsobe();
        postaviAutoCompleteAdapterZaLokacije();

        if (popisnaStavkaZaAzuriranje != null) {
            new OsGetTask(this, osnovnoSredstvoDao, popisnaStavkaZaAzuriranje.getOsnovnoSredstvoId()).execute();

        }

        if (isSkeniranje) {
            skenirajBarkod();
        }

        saveStavkaButton.setOnClickListener(v -> {
            saveStavka();
        });

        return view;
    }

    public void updateList(OsnovnoSredstvo os) {
        if (trenutnaStavka == null) {
            trenutnaStavka = new PopisnaStavka();  // Inicijalizuj objekat ako je null
        }
        osnovnoSredstvoAutoCompleteTextView.setText(os.getNaziv());
        trenutnaStavka.setOsnovnoSredstvoId(os.getId());
        trenutnaStavka.setPopisnaListaId(popisnaListaId);
        popuniFormuSaPodacima(os);
    }

    private void saveStavka() {
        if (trenutnaStavka == null || trenutnaStavka.getOsnovnoSredstvoId() == 0) {
            Toast.makeText(getContext(), getString(R.string.popunite_sva_polja), Toast.LENGTH_SHORT).show();
            return;
        }

        // Postavljanje potrebnih ID vrednosti
        trenutnaStavka.setTrenutnaOsobaId(trenutnaOsobaId);
        trenutnaStavka.setNovaOsobaId(novaOsobaId);
        trenutnaStavka.setTrenutnaLokacijaId(trenutnaLokacijaId);
        trenutnaStavka.setNovaLokacijaId(novaLokacijaId);
        trenutnaStavka.setPopisnaListaId(popisnaListaId);

        if (popisnaStavkaZaAzuriranje != null) {
            // Ažuriranje postojeće stavke
            trenutnaStavka.setId(popisnaStavkaZaAzuriranje.getId());  // Postavljanje ID-a stavke za ažuriranje
            new PopisnaStavkaTask(null, popisnaStavkaDao, PopisnaStavkaTask.OperationType.UPDATE, trenutnaStavka).execute();
            Toast.makeText(getContext(), getString(R.string.uspjesno_azuriranje), Toast.LENGTH_SHORT).show();

        } else {
            // Unos nove stavke
            new PopisnaStavkaTask(null, popisnaStavkaDao, PopisnaStavkaTask.OperationType.INSERT, trenutnaStavka).execute();
            Toast.makeText(getContext(), getString(R.string.uspjesno_dodavanje), Toast.LENGTH_SHORT).show();

        }
    }


    private void postaviAutoCompleteAdapterZaOsnovnaSredstva() {
        List<OsnovnoSredstvo> osnovnoSredstvoList = osnovnoSredstvoDao.getAll();
        List<String> osnovnoSredstvoNazivi = new ArrayList<>();

        for (OsnovnoSredstvo os : osnovnoSredstvoList) {
            osnovnoSredstvoNazivi.add(os.getNaziv());
        }

        ArrayAdapter<String> osnovnoSredstvoAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, osnovnoSredstvoNazivi);
        osnovnoSredstvoAutoCompleteTextView.setAdapter(osnovnoSredstvoAdapter);

        osnovnoSredstvoAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedOsnovnoSredstvoName = (String) parent.getItemAtPosition(position);
            for (OsnovnoSredstvo os : osnovnoSredstvoList) {
                if (os.getNaziv().equals(selectedOsnovnoSredstvoName)) {
                    trenutnaStavka = new PopisnaStavka();
                    trenutnaStavka.setOsnovnoSredstvoId(os.getId());
                    popuniFormuSaPodacima(os);
                    break;
                }
            }
        });
    }

    private void postaviAutoCompleteAdapterZaOsobe() {
        // Pretpostavljamo da 'zaposleniDao.getAll()' vraća listu svih zaposlenih
        List<Zaposleni> zaposleniList = zaposleniDao.getAll();
        List<String> zaposleniImena = new ArrayList<>();

        // Kreiranje liste stringova za prikaz (ime i prezime zaposlenog)
        for (Zaposleni zaposleni : zaposleniList) {
            zaposleniImena.add(zaposleni.getIme() + " " + zaposleni.getPrezime());
        }

        ArrayAdapter<String> zaposleniAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, zaposleniImena);
        novaOsobaEditText.setAdapter(zaposleniAdapter);

        // Postavljanje listenera koji čuva ID izabranog zaposlenog
        novaOsobaEditText.setOnItemClickListener((parent, view, position, id) -> {
            String selectedZaposleniName = (String) parent.getItemAtPosition(position);
            for (Zaposleni zaposleni : zaposleniList) {
                if ((zaposleni.getIme() + " " + zaposleni.getPrezime()).equals(selectedZaposleniName)) {
                    novaOsobaId = zaposleni.getId();  // Čuvanje ID vrednosti izabrane osobe
                    break;
                }
            }
        });
    }

    private void postaviAutoCompleteAdapterZaLokacije() {

        List<Lokacija> lokacijaList = lokacijaDao.getAll();
        List<String> lokacijeImena = new ArrayList<>();

        // Kreiranje liste stringova za prikaz (grad i adresa lokacije)
        for (Lokacija lokacija : lokacijaList) {
            lokacijeImena.add(lokacija.getGrad() + ", " + lokacija.getAdresa());
        }

        ArrayAdapter<String> lokacijaAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, lokacijeImena);
        novaLokacijaEditText.setAdapter(lokacijaAdapter);

        // Postavljanje listenera koji čuva ID izabrane lokacije
        novaLokacijaEditText.setOnItemClickListener((parent, view, position, id) -> {
            String selectedLokacijaName = (String) parent.getItemAtPosition(position);
            for (Lokacija lokacija : lokacijaList) {
                if ((lokacija.getGrad() + ", " + lokacija.getAdresa()).equals(selectedLokacijaName)) {
                    novaLokacijaId = lokacija.getId();  // Čuvanje ID vrednosti izabrane lokacije
                    break;
                }
            }
        });
    }


    private void skenirajBarkod() {
        // Skeniranje barkoda
        ScanOptions options = new ScanOptions();
        options.setPrompt("Skeniraj barkod");
        scanLauncher.launch(options);
    }

    private final ActivityResultLauncher<ScanOptions> scanLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            String barkod = result.getContents();
            OsnovnoSredstvo os = osnovnoSredstvoDao.getByBarkod(barkod);
            if (os != null) {
                trenutnaStavka = new PopisnaStavka();
                trenutnaStavka.setOsnovnoSredstvoId(os.getId());
                popuniFormuSaPodacima(os);
            } else {
                Toast.makeText(getContext(), "Osnovno sredstvo nije pronađeno.", Toast.LENGTH_SHORT).show();
            }
        }
    });


    private void popuniFormuSaPodacima(OsnovnoSredstvo os) {
        osnovnoSredstvoAutoCompleteTextView.setText(os.getNaziv());

        // Dohvatanje trenutne osobe i lokacije
        Zaposleni trenutnaOsoba = zaposleniDao.getById(os.getZaduzenaOsobaId());
        Lokacija trenutnaLokacija = lokacijaDao.getById(os.getZaduzenaLokacijaId());

        if (trenutnaOsoba != null) {
            trenutnaOsobaEditText.setText(trenutnaOsoba.getIme() + " " + trenutnaOsoba.getPrezime());
            trenutnaOsobaId = trenutnaOsoba.getId(); // Postavljanje ID-a trenutne osobe }

            if (trenutnaLokacija != null) {
                trenutnaLokacijaEditText.setText(trenutnaLokacija.getAdresa());
                trenutnaLokacijaId = trenutnaLokacija.getId();  // Postavljanje ID-a trenutne lokacije
            }

            // Postavljanje inicijalnih vrednosti za novu osobu i novu lokaciju
            novaOsobaEditText.setText(trenutnaOsoba != null ? trenutnaOsoba.getIme() + " " + trenutnaOsoba.getPrezime() : "");
            novaOsobaId = trenutnaOsobaId;
            novaLokacijaEditText.setText(trenutnaLokacija != null ? trenutnaLokacija.getAdresa() : "");
            novaLokacijaId = trenutnaLokacijaId;
        }
    }
}
