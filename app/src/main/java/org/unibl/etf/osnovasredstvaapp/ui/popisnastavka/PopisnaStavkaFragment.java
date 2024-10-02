package org.unibl.etf.osnovasredstvaapp.ui.popisnastavka;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.unibl.etf.osnovasredstvaapp.R;
import org.unibl.etf.osnovasredstvaapp.dao.PopisnaStavkaDao;
import org.unibl.etf.osnovasredstvaapp.database.AppDatabase;
import org.unibl.etf.osnovasredstvaapp.entity.OsnovnoSredstvo;
import org.unibl.etf.osnovasredstvaapp.entity.PopisnaStavka;
import org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo.OsnovnoSredstvoRecyclerViewAdapter;
import org.unibl.etf.osnovasredstvaapp.ui.popisnastavka.placeholder.PlaceholderContent;
import org.unibl.etf.osnovasredstvaapp.ui.zaposleni.ZaposleniRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class PopisnaStavkaFragment extends Fragment {
    private RecyclerView stavkeRecyclerView;
    private PopisnaStavkaRecyclerViewAdapter adapter;
    private TextView emptyView;
    private Button dodajStavkuBarkodomButton, dodajStavkuRucnoButton;
    private PopisnaStavkaDao popisnaStavkaDao;
    private int popisnaListaId;

    public static PopisnaStavkaFragment newInstance(int popisnaListaId) {
        PopisnaStavkaFragment fragment = new PopisnaStavkaFragment();
        Bundle args = new Bundle();
        args.putInt("popisnaListaId", popisnaListaId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            popisnaListaId = getArguments().getInt("popisnaListaId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popisna_stavka_list, container, false);

        stavkeRecyclerView = view.findViewById(R.id.stavkeRecyclerView);
        emptyView = view.findViewById(R.id.empty_view);
        dodajStavkuBarkodomButton = view.findViewById(R.id.dodajStavkuBarkodomButton);
        dodajStavkuRucnoButton = view.findViewById(R.id.dodajStavkuRucnoButton);

        stavkeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        popisnaStavkaDao = AppDatabase.getInstance(getContext()).popisnaStavkaDao();

        // Prikazivanje stavki popisne liste
        List<PopisnaStavka> stavke = popisnaStavkaDao.getByPopisnaListaId(popisnaListaId);
        if (stavke.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            stavkeRecyclerView.setVisibility(View.GONE);
        } else {
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(stavkeRecyclerView.getContext(), LinearLayoutManager.VERTICAL);
            stavkeRecyclerView.addItemDecoration(dividerItemDecoration);
            emptyView.setVisibility(View.GONE);
            stavkeRecyclerView.setVisibility(View.VISIBLE);
            adapter = new PopisnaStavkaRecyclerViewAdapter(stavke, getContext());
            stavkeRecyclerView.setAdapter(adapter);

        }

        // Klik za dodavanje stavke skeniranjem barkoda
        dodajStavkuBarkodomButton.setOnClickListener(v -> {
            // Otvori novi fragment za dodavanje stavke skeniranjem barkoda
            openAddStavkaFragment(true);
        });

        // Klik za ručno dodavanje stavke
        dodajStavkuRucnoButton.setOnClickListener(v -> {
            // Otvori novi fragment za ručno dodavanje stavke
            openAddStavkaFragment(false);
        });

        return view;
    }

    private void openAddStavkaFragment(boolean isSkeniranje) {
        Bundle args = new Bundle();
        args.putBoolean("isSkeniranje", isSkeniranje);
        args.putInt("popisnaListaId", popisnaListaId);

        // Pronalazak NavController-a i navigacija ka AddPopisnaStavkaFragment
        NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.add_popisna_stavka_fragment, args);
    }

    public void updateList(List<PopisnaStavka> popisneStavke) {
        if (popisneStavke == null || popisneStavke.isEmpty()) {
            // Ako nema podataka, prikaži poruku
            stavkeRecyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            // Ako ima podataka, sakrij poruku i prikaži listu
            stavkeRecyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            if (adapter == null) {
                adapter = new PopisnaStavkaRecyclerViewAdapter(popisneStavke, getContext());
                stavkeRecyclerView.setAdapter(adapter);
            } else {
                adapter.updateData(popisneStavke);
            }
        }
    }

}