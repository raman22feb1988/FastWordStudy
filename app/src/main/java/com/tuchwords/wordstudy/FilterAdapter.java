package com.tuchwords.wordstudy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.ViewHolder> {
    Context con;
    int _resource;
    List<Filter> lival1;
    int loading;
    sqliteDB database;

    final TextView[] previous = {null};
    Filter selection;

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
    public FilterAdapter(Context context, int resource, List<Filter> li1, int loader, sqliteDB db) {
        con = context;
        _resource = resource;
        lival1 = li1;
        loading = loader;
        database = db;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.load, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        View v = viewHolder.getView();

        int nightModeFlags =
                con.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        int white = (nightModeFlags == Configuration.UI_MODE_NIGHT_YES ? Color.BLACK : Color.WHITE);

        TextView t1 = v.findViewById(R.id.textview68);
        t1.setText((lival1.get(position)).getName());
        t1.setTextSize(loading);
        t1.setBackgroundColor(white);

        v.setOnClickListener(v1 -> {
            if (previous[0] != null) {
                previous[0].setBackgroundColor(white);
            }

            TextView t2 = v1.findViewById(R.id.textview68);
            t2.setBackgroundColor(Color.BLUE);
            previous[0] = t2;
            selection = lival1.get(viewHolder.getBindingAdapterPosition());
        });

        v.setOnLongClickListener(v2 -> {
            LayoutInflater inflater = LayoutInflater.from(con);
            final View yourCustomView = inflater.inflate(R.layout.update, null);

            TextView t3 = yourCustomView.findViewById(R.id.textview69);
            EditText e1 = yourCustomView.findViewById(R.id.edittext31);

            Filter filterObject = lival1.get(viewHolder.getBindingAdapterPosition());
            t3.setText("Current name: " + filterObject.getName());
            e1.setText(filterObject.getName());

            AlertDialog dialog = new AlertDialog.Builder(con)
                    .setTitle("Rename saved word list")
                    .setView(yourCustomView)
                    .setPositiveButton("OK", (dialog1, whichButton) -> {
                        String newName = ((e1.getText()).toString()).replace("\"", "'");
                        t1.setText(newName);
                        database.saveFilter(filterObject.getSerial(), newName);
                    }).create();
            dialog.show();

            return true;
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return lival1.size();
    }

    public Filter getSelection() {
        return selection;
    }
}