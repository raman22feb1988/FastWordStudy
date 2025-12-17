package com.tuchwords.wordstudy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {
    Context con;
    int _resource;
    List<Filter> lival1;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View itemView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            itemView = view;
        }

        public View getView() {
            return itemView;
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public FilterAdapter(Context context, int resource, List<Filter> li1) {
        con = context;
        _resource = resource;
        lival1 = li1;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FilterAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text, viewGroup, false);
        return new FilterAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FilterAdapter.ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        View v = viewHolder.getView();

        TextView t1 = v.findViewById(R.id.textview67);
        t1.setText((lival1.get(position)).getName());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return lival1.size();
    }
}