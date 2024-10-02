package org.unibl.etf.osnovasredstvaapp.ui.popisnastavka;

import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.unibl.etf.osnovasredstvaapp.R;
import org.unibl.etf.osnovasredstvaapp.dao.OsnovnoSredstvoDao;
import org.unibl.etf.osnovasredstvaapp.dao.PopisnaStavkaDao;
import org.unibl.etf.osnovasredstvaapp.database.AppDatabase;
import org.unibl.etf.osnovasredstvaapp.entity.OsnovnoSredstvo;
import org.unibl.etf.osnovasredstvaapp.entity.PopisnaStavka;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddPopisnaStavkaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPopisnaStavkaFragment extends Fragment {
    private EditText osnovnoSredstvoEditText, trenutnaOsobaEditText, novaOsobaEditText, trenutnaLokacijaEditText, novaLokacijaEditText;
    private Button saveStavkaButton;
    private int popisnaListaId;
    private boolean isSkeniranje;
    private OsnovnoSredstvoDao osnovnoSredstvoDao;
    private PopisnaStavka trenutnaStavka;
    private PopisnaStavkaDao popisnaStavkaDao;

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
        }

        osnovnoSredstvoDao = AppDatabase.getInstance(getContext()).osnovnoSredstvoDao();
        popisnaStavkaDao = AppDatabase.getInstance(getContext()).popisnaStavkaDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_popisna_stavka, container, false);

        osnovnoSredstvoEditText = view.findViewById(R.id.osnovnoSredstvoEditText);
        trenutnaOsobaEditText = view.findViewById(R.id.trenutnaOsobaEditText);
        novaOsobaEditText = view.findViewById(R.id.novaOsobaEditText);
        trenutnaLokacijaEditText = view.findViewById(R.id.trenutnaLokacijaEditText);
        novaLokacijaEditText = view.findViewById(R.id.novaLokacijaEditText);
        saveStavkaButton = view.findViewById(R.id.saveStavkaButton);

        if (isSkeniranje) {
            skenirajBarkod();
        }

        saveStavkaButton.setOnClickListener(v -> {
            saveStavka();
        });

        return view;
    }

    private void saveStavka() {
        if (trenutnaStavka == null || trenutnaStavka.getOsnovnoSredstvoId() == 0) {
            Toast.makeText(getContext(), "Molimo unesite validno osnovno sredstvo.", Toast.LENGTH_SHORT).show();
            return;
        }

        trenutnaStavka.setTrenutnaOsobaId(Integer.parseInt(trenutnaOsobaEditText.getText().toString()));
        trenutnaStavka.setNovaOsobaId(Integer.parseInt(novaOsobaEditText.getText().toString()));
        trenutnaStavka.setTrenutnaLokacijaId(Integer.parseInt(trenutnaLokacijaEditText.getText().toString()));
        trenutnaStavka.setNovaLokacijaId(Integer.parseInt(novaLokacijaEditText.getText().toString()));
        trenutnaStavka.setPopisnaListaId(popisnaListaId);

        popisnaStavkaDao.insert(trenutnaStavka);
        Toast.makeText(getContext(), "Stavka je uspešno sačuvana.", Toast.LENGTH_SHORT).show();
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
        osnovnoSredstvoEditText.setText(os.getNaziv());
        trenutnaOsobaEditText.setText(String.valueOf(os.getZaduzenaOsobaId()));
        novaOsobaEditText.setText(String.valueOf(os.getZaduzenaOsobaId()));
        trenutnaLokacijaEditText.setText(String.valueOf(os.getZaduzenaLokacijaId()));
        novaLokacijaEditText.setText(String.valueOf(os.getZaduzenaLokacijaId()));
    }
}
