package cat.xojan.fittracker.main.fragment;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cat.xojan.fittracker.R;

public class HistoryListAdapter extends WearableListView.Adapter {

    private static final String RUNNING = "Running";
    private static final String BIKING = "Biking";
    private static final String WALKING = "Walking";

    private Context mContext;
    private LayoutInflater  mInflater;
    private String[] mDataSet;

    public HistoryListAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDataSet = context.getResources().getStringArray(R.array.activities);
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(mInflater.inflate(R.layout.list_item, null));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
        ItemViewHolder itemHolder = (ItemViewHolder) holder;

        TextView textView = itemHolder.textView;
        ImageView imageView = itemHolder.imageView;


        textView.setText(mDataSet[position]);
        switch (mDataSet[position]) {
            case RUNNING:
                imageView.setImageDrawable(mContext.getResources()
                        .getDrawable(R.drawable.ic_running30));
                break;
            case WALKING:
                imageView.setImageDrawable(mContext.getResources()
                        .getDrawable(R.drawable.ic_walking3));
                break;
            case BIKING:
                imageView.setImageDrawable(mContext.getResources()
                        .getDrawable(R.drawable.ic_biking2));
                break;
            default:
                imageView.setImageDrawable(mContext.getResources()
                        .getDrawable(R.drawable.ic_running30));
                break;
        }

        // replace list item's metadata
        ((ItemViewHolder) holder).itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }

    // Provide a reference to the type of views you're using
    public static class ItemViewHolder extends WearableListView.ViewHolder {
        private TextView textView;
        private ImageView imageView;
        public ItemViewHolder(View itemView) {
            super(itemView);
            // find the text view within the custom item's layout
            textView = (TextView) itemView.findViewById(R.id.item_text);
            imageView = (ImageView) itemView.findViewById(R.id.item_image);
        }
    }
}
