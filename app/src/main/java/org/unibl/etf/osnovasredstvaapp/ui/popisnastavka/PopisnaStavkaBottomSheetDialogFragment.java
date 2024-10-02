package org.unibl.etf.osnovasredstvaapp.ui.popisnastavka;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.unibl.etf.osnovasredstvaapp.R;
import org.unibl.etf.osnovasredstvaapp.entity.OsnovnoSredstvo;
import org.unibl.etf.osnovasredstvaapp.entity.PopisnaStavka;
import org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo.OsnovnoSredstvoBottomSheetDialogFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PopisnaStavkaBottomSheetDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PopisnaStavkaBottomSheetDialogFragment  extends BottomSheetDialogFragment {
    private PopisnaStavka popisnaStavka;
    private OnOptionSelectedListener listener;

    public interface OnOptionSelectedListener {
        void onDelete(PopisnaStavka popisnaStavka);
        void onUpdate(PopisnaStavka popisnaStavka);

    }
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PopisnaStavkaBottomSheetDialogFragment() {
        // Required empty public constructor
    }
    public PopisnaStavkaBottomSheetDialogFragment(PopisnaStavka popisnaStavka, PopisnaStavkaBottomSheetDialogFragment.OnOptionSelectedListener listener) {
        this.popisnaStavka = popisnaStavka;
        this.listener = listener;
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PopisnaStavkaBottomSheetDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PopisnaStavkaBottomSheetDialogFragment newInstance(String param1, String param2) {
        PopisnaStavkaBottomSheetDialogFragment fragment = new PopisnaStavkaBottomSheetDialogFragment();
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
        View view = inflater.inflate(R.layout.fragment_popisna_stavka_bottom_sheet_dialog, container, false);
        Button deleteButton = view.findViewById(R.id.button_delete);
        Button updateButton = view.findViewById(R.id.button_update);


        // Handle delete action
        deleteButton.setOnClickListener(v -> {
            listener.onDelete(popisnaStavka);
            dismiss();
        });

        // Handle update action
        updateButton.setOnClickListener(v -> {
            listener.onUpdate(popisnaStavka);
            dismiss();
        });

        return view;
    }
}