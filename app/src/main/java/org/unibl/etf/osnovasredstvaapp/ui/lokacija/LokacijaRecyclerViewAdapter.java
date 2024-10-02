package org.unibl.etf.osnovasredstvaapp.ui.lokacija;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.unibl.etf.osnovasredstvaapp.R;
import org.unibl.etf.osnovasredstvaapp.dao.LokacijaDao;
import org.unibl.etf.osnovasredstvaapp.database.AppDatabase;
import org.unibl.etf.osnovasredstvaapp.entity.Lokacija;
import org.unibl.etf.osnovasredstvaapp.entity.Zaposleni;
import org.unibl.etf.osnovasredstvaapp.ui.lokacija.placeholder.PlaceholderContent.PlaceholderItem;
import org.unibl.etf.osnovasredstvaapp.databinding.FragmentLokacijaBinding;
import org.unibl.etf.osnovasredstvaapp.ui.zaposleni.ZaposleniBottomSheetDialogFragment;
import org.unibl.etf.osnovasredstvaapp.ui.zaposleni.ZaposleniTask;

import java.util.Collection;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class LokacijaRecyclerViewAdapter extends RecyclerView.Adapter<LokacijaRecyclerViewAdapter.ViewHolder> {

    private List<Lokacija> lokacije;
    private Context context;
    LokacijaDao lokacijaDao ;
    public LokacijaRecyclerViewAdapter(List<Lokacija> items, Context context)
    {
        lokacije = items;
        this.context = context;
        lokacijaDao = AppDatabase.getInstance(context).lokacijaDao();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentLokacijaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    public void updateData(List<Lokacija> newData) {
        lokacije = newData;
        notifyDataSetChanged();
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = lokacije.get(position);
        holder.mIdView.setText(String.valueOf(lokacije.get(position).getId()));
        holder.mAdresaView.setText(lokacije.get(position).getAdresa());
        holder.mGradView.setText(lokacije.get(position).getGrad());
        holder.mLongitudeView.setText(lokacije.get(position).getLongitude());
        holder.mLatitudeView.setText(lokacije.get(position).getLatitude());

        holder.itemView.setOnClickListener(v -> {
            // Create and show the BottomSheetDialog
            LokacijaBottomSheetDialogFragment bottomSheetDialog = new LokacijaBottomSheetDialogFragment(lokacije.get(position), new LokacijaBottomSheetDialogFragment.OnOptionSelectedListener() {
                @Override
                public void onDelete(Lokacija lokacija) {
                    deleteLokacija(lokacija);
                }

                @Override
                public void onUpdate(Lokacija lokacija) {
                    updateLokacija(lokacija);

                }
            });

            bottomSheetDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "OptionsBottomSheetDialog");
        });
    }

    private void deleteLokacija(Lokacija lokacija) {
        new LokacijaTask(null, lokacijaDao, LokacijaTask.OperationType.DELETE, lokacija).execute();
        // Osvježi listu nakon brisanja
        this.lokacije.remove(lokacija);
        notifyDataSetChanged();
    }

    private void updateLokacija(Lokacija lokacija) {
        NavController navController = Navigation.findNavController((FragmentActivity) context, R.id.nav_host_fragment_content_main);
        Bundle args = new Bundle();
        args.putSerializable("lokacija", lokacija);
        navController.navigate(R.id.action_nav_lokacije_to_nav_add_lokacija, args);
    }



    @Override
    public int getItemCount() {
        return lokacije.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mIdView;
        public final TextView mAdresaView;
        public final TextView mGradView;
        public final TextView mLatitudeView;
        public final TextView mLongitudeView;
        public Lokacija mItem;

        public ViewHolder(FragmentLokacijaBinding binding) {
            super(binding.getRoot());
            mIdView = binding.itemNumber;
            mAdresaView = binding.adresa;
            mGradView = binding.grad;
            mLatitudeView = binding.latitude;
            mLongitudeView = binding.longitude;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mAdresaView.getText() + "'";
        }
    }


}