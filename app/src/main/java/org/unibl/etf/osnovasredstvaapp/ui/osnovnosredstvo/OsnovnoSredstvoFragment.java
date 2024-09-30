package org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo;

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
import org.unibl.etf.osnovasredstvaapp.dao.OsnovnoSredstvoDao;
import org.unibl.etf.osnovasredstvaapp.database.AppDatabase;
import org.unibl.etf.osnovasredstvaapp.entity.OsnovnoSredstvo;
import org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo.placeholder.PlaceholderContent;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class OsnovnoSredstvoFragment extends Fragment {

    private RecyclerView recyclerView;
    private OsnovnoSredstvoRecyclerViewAdapter adapter;
    private OsnovnoSredstvoDao osnovnoSredstvoDao;
    private TextView emptyView;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public OsnovnoSredstvoFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static OsnovnoSredstvoFragment newInstance(int columnCount) {
        OsnovnoSredstvoFragment fragment = new OsnovnoSredstvoFragment();
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
        View view = inflater.inflate(R.layout.fragment_osnovno_sredstvo_list, container, false);
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
        adapter = new OsnovnoSredstvoRecyclerViewAdapter(new ArrayList<>(), context);
        recyclerView.setAdapter(adapter);

        // Inicijalizacija Room baze i DAO-a
        osnovnoSredstvoDao = AppDatabase.getInstance(getContext()).osnovnoSredstvoDao();

        // Pokretanje AsyncTask-a za dohvatanje podataka
        new OsnovnoSredstvoTask(this, osnovnoSredstvoDao).execute();
        return view;
    }


    // Metoda za ažuriranje RecyclerView-a sa podacima iz baze
    public void updateList(List<OsnovnoSredstvo> osnovnaSredstva) {
        if (osnovnaSredstva == null || osnovnaSredstva.isEmpty()) {
            // Ako nema podataka, prikaži poruku
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            // Ako ima podataka, sakrij poruku i prikaži listu
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            if (adapter == null) {
                adapter = new OsnovnoSredstvoRecyclerViewAdapter(osnovnaSredstva, getContext());
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateData(osnovnaSredstva);
            }
        }
    }

}