package org.unibl.etf.osnovasredstvaapp.ui.popisnastavka;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.unibl.etf.osnovasredstvaapp.R;
import org.unibl.etf.osnovasredstvaapp.dao.LokacijaDao;
import org.unibl.etf.osnovasredstvaapp.dao.OsnovnoSredstvoDao;
import org.unibl.etf.osnovasredstvaapp.dao.PopisnaStavkaDao;
import org.unibl.etf.osnovasredstvaapp.dao.ZaposleniDao;
import org.unibl.etf.osnovasredstvaapp.database.AppDatabase;
import org.unibl.etf.osnovasredstvaapp.entity.Lokacija;
import org.unibl.etf.osnovasredstvaapp.entity.OsnovnoSredstvo;
import org.unibl.etf.osnovasredstvaapp.entity.PopisnaStavka;
import org.unibl.etf.osnovasredstvaapp.entity.Zaposleni;
import org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo.OsnovnoSredstvoBottomSheetDialogFragment;
import org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo.OsnovnoSredstvoTask;
import org.unibl.etf.osnovasredstvaapp.ui.popisnastavka.placeholder.PlaceholderContent.PlaceholderItem;
import org.unibl.etf.osnovasredstvaapp.databinding.FragmentPopsinaStavkaBinding;

import java.util.List;
import java.util.Locale;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class PopisnaStavkaRecyclerViewAdapter extends RecyclerView.Adapter<PopisnaStavkaRecyclerViewAdapter.ViewHolder> {
    private  List<PopisnaStavka> stavke;
    private  ZaposleniDao zaposleniDao;
    private  LokacijaDao lokacijaDao;
    private OsnovnoSredstvoDao osnovnoSredstvoDao;
    private PopisnaStavkaDao popisnaStavkaDao;
    private Context context;

    public PopisnaStavkaRecyclerViewAdapter(List<PopisnaStavka> items, Context context) {
        stavke = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentPopsinaStavkaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    public void addStavka(PopisnaStavka stavka)
    {
        Log.d("stavka", stavka.getTrenutnaOsobaId()+"");
        stavke.add(stavka);
        notifyItemInserted(stavke.size() - 1);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        PopisnaStavka stavka = stavke.get(position);
        zaposleniDao = AppDatabase.getInstance(context).zaposleniDao();
        lokacijaDao = AppDatabase.getInstance(context).lokacijaDao();
        osnovnoSredstvoDao = AppDatabase.getInstance(context).osnovnoSredstvoDao();
        popisnaStavkaDao = AppDatabase.getInstance(context).popisnaStavkaDao();

        Zaposleni trenutnaOsoba = zaposleniDao.getById(stavka.getTrenutnaOsobaId());
        Zaposleni novaOsoba = zaposleniDao.getById(stavka.getNovaOsobaId());
        Lokacija trenutnaLokacija = lokacijaDao.getById(stavka.getTrenutnaLokacijaId());
        Lokacija novaLokacija = lokacijaDao.getById(stavka.getNovaLokacijaId());

        OsnovnoSredstvo os = osnovnoSredstvoDao.getById(stavka.getOsnovnoSredstvoId());
        // Popunite view-ove podacima iz stavke
        holder.nazivOsnovnogSredstvaTextView.setText(os.getNaziv());

        // Prikaz imena trenutne i nove osobe
        holder.trenutnaOsobaTextView.setText("Trenutna osoba: " + (trenutnaOsoba != null ? trenutnaOsoba.getIme() + " " + trenutnaOsoba.getPrezime() : "N/A"));
        holder.novaOsobaTextView.setText("Nova osoba: " + (novaOsoba != null ? novaOsoba.getIme() + " " + novaOsoba.getPrezime() : "N/A"));

        // Prikaz trenutne i nove lokacije
        holder.trenutnaLokacijaTextView.setText("Trenutna lokacija: " + (trenutnaLokacija != null ? trenutnaLokacija.getAdresa() + ", " + trenutnaLokacija.getGrad() : "N/A"));
        holder.novaLokacijaTextView.setText("Nova lokacija: " + (novaLokacija != null ? novaLokacija.getAdresa() + ", " + novaLokacija.getGrad() : "N/A"));

        holder.itemView.setOnClickListener(v -> {
            // Create and show the BottomSheetDialog
            PopisnaStavkaBottomSheetDialogFragment bottomSheetDialog = new PopisnaStavkaBottomSheetDialogFragment(stavke.get(position), new PopisnaStavkaBottomSheetDialogFragment.OnOptionSelectedListener() {
                @Override
                public void onDelete(PopisnaStavka popisnaStavka) {
                  deleteStavka(popisnaStavka);
                }

                @Override
                public void onUpdate(PopisnaStavka popisnaStavka) {
                   updateStavka(popisnaStavka);
                }
            });


            bottomSheetDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "OptionsBottomSheetDialog");
        });


    }

    public void updateData(List<PopisnaStavka> newData) {
        stavke = newData;
        notifyDataSetChanged();
    }

    private void deleteStavka(PopisnaStavka popisnaStavka) {
        new PopisnaStavkaTask(null, popisnaStavkaDao, PopisnaStavkaTask.OperationType.DELETE, popisnaStavka).execute();
        // Osvježi listu nakon brisanja
        this.stavke.remove(popisnaStavka);
        notifyDataSetChanged();
    }

    private void updateStavka(PopisnaStavka popisnaStavka) {
        Bundle args = new Bundle();
        args.putSerializable("popisnaStavka", popisnaStavka);
        args.putSerializable("popisnaListaId", popisnaStavka.getPopisnaListaId());

        NavController navController = Navigation.findNavController((FragmentActivity) context, R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.add_popisna_stavka_fragment, args);

    }

    @Override
    public int getItemCount() {
        return stavke.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nazivOsnovnogSredstvaTextView;
        public TextView trenutnaOsobaTextView;
        public TextView novaOsobaTextView;
        public TextView trenutnaLokacijaTextView;
        public TextView novaLokacijaTextView;

        public ViewHolder(FragmentPopsinaStavkaBinding binding) {
            super(binding.getRoot());
            nazivOsnovnogSredstvaTextView = binding.textViewNazivOsnovnogSredstva;
            trenutnaOsobaTextView = binding.textViewTrenutnaOsoba;
            novaOsobaTextView = binding.textViewNovaOsoba;
            trenutnaLokacijaTextView = binding.textViewTrenutnaLokacija;
            novaLokacijaTextView = binding.textViewNovaLokacija;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + nazivOsnovnogSredstvaTextView.getText() + "'";
        }
    }
}