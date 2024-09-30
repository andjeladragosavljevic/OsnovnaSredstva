package org.unibl.etf.osnovasredstvaapp.ui.zaposleni;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.unibl.etf.osnovasredstvaapp.dao.ZaposleniDao;
import org.unibl.etf.osnovasredstvaapp.database.AppDatabase;
import org.unibl.etf.osnovasredstvaapp.entity.OsnovnoSredstvo;
import org.unibl.etf.osnovasredstvaapp.entity.Zaposleni;
import org.unibl.etf.osnovasredstvaapp.ui.zaposleni.placeholder.PlaceholderContent.PlaceholderItem;
import org.unibl.etf.osnovasredstvaapp.databinding.FragmentZaposleniBinding;
import org.unibl.etf.osnovasredstvaapp.R;

import java.util.List;
import java.util.Locale;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ZaposleniRecyclerViewAdapter extends RecyclerView.Adapter<ZaposleniRecyclerViewAdapter.ViewHolder> {

    private  List<Zaposleni> zaposleni;
    private Context context;
    ZaposleniDao zaposleniDao = AppDatabase.getInstance(context).zaposleniDao();


    public ZaposleniRecyclerViewAdapter(List<Zaposleni> items, Context context) {
        zaposleni = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentZaposleniBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }


    public void updateData(List<Zaposleni> newData) {
        zaposleni = newData;
        notifyDataSetChanged();
    }



    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = zaposleni.get(position);
        holder.mIdView.setText(String.valueOf(zaposleni.get(position).getId()));
        holder.mImeView.setText(zaposleni.get(position).getIme());
        holder.mPrezimeView.setText(zaposleni.get(position).getPrezime());
        holder.mPozicijaView.setText(zaposleni.get(position).getPozicija());


        holder.itemView.setOnClickListener(v -> {
            // Create and show the BottomSheetDialog
            ZaposleniBottomSheetDialogFragment bottomSheetDialog = new ZaposleniBottomSheetDialogFragment(zaposleni.get(position), new ZaposleniBottomSheetDialogFragment.OnOptionSelectedListener() {
                @Override
                public void onDelete(Zaposleni zaposleni) {

                    deleteZaposleni(zaposleni);
                }

                @Override
                public void onUpdate(Zaposleni zaposleni) {

                    updateZaposleni(zaposleni);

                }
            });

            bottomSheetDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "OptionsBottomSheetDialog");
        });

    }

    private void deleteZaposleni(Zaposleni zaposleni) {
        new ZaposleniTask(null, zaposleniDao, ZaposleniTask.OperationType.DELETE, zaposleni).execute();
        // Osvježi listu nakon brisanja
        this.zaposleni.remove(zaposleni);
        notifyDataSetChanged();
    }

    private void updateZaposleni(Zaposleni zaposleni) {
        NavController navController = Navigation.findNavController((FragmentActivity) context, R.id.nav_host_fragment_content_main);
        Bundle args = new Bundle();
        args.putSerializable("zaposleni", zaposleni);
        navController.navigate(R.id.action_nav_zaposleni_to_nav_add_zaposleni, args);
    }







    @Override
    public int getItemCount() {
        return zaposleni.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mImeView;
        public final TextView mPrezimeView;
        public final TextView mPozicijaView;
        public Zaposleni mItem;

        public ViewHolder(FragmentZaposleniBinding binding) {
            super(binding.getRoot());
            mIdView = binding.itemNumber;
            mImeView = binding.ime;
            mPrezimeView = binding.prezime;
            mPozicijaView = binding.pozicija;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mImeView.getText() + "'";
        }
    }




}