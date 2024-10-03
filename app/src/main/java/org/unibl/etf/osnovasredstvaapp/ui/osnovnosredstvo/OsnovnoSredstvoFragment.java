package org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo;

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
import org.unibl.etf.osnovasredstvaapp.dao.OsnovnoSredstvoDao;
import org.unibl.etf.osnovasredstvaapp.database.AppDatabase;
import org.unibl.etf.osnovasredstvaapp.entity.OsnovnoSredstvo;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class OsnovnoSredstvoFragment extends Fragment {
    private MaterialCardView searchCard;
    private LinearLayout expansionContent;
    private ImageView expandCollapseIcon;
    private boolean isExpanded = false;
    private RecyclerView recyclerView;
    private OsnovnoSredstvoRecyclerViewAdapter adapter;
    private OsnovnoSredstvoDao osnovnoSredstvoDao;
    private TextView emptyView;
    private SearchView searchView, searchViewBarkod;

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
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    // Method to toggle expansion/collapse
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
        View view = inflater.inflate(R.layout.fragment_osnovno_sredstvo_list, container, false);
        emptyView = view.findViewById(R.id.empty_view);
        // Initialize views
        searchCard = view.findViewById(R.id.searchCard);
        expansionContent = view.findViewById(R.id.expansionContent);
        expandCollapseIcon = view.findViewById(R.id.expandCollapseIcon);

        // Set click listener for header to toggle expansion/collapse
        searchCard.findViewById(R.id.expansionHeader).setOnClickListener(v -> toggleExpansion());
        // Pretraga po nazivu
        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterOsnovnaSredstva(query, searchViewBarkod.getQuery().toString());  // Filtriraj po oba kriterijuma
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterOsnovnaSredstva(newText, searchViewBarkod.getQuery().toString());  // Filtriraj po oba kriterijuma
                return true;
            }
        });

        // Pretraga po barkodu
        searchViewBarkod = view.findViewById(R.id.searchBarkod);  // Novo polje za barkod
        searchViewBarkod.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterOsnovnaSredstva(searchView.getQuery().toString(), query);  // Filtriraj po oba kriterijuma
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterOsnovnaSredstva(searchView.getQuery().toString(), newText);  // Filtriraj po oba kriterijuma
                return true;
            }
        });

        // Set the adapter
        Context context = getContext();

        // Inicijalizacija Room baze i DAO-a
        osnovnoSredstvoDao = AppDatabase.getInstance(getContext()).osnovnoSredstvoDao();


        recyclerView = view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        // Postavljanje praznog adaptera dok se ne učitaju podaci
        adapter = new OsnovnoSredstvoRecyclerViewAdapter(new ArrayList<>(), context);
        recyclerView.setAdapter(adapter);

        Bundle args = getArguments();
        if (args != null) {

            int lokacijaId = args.getInt("LOKACIJA_ID", -1);

            if (lokacijaId != -1) {
                filterByLocation(lokacijaId);  // Poziv metode za filtriranje po lokaciji
            } else {
                new OsnovnoSredstvoTask(this, osnovnoSredstvoDao).execute();  // Poziv za sve podatke ako nema filtriranja
            }
        } else {
            // Pokretanje AsyncTask-a za dohvatanje svih podataka
            new OsnovnoSredstvoTask(this, osnovnoSredstvoDao).execute();
        }

        return view;
    }

    private void filterOsnovnaSredstva(String naziv, String barkod) {
        new OsnovnoSredstvoTask(this, osnovnoSredstvoDao, naziv, barkod).execute();  // Poziv AsyncTask-a za filtriranje
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



    public void filterByLocation(int lokacijaId) {
        new OsnovnoSredstvoTask(this, osnovnoSredstvoDao, OsnovnoSredstvoTask.OperationType.GETBYLOCATIONID, lokacijaId).execute();
    }

}