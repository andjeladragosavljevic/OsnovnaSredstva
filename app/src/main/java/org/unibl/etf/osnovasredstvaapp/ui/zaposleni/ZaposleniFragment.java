package org.unibl.etf.osnovasredstvaapp.ui.zaposleni;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.unibl.etf.osnovasredstvaapp.R;
import org.unibl.etf.osnovasredstvaapp.dao.OsnovnoSredstvoDao;
import org.unibl.etf.osnovasredstvaapp.dao.ZaposleniDao;
import org.unibl.etf.osnovasredstvaapp.database.AppDatabase;
import org.unibl.etf.osnovasredstvaapp.entity.OsnovnoSredstvo;
import org.unibl.etf.osnovasredstvaapp.entity.Zaposleni;
import org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo.OsnovnoSredstvoRecyclerViewAdapter;
import org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo.OsnovnoSredstvoTask;
import org.unibl.etf.osnovasredstvaapp.ui.zaposleni.placeholder.PlaceholderContent;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class ZaposleniFragment extends Fragment {
    private RecyclerView recyclerView;
    private ZaposleniRecyclerViewAdapter adapter;
    private ZaposleniDao zaposleniDao;
    private TextView emptyView;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ZaposleniFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ZaposleniFragment newInstance(int columnCount) {
        ZaposleniFragment fragment = new ZaposleniFragment();
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
        View view = inflater.inflate(R.layout.fragment_zaposleni_list, container, false);
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
        adapter = new ZaposleniRecyclerViewAdapter(new ArrayList<>(), context);
        recyclerView.setAdapter(adapter);

        // Inicijalizacija Room baze i DAO-a
        zaposleniDao = AppDatabase.getInstance(getContext()).zaposleniDao();

        // Pokretanje AsyncTask-a za dohvatanje podataka
        new ZaposleniTask(this, zaposleniDao).execute();


        return view;
    }




    private void deleteZaposleni(Zaposleni zaposleni) {
        new ZaposleniTask(null, zaposleniDao, ZaposleniTask.OperationType.DELETE, zaposleni).execute();

    }

    private void updateZaposleni(Zaposleni zaposleni) {
        // Logika za otvaranje ekrana za ažuriranje
    }

    // Metoda za ažuriranje RecyclerView-a sa podacima iz baze
    public void updateList(List<Zaposleni> zaposleni) {
        Log.i("ZAPOSLENI ", zaposleni.toString());
        if (zaposleni == null || zaposleni.isEmpty()) {
            // Ako nema podataka, prikaži poruku
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            // Ako ima podataka, sakrij poruku i prikaži listu
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            if (adapter == null) {
                adapter = new ZaposleniRecyclerViewAdapter(zaposleni, getContext());
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateData(zaposleni);
            }
        }
    }
}