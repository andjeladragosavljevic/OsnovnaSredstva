package org.unibl.etf.osnovasredstvaapp.ui.popisnalista;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.unibl.etf.osnovasredstvaapp.R;
import org.unibl.etf.osnovasredstvaapp.entity.Lokacija;
import org.unibl.etf.osnovasredstvaapp.entity.OsnovnoSredstvo;
import org.unibl.etf.osnovasredstvaapp.entity.PopisnaLista;
import org.unibl.etf.osnovasredstvaapp.entity.PopisnaStavka;
import org.unibl.etf.osnovasredstvaapp.ui.lokacija.LokacijaBottomSheetDialogFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PopisnaListaBottomSheetDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PopisnaListaBottomSheetDialogFragment extends BottomSheetDialogFragment {
    private PopisnaLista popisnaLista;
    private PopisnaListaBottomSheetDialogFragment.OnOptionSelectedListener listener;

    public interface OnOptionSelectedListener {
        void onDelete(PopisnaLista popisnaLista);
        void onUpdate(PopisnaLista popisnaLista);
        void onShowDetails(PopisnaLista popisnaLista);
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PopisnaListaBottomSheetDialogFragment() {
        // Required empty public constructor
    }
    public PopisnaListaBottomSheetDialogFragment(PopisnaLista popisnaLista, PopisnaListaBottomSheetDialogFragment.OnOptionSelectedListener listener) {
        this.popisnaLista = popisnaLista;
        this.listener = listener;
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PopisnaListaBottomSheetDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PopisnaListaBottomSheetDialogFragment newInstance(String param1, String param2) {
        PopisnaListaBottomSheetDialogFragment fragment = new PopisnaListaBottomSheetDialogFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_popisna_lista_bottom_sheet_dialog, container, false);
        Button deleteButton = view.findViewById(R.id.button_delete);
        Button updateButton = view.findViewById(R.id.button_update);
        Button showDetailsButton = view.findViewById(R.id.button_stavke);

        // Handle delete action
        deleteButton.setOnClickListener(v -> {
            listener.onDelete(popisnaLista);
            dismiss();
        });

        // Handle update action
        updateButton.setOnClickListener(v -> {
            listener.onUpdate(popisnaLista);
            dismiss();
        });

        showDetailsButton.setOnClickListener(v -> {
            listener.onShowDetails(popisnaLista);
            dismiss();
        });

        return view;
    }
}