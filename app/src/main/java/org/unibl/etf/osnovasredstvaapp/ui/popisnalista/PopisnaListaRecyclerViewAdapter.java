package org.unibl.etf.osnovasredstvaapp.ui.popisnalista;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import org.unibl.etf.osnovasredstvaapp.R;
import org.unibl.etf.osnovasredstvaapp.dao.PopisnaListaDao;
import org.unibl.etf.osnovasredstvaapp.database.AppDatabase;
import org.unibl.etf.osnovasredstvaapp.entity.PopisnaLista;
import org.unibl.etf.osnovasredstvaapp.ui.popisnalista.placeholder.PlaceholderContent.PlaceholderItem;
import org.unibl.etf.osnovasredstvaapp.databinding.FragmentPopisnaListaBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class PopisnaListaRecyclerViewAdapter extends RecyclerView.Adapter<PopisnaListaRecyclerViewAdapter.ViewHolder> {

    private  List<PopisnaLista> liste;
    Context context;
    PopisnaListaDao popisnaListaDao;

    public PopisnaListaRecyclerViewAdapter(List<PopisnaLista> items, Context context) {
        liste = items;
        this.context = context;
        popisnaListaDao = AppDatabase.getInstance(context).popisnaListaDao();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentPopisnaListaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mIdView.setText(String.valueOf(liste.get(position).getId()));
        holder.mContentView.setText(liste.get(position).getNaziv());
        holder.mDatum.setText(liste.get(position).getDatumKreiranja());

        holder.itemView.setOnClickListener(v -> {
            // Create and show the BottomSheetDialog
            PopisnaListaBottomSheetDialogFragment bottomSheetDialog = new PopisnaListaBottomSheetDialogFragment(liste.get(position), new PopisnaListaBottomSheetDialogFragment.OnOptionSelectedListener() {
                @Override
                public void onDelete(PopisnaLista popisnaLista) {
                    deletePopisnaLista(popisnaLista);
                }

                @Override
                public void onUpdate(PopisnaLista popisnaLista) {
                    updatePopisnaLista(popisnaLista);

                }
                @Override
                public void onShowDetails(PopisnaLista popisnaLista) {
                    showDetails(popisnaLista);
                }
            });

            bottomSheetDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "OptionsBottomSheetDialog");
        });
    }

    private void showDetails(PopisnaLista popisnaLista) {
        NavController navController = Navigation.findNavController((FragmentActivity) context, R.id.nav_host_fragment_content_main);
        Bundle args = new Bundle();
        args.putSerializable("popisnaListaId", popisnaLista.getId());
        navController.navigate(R.id.action_nav_popisna_lista_to_nav_popisna_stavka_list, args);
    }

    private void deletePopisnaLista(PopisnaLista popisnaLista) {

        new PopisnaListaTask(null, popisnaListaDao, PopisnaListaTask.OperationType.DELETE, popisnaLista).execute();
        // Osvježi listu nakon brisanja
        this.liste.remove(popisnaLista);
        notifyDataSetChanged();
    }

    private void updatePopisnaLista(PopisnaLista popisnaLista) {
        NavController navController = Navigation.findNavController((FragmentActivity) context, R.id.nav_host_fragment_content_main);
        Bundle args = new Bundle();
        args.putSerializable("popisnaLista", popisnaLista);
        navController.navigate(R.id.action_nav_popisna_lista_to_add_popisna_lista, args);
    }

    @Override
    public int getItemCount() {
        return liste.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mDatum;

        public ViewHolder(FragmentPopisnaListaBinding binding) {
            super(binding.getRoot());
            mIdView = binding.itemNumber;
            mContentView = binding.content;
            mDatum = binding.datum;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public void updateData(List<PopisnaLista> newData) {
        liste = newData;
        notifyDataSetChanged();
    }

}