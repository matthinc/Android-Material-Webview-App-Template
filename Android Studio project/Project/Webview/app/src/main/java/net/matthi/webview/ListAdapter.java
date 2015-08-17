package net.matthi.webview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.matthias.webview.R;

import java.util.ArrayList;

/**
 * Created by Matthias on 11.08.2015.
 */
public class ListAdapter extends ArrayAdapter {

    private final Context context;
    private final ArrayList<Pages> values;

    public ListAdapter(Context context, ArrayList<Pages> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.drawer_item_layout, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.textView);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
            textView.setText(values.get(position).title);
            if (values.get(position).icon != null)
                 imageView.setImageResource(values.get(position).icon.value);
            return rowView;
    }
}


