package org.unibl.etf.osnovasredstvaapp.ui.zaposleni;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.unibl.etf.osnovasredstvaapp.R;
import org.unibl.etf.osnovasredstvaapp.dao.ZaposleniDao;
import org.unibl.etf.osnovasredstvaapp.database.AppDatabase;
import org.unibl.etf.osnovasredstvaapp.entity.Zaposleni;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddZaposleniFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddZaposleniFragment extends Fragment {
    private EditText ime, prezime, pozicija;
    private Button saveButton;
    private ZaposleniDao zaposleniDao;
    private Zaposleni zaposleniZaAzuriranje;

    private static final String ARG_ZAPOSLENI = "zaposleni";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddZaposleniFragment() {
        // Required empty public constructor
    }

    public static AddZaposleniFragment newInstance(Zaposleni zaposleni) {
        AddZaposleniFragment fragment = new AddZaposleniFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ZAPOSLENI, zaposleni);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            zaposleniZaAzuriranje = (Zaposleni) getArguments().getSerializable(ARG_ZAPOSLENI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_zaposleni, container, false);
        ime = view.findViewById(R.id.ime);
        prezime = view.findViewById(R.id.prezime);
        pozicija = view.findViewById(R.id.pozicija);
        saveButton = view.findViewById(R.id.save_zaposleni);

        // Inicijalizacija DAO objekta
        zaposleniDao = AppDatabase.getInstance(getContext()).zaposleniDao();
        // Ako imamo zaposlenog za ažuriranje, popuni polja unaprijed
        if (zaposleniZaAzuriranje != null) {
            ime.setText(zaposleniZaAzuriranje.getIme());
            prezime.setText(zaposleniZaAzuriranje.getPrezime());
            pozicija.setText(zaposleniZaAzuriranje.getPozicija());
            saveButton.setText("Ažuriraj zaposlenog");
        }

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String imeZaposlenog = ime.getText().toString();
                String prezimeZaposlenog = prezime.getText().toString();
                String pozicijaZaposlenog = pozicija.getText().toString();

                if (imeZaposlenog.isEmpty() || prezimeZaposlenog.isEmpty() || pozicijaZaposlenog.isEmpty()) {
                    Toast.makeText(getContext(), "Sva polja moraju biti popunjena", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (zaposleniZaAzuriranje == null) {
                    // Kreiranje novog zaposlenog
                    Zaposleni noviZaposleni = new Zaposleni();
                    noviZaposleni.setIme(imeZaposlenog);
                    noviZaposleni.setPrezime(prezimeZaposlenog);
                    noviZaposleni.setPozicija(pozicijaZaposlenog);

                    new ZaposleniTask(null, zaposleniDao, ZaposleniTask.OperationType.INSERT, noviZaposleni).execute();
                    Toast.makeText(getContext(), "Zaposleni uspješno dodat", Toast.LENGTH_SHORT).show();
                } else {
                    // Ažuriranje postojećeg zaposlenog
                    zaposleniZaAzuriranje.setIme(imeZaposlenog);
                    zaposleniZaAzuriranje.setPrezime(prezimeZaposlenog);
                    zaposleniZaAzuriranje.setPozicija(pozicijaZaposlenog);

                    new ZaposleniTask(null, zaposleniDao, ZaposleniTask.OperationType.UPDATE, zaposleniZaAzuriranje).execute();
                    Toast.makeText(getContext(), "Zaposleni uspješno ažuriran", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}