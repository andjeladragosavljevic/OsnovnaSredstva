package org.unibl.etf.osnovasredstvaapp.ui.popisnastavka;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.unibl.etf.osnovasredstvaapp.R;
import org.unibl.etf.osnovasredstvaapp.dao.LokacijaDao;
import org.unibl.etf.osnovasredstvaapp.dao.ZaposleniDao;
import org.unibl.etf.osnovasredstvaapp.database.AppDatabase;
import org.unibl.etf.osnovasredstvaapp.entity.Lokacija;
import org.unibl.etf.osnovasredstvaapp.entity.PopisnaStavka;
import org.unibl.etf.osnovasredstvaapp.entity.Zaposleni;
import org.unibl.etf.osnovasredstvaapp.ui.popisnastavka.placeholder.PlaceholderContent.PlaceholderItem;
import org.unibl.etf.osnovasredstvaapp.databinding.FragmentPopsinaStavkaBinding;

import java.util.List;
import java.util.Locale;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class PopisnaStavkaRecyclerViewAdapter extends RecyclerView.Adapter<PopisnaStavkaRecyclerViewAdapter.ViewHolder> {

    private final List<PopisnaStavka> stavke;
    private  ZaposleniDao zaposleniDao;
    private  LokacijaDao lokacijaDao;
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

        Zaposleni trenutnaOsoba = zaposleniDao.getById(stavka.getTrenutnaOsobaId());
        Zaposleni novaOsoba = zaposleniDao.getById(stavka.getNovaOsobaId());
        Lokacija trenutnaLokacija = lokacijaDao.getById(stavka.getTrenutnaLokacijaId());
        Lokacija novaLokacija = lokacijaDao.getById(stavka.getNovaLokacijaId());

        // Popunite view-ove podacima iz stavke
        holder.nazivOsnovnogSredstvaTextView.setText(stavka.getOsnovnoSredstvoId() + ""); // Promijenite ovo ako imate naziv u entitetu

        // Prikaz imena trenutne i nove osobe
        holder.trenutnaOsobaTextView.setText("Trenutna osoba: " + (trenutnaOsoba != null ? trenutnaOsoba.getIme() + " " + trenutnaOsoba.getPrezime() : "N/A"));
        holder.novaOsobaTextView.setText("Nova osoba: " + (novaOsoba != null ? novaOsoba.getIme() + " " + novaOsoba.getPrezime() : "N/A"));

        // Prikaz trenutne i nove lokacije
        holder.trenutnaLokacijaTextView.setText("Trenutna lokacija: " + (trenutnaLokacija != null ? trenutnaLokacija.getAdresa() + ", " + trenutnaLokacija.getGrad() : "N/A"));
        holder.novaLokacijaTextView.setText("Nova lokacija: " + (novaLokacija != null ? novaLokacija.getAdresa() + ", " + novaLokacija.getGrad() : "N/A"));
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