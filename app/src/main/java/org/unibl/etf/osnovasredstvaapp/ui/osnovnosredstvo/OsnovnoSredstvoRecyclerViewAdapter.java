package org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.unibl.etf.osnovasredstvaapp.R;
import org.unibl.etf.osnovasredstvaapp.entity.OsnovnoSredstvo;
import org.unibl.etf.osnovasredstvaapp.ui.osnovnosredstvo.placeholder.PlaceholderContent.PlaceholderItem;
import org.unibl.etf.osnovasredstvaapp.databinding.FragmentOsnovnoSredstvoBinding;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class OsnovnoSredstvoRecyclerViewAdapter extends RecyclerView.Adapter<OsnovnoSredstvoRecyclerViewAdapter.ViewHolder> {

    private List<OsnovnoSredstvo> osnovnaSredstva;

    public OsnovnoSredstvoRecyclerViewAdapter(List<OsnovnoSredstvo> items) {
        osnovnaSredstva = items;
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
//        holder.mIdView.setText(osnovnoSredstvo.get(position).id);
//        holder.mContentView.setText(mValues.get(position).content);
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