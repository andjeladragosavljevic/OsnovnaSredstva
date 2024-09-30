package org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.system.Os;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.unibl.etf.osnovasredstvaapp.R;
import org.unibl.etf.osnovasredstvaapp.dao.OsnovnoSredstvoDao;
import org.unibl.etf.osnovasredstvaapp.dao.ZaposleniDao;
import org.unibl.etf.osnovasredstvaapp.database.AppDatabase;
import org.unibl.etf.osnovasredstvaapp.entity.OsnovnoSredstvo;
import org.unibl.etf.osnovasredstvaapp.entity.Zaposleni;
import org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo.placeholder.PlaceholderContent.PlaceholderItem;
import org.unibl.etf.osnovasredstvaapp.databinding.FragmentOsnovnoSredstvoBinding;
import org.unibl.etf.osnovasredstvaapp.ui.zaposleni.ZaposleniBottomSheetDialogFragment;
import org.unibl.etf.osnovasredstvaapp.ui.zaposleni.ZaposleniTask;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class OsnovnoSredstvoRecyclerViewAdapter extends RecyclerView.Adapter<OsnovnoSredstvoRecyclerViewAdapter.ViewHolder> {

    private List<OsnovnoSredstvo> osnovnaSredstva;
    private Context context;
    OsnovnoSredstvoDao osnovnoSredstvoDao; //= AppDatabase.getInstance(context).osnovnoSredstvoDao();

    public OsnovnoSredstvoRecyclerViewAdapter(List<OsnovnoSredstvo> items, Context context) {
        osnovnaSredstva = items;
        this.context = context;
        if (context != null) {
            osnovnoSredstvoDao = AppDatabase.getInstance(context).osnovnoSredstvoDao();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_osnovno_sredstvo, parent, false);
        return new ViewHolder(view);
//urn new ViewHolder(FragmentOsnovnoSredstvoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    public void updateData(List<OsnovnoSredstvo> newData) {
        osnovnaSredstva = newData;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        OsnovnoSredstvo osnovnoSredstvo = osnovnaSredstva.get(position);
        holder.mContentView.setText(osnovnoSredstvo.getNaziv());  //

        holder.itemView.setOnClickListener(v -> {
            // Create and show the BottomSheetDialog
            OsnovnoSredstvoBottomSheetDialogFragment bottomSheetDialog = new OsnovnoSredstvoBottomSheetDialogFragment(osnovnaSredstva.get(position), new OsnovnoSredstvoBottomSheetDialogFragment.OnOptionSelectedListener() {
                @Override
                public void onDelete(OsnovnoSredstvo osnovnoSredstvo) {
                    deleteOsnovnoSredstvo(osnovnoSredstvo);
                }

                @Override
                public void onUpdate(OsnovnoSredstvo osnovnoSredstvo) {
                    updateOsnovnoSredstvo(osnovnoSredstvo);
                }

                @Override
                public void onShowDetails(OsnovnoSredstvo osnovnoSredstvo) {
                    showDetails(osnovnoSredstvo);
                }
            });

            bottomSheetDialog.show(((FragmentActivity) context).getSupportFragmentManager(), "OptionsBottomSheetDialog");
        });
    }
    private void deleteOsnovnoSredstvo(OsnovnoSredstvo osnovnoSredstvo) {
        new OsnovnoSredstvoTask(null, osnovnoSredstvoDao, OsnovnoSredstvoTask.OperationType.DELETE, osnovnoSredstvo).execute();
        // Osvježi listu nakon brisanja
        this.osnovnaSredstva.remove(osnovnoSredstvo);
        notifyDataSetChanged();
    }

    private void updateOsnovnoSredstvo(OsnovnoSredstvo osnovnoSredstvo) {
        NavController navController = Navigation.findNavController((FragmentActivity) context, R.id.nav_host_fragment_content_main);
        Bundle args = new Bundle();
        args.putSerializable("osnovnoSredstvo", osnovnoSredstvo);
        navController.navigate(R.id.action_nav_osnovno_sredsvo_to_add_osnovno_sredstvo_fragment, args);
    }

    private void showDetails(OsnovnoSredstvo osnovnoSredstvo) {
        NavController navController = Navigation.findNavController((FragmentActivity) context, R.id.nav_host_fragment_content_main);
        Bundle args = new Bundle();
        args.putSerializable("osnovnoSredstvo", osnovnoSredstvo);
        navController.navigate(R.id.action_nav_osnovno_sredsvo_to_nav_details, args);
    }
    @Override
    public int getItemCount() {
        return osnovnaSredstva.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}