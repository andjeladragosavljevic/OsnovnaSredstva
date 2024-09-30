package org.unibl.etf.osnovasredstvaapp.ui.lokacija;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.unibl.etf.osnovasredstvaapp.R;
import org.unibl.etf.osnovasredstvaapp.dao.LokacijaDao;
import org.unibl.etf.osnovasredstvaapp.dao.ZaposleniDao;
import org.unibl.etf.osnovasredstvaapp.database.AppDatabase;
import org.unibl.etf.osnovasredstvaapp.entity.Lokacija;
import org.unibl.etf.osnovasredstvaapp.entity.Zaposleni;
import org.unibl.etf.osnovasredstvaapp.ui.zaposleni.ZaposleniTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddLokacijaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddLokacijaFragment extends Fragment {
    private EditText gradEt, adresaEt;
    private Button saveButton;
    private LokacijaDao lokacijaDao;
    private Lokacija lokacijaZaAzuriranje;

    private static final String ARG_LOKACIJA = "lokacija";


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddLokacijaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddLokacijaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddLokacijaFragment newInstance(String param1, String param2) {
        AddLokacijaFragment fragment = new AddLokacijaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            lokacijaZaAzuriranje = (Lokacija) getArguments().getSerializable(ARG_LOKACIJA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_lokacija, container, false);

        gradEt = view.findViewById(R.id.grad);
        adresaEt = view.findViewById(R.id.adresa);
        saveButton = view.findViewById(R.id.save_lokacija);

        // Inicijalizacija DAO objekta
        lokacijaDao = AppDatabase.getInstance(getContext()).lokacijaDao();


        if (lokacijaZaAzuriranje != null) {
            gradEt.setText(lokacijaZaAzuriranje.getGrad());
            adresaEt.setText(lokacijaZaAzuriranje.getAdresa());
            saveButton.setText("Ažuriraj lokaciju");
        }

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String grad = gradEt.getText().toString();
                String adresa = adresaEt.getText().toString();


                if (grad.isEmpty() || adresa.isEmpty()) {
                    Toast.makeText(getContext(), "Sva polja moraju biti popunjena", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (lokacijaZaAzuriranje == null) {
                    // Kreiranje novog zaposlenog
                    Lokacija novaLokacija = new Lokacija();
                    novaLokacija.setGrad(grad);
                    novaLokacija.setAdresa(adresa);

                    new LokacijaTask(null, lokacijaDao, LokacijaTask.OperationType.INSERT, novaLokacija).execute();
                    Toast.makeText(getContext(), "Lokacija uspješno dodat", Toast.LENGTH_SHORT).show();
                } else {

                    lokacijaZaAzuriranje.setGrad(grad);
                    lokacijaZaAzuriranje.setAdresa(adresa);


                    new LokacijaTask(null, lokacijaDao, LokacijaTask.OperationType.UPDATE, lokacijaZaAzuriranje).execute();
                    Toast.makeText(getContext(), "Lokacija uspješno ažuriran", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}