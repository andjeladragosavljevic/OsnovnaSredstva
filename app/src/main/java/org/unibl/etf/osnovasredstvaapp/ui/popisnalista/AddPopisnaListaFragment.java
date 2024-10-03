package org.unibl.etf.osnovasredstvaapp.ui.popisnalista;

import android.os.Bundle;

import androidx.fragment.app.Fragment;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import org.unibl.etf.osnovasredstvaapp.R;
import org.unibl.etf.osnovasredstvaapp.dao.PopisnaListaDao;
import org.unibl.etf.osnovasredstvaapp.database.AppDatabase;
import org.unibl.etf.osnovasredstvaapp.entity.PopisnaLista;
import org.unibl.etf.osnovasredstvaapp.ui.popisnastavka.PopisnaStavkaRecyclerViewAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddPopisnaListaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddPopisnaListaFragment extends Fragment {
    private EditText nazivListeEditText;
    private Button kreirajListuButton;
    private PopisnaListaDao popisnaListaDao;
    private PopisnaLista trenutnaLista;

    private PopisnaLista listaZaAzuriranje;

    private static final String ARG_LISTA = "popisnaLista";


    private PopisnaStavkaRecyclerViewAdapter adapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddPopisnaListaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddPopisnaListaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddPopisnaListaFragment newInstance(String param1, String param2) {
        AddPopisnaListaFragment fragment = new AddPopisnaListaFragment();
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
            listaZaAzuriranje = (PopisnaLista) getArguments().getSerializable(ARG_LISTA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_popisna_lista, container, false);

        nazivListeEditText = view.findViewById(R.id.nazivListe);
        kreirajListuButton = view.findViewById(R.id.kreirajListuButton);


        popisnaListaDao = AppDatabase.getInstance(getContext()).popisnaListaDao();

        if (listaZaAzuriranje != null) {
            nazivListeEditText.setText(listaZaAzuriranje.getNaziv());

            kreirajListuButton.setText(getString(R.string.azuriraj_listu));

        }

        // Kreiranje popisne liste
        kreirajListuButton.setOnClickListener(v -> {
            String naziv = nazivListeEditText.getText().toString();

            if (naziv.isEmpty()) {
                Toast.makeText(getContext(), getString(R.string.popunite_sva_polja), Toast.LENGTH_SHORT).show();
                return;
            }

            if(listaZaAzuriranje == null){
                trenutnaLista = new PopisnaLista();
                trenutnaLista.setNaziv(naziv);
                trenutnaLista.setDatumKreiranja(getCurrentDate());
                popisnaListaDao.insert(trenutnaLista);

                new PopisnaListaTask(null, popisnaListaDao, PopisnaListaTask.OperationType.INSERT, trenutnaLista);
                Toast.makeText(getContext(), getString(R.string.uspjesno_dodavanje), Toast.LENGTH_SHORT).show();

            } else {
                // Ažuriranje postojećeg zaposlenog
                listaZaAzuriranje.setNaziv(naziv);


                new PopisnaListaTask(null, popisnaListaDao, PopisnaListaTask.OperationType.UPDATE, listaZaAzuriranje).execute();
                Toast.makeText(getContext(), getString(R.string.uspjesno_azuriranje), Toast.LENGTH_SHORT).show();

            }




        });


        return view;
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }


}