package cat.xojan.fittracker.presentation.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cat.xojan.fittracker.R;

/**
 * Created by Joan on 23/01/2016.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private final String[] mDataset;
    private final MenuClickListener mListener;

    /**
     * Interface for receiving click events from cells.
     */
    public interface MenuClickListener {
        public void onClick(View view, int position);
    }

    /**
     * Custom viewholder for our menu views.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mTextView;

        public ViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }

    public MenuAdapter(String[] dataset, MenuClickListener listener) {
        mDataset = dataset;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater vi = LayoutInflater.from(parent.getContext());
        View v = vi.inflate(R.layout.drawer_list_item, parent, false);
        TextView tv = (TextView) v.findViewById(android.R.id.text1);
        return new ViewHolder(tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mDataset[position]);
        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
