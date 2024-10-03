package org.unibl.etf.osnovasredstvaapp.ui.zaposleni;

import android.content.Context;
import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import org.unibl.etf.osnovasredstvaapp.dao.ZaposleniDao;
import org.unibl.etf.osnovasredstvaapp.database.AppDatabase;
import org.unibl.etf.osnovasredstvaapp.entity.Zaposleni;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class ZaposleniFragment extends Fragment {
    private MaterialCardView searchCard;
    private LinearLayout expansionContent;
    private ImageView expandCollapseIcon;
    private boolean isExpanded = false;
    private SearchView searchViewIme, searchViewPrezime;
    private ZaposleniDao zaposleniDao;
    private RecyclerView recyclerView;
    private ZaposleniRecyclerViewAdapter adapter;
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
        View view = inflater.inflate(R.layout.fragment_zaposleni_list, container, false);
        emptyView = view.findViewById(R.id.empty_view);

        searchCard = view.findViewById(R.id.searchCard);
        expansionContent = view.findViewById(R.id.expansionContent);
        expandCollapseIcon = view.findViewById(R.id.expandCollapseIcon);

        // Set click listener for header to toggle expansion/collapse
        searchCard.findViewById(R.id.expansionHeader).setOnClickListener(v -> toggleExpansion());

        searchViewIme = view.findViewById(R.id.searchViewIme);
        searchViewIme.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterZaposleni(query, searchViewPrezime.getQuery().toString());  // Filtriraj po oba kriterijuma
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterZaposleni(newText, searchViewPrezime.getQuery().toString());  // Filtriraj po oba kriterijuma
                return true;
            }
        });


        searchViewPrezime = view.findViewById(R.id.searchViewPrezime);  // Novo polje za barkod
        searchViewPrezime.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterZaposleni(searchViewIme.getQuery().toString(), query);  // Filtriraj po oba kriterijuma
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterZaposleni(searchViewIme.getQuery().toString(), newText);  // Filtriraj po oba kriterijuma
                return true;
            }
        });

        // Set the adapter
        Context context = getContext();

        zaposleniDao = AppDatabase.getInstance(getContext()).zaposleniDao();

        recyclerView = view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Postavljanje praznog adaptera dok se ne učitaju podaci
        adapter = new ZaposleniRecyclerViewAdapter(new ArrayList<>(), context);
        recyclerView.setAdapter(adapter);

        // Inicijalizacija Room baze i DAO-a
        ZaposleniDao zaposleniDao = AppDatabase.getInstance(getContext()).zaposleniDao();


        new ZaposleniTask(this, zaposleniDao).execute();


        return view;
    }

    private void filterZaposleni(String ime, String prezime) {
        new ZaposleniTask(this, zaposleniDao, ime, prezime).execute();  // Poziv AsyncTask-a za filtriranje
    }


    // Metoda za ažuriranje RecyclerView-a sa podacima iz baze
    public void updateList(List<Zaposleni> zaposleni) {
        if (zaposleni.isEmpty()) {
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