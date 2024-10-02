package org.unibl.etf.osnovasredstvaapp.ui.popisnalista;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;

import org.unibl.etf.osnovasredstvaapp.R;
import org.unibl.etf.osnovasredstvaapp.dao.PopisnaListaDao;
import org.unibl.etf.osnovasredstvaapp.dao.ZaposleniDao;
import org.unibl.etf.osnovasredstvaapp.database.AppDatabase;
import org.unibl.etf.osnovasredstvaapp.entity.PopisnaLista;
import org.unibl.etf.osnovasredstvaapp.entity.Zaposleni;
import org.unibl.etf.osnovasredstvaapp.ui.popisnalista.placeholder.PlaceholderContent;
import org.unibl.etf.osnovasredstvaapp.ui.zaposleni.ZaposleniRecyclerViewAdapter;
import org.unibl.etf.osnovasredstvaapp.ui.zaposleni.ZaposleniTask;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class PopisnaListaFragment extends Fragment {
    private MaterialCardView searchCard;
    private LinearLayout expansionContent;
    private ImageView expandCollapseIcon;
    private boolean isExpanded = false;
    private SearchView searchViewNaziv, searchViewDate;
    private PopisnaListaDao popisnaListaDao;
    private RecyclerView recyclerView;
    private PopisnaListaRecyclerViewAdapter adapter;
    private TextView emptyView;
    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PopisnaListaFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static PopisnaListaFragment newInstance(int columnCount) {
        PopisnaListaFragment fragment = new PopisnaListaFragment();
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
    private void toggleExpansion() {
        if (isExpanded) {
            // Collapse content
            expansionContent.setVisibility(View.GONE);
            rotateIcon(180, 0); // Rotate back to original position
        } else {
            // Expand content
            expansionContent.setVisibility(View.VISIBLE);
            rotateIcon(0, 180); // Rotate downwards
        }
        isExpanded = !isExpanded;
    }

    // Optional: Rotate the expand/collapse icon
    private void rotateIcon(float from, float to) {
        RotateAnimation rotate = new RotateAnimation(from, to,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);
        expandCollapseIcon.startAnimation(rotate);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popisna_lista_list, container, false);

        emptyView = view.findViewById(R.id.empty_view);

        searchCard = view.findViewById(R.id.searchCard);
        expansionContent = view.findViewById(R.id.expansionContent);
        expandCollapseIcon = view.findViewById(R.id.expandCollapseIcon);

        // Set click listener for header to toggle expansion/collapse
        searchCard.findViewById(R.id.expansionHeader).setOnClickListener(v -> toggleExpansion());
        // Pretraga po nazivu
        searchViewNaziv = view.findViewById(R.id.searchViewNaziv);
        searchViewNaziv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               filterListe(query, searchViewDate.getQuery().toString());  // Filtriraj po oba kriterijuma
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
               filterListe(newText, searchViewDate.getQuery().toString());  // Filtriraj po oba kriterijuma
                return true;
            }
        });


        searchViewDate = view.findViewById(R.id.searchViewDatum);  // Novo polje za barkod
        searchViewDate.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterListe(searchViewNaziv.getQuery().toString(), query);  // Filtriraj po oba kriterijuma
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
              filterListe(searchViewNaziv.getQuery().toString(), newText);  // Filtriraj po oba kriterijuma
                return true;
            }
        });

        // Set the adapter
        Context context = getContext();



        recyclerView = view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);



        // Postavljanje praznog adaptera dok se ne učitaju podaci
        adapter = new PopisnaListaRecyclerViewAdapter(new ArrayList<>(), context);
        recyclerView.setAdapter(adapter);


        popisnaListaDao = AppDatabase.getInstance(getContext()).popisnaListaDao();
        // Pokretanje AsyncTask-a za dohvatanje podataka
        new PopisnaListaTask(this, popisnaListaDao).execute();


        return view;
    }

    private void filterListe(String naziv, String datum) {
        new PopisnaListaTask(this, popisnaListaDao, naziv, datum).execute();  // Poziv AsyncTask-a za filtriranje
    }


    // Metoda za ažuriranje RecyclerView-a sa podacima iz baze
    public void updateList(List<PopisnaLista> liste) {

        if (liste == null || liste.isEmpty()) {
            // Ako nema podataka, prikaži poruku
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            // Ako ima podataka, sakrij poruku i prikaži listu
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);

            if (adapter == null) {
                adapter = new PopisnaListaRecyclerViewAdapter(liste, getContext());
                recyclerView.setAdapter(adapter);
            } else {
                adapter.updateData(liste);
            }
        }
    }
}