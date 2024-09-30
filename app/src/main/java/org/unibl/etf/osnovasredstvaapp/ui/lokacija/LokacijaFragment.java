package org.unibl.etf.osnovasredstvaapp.ui.lokacija;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.unibl.etf.osnovasredstvaapp.R;
import org.unibl.etf.osnovasredstvaapp.dao.LokacijaDao;
import org.unibl.etf.osnovasredstvaapp.dao.ZaposleniDao;
import org.unibl.etf.osnovasredstvaapp.database.AppDatabase;
import org.unibl.etf.osnovasredstvaapp.entity.Lokacija;
import org.unibl.etf.osnovasredstvaapp.entity.Zaposleni;
import org.unibl.etf.osnovasredstvaapp.ui.lokacija.placeholder.PlaceholderContent;
import org.unibl.etf.osnovasredstvaapp.ui.zaposleni.ZaposleniRecyclerViewAdapter;
import org.unibl.etf.osnovasredstvaapp.ui.zaposleni.ZaposleniTask;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class LokacijaFragment extends Fragment {
    private RecyclerView recyclerView;
    private LokacijaRecyclerViewAdapter adapter;
    private LokacijaDao lokacijaDao;
    private TextView emptyView;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LokacijaFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static LokacijaFragment newInstance(int columnCount) {
        LokacijaFragment fragment = new LokacijaFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lokacija_list, container, false);
        emptyView = view.findViewById(R.id.empty_view);

        // Set the adapter
        Context context = getContext();
        recyclerView = view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        // Postavljanje praznog adaptera dok se ne učitaju podaci
        adapter = new LokacijaRecyclerViewAdapter(new ArrayList<>(), context);
        recyclerView.setAdapter(adapter);

        // Inicijalizacija Room baze i DAO-a
        lokacijaDao = AppDatabase.getInstance(getContext()).lokacijaDao();
        Log.d("LOK ", lokacijaDao.toString());
        // Pokretanje AsyncTask-a za dohvatanje podataka
        new LokacijaTask(this, lokacijaDao).execute();

        return view;
    }

    public void updateList(List<Lokacija> lokacije) {
        if (lokacije == null || lokacije.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            Log.d("TEST", emptyView.getText().toString());
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            if (adapter == null) {
                adapter = new LokacijaRecyclerViewAdapter(lokacije, getContext());
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateData(lokacije);
            }
        }
    }
}