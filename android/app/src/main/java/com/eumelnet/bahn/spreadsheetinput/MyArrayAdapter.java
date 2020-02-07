package com.eumelnet.bahn.spreadsheetinput;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.List;

public class MyArrayAdapter extends ArrayAdapter<MyDataModel> {

    List<MyDataModel> modelList;
    public Context context;
    private LayoutInflater mInflater;

    // Constructors
    MyArrayAdapter(QuestionsActivity context, List<MyDataModel> objects) {
        super((Context) context, 0, objects);
        this.context = (Context) context;
        this.mInflater = LayoutInflater.from((Context) context);
        modelList = objects;
    }

    @Override
    public MyDataModel getItem(int position) {
        return modelList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {

            View view = mInflater.inflate(R.layout.layout_row_view, parent, false);
            vh = ViewHolder.create((CoordinatorLayout) view);
            view.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        MyDataModel item = getItem(position);

        assert item != null;
        vh.textViewDatum.setText(item.getDatum());
        vh.textViewZug.setText(item.getZug());
        vh.textViewAbfahrt.setText(item.getAbfahrt());
        vh.textViewZiel.setText(item.getZiel());

        return vh.rootView;
    }

    /**
     * ViewHolder class for layout.<br />
     * <br />
     * Auto-created on 2016-01-05 00:50:26 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private static class ViewHolder {
        final CoordinatorLayout rootView;

        final TextView textViewDatum;
        final TextView textViewZug;
        final TextView textViewAbfahrt;
        final TextView textViewZiel;

        private ViewHolder(CoordinatorLayout rootView, TextView textViewDatum, TextView textViewZug, TextView textViewAbfahrt, TextView textViewZiel) {
            this.rootView = rootView;
            this.textViewDatum = textViewDatum;
            this.textViewZug = textViewZug;
            this.textViewAbfahrt = textViewAbfahrt;
            this.textViewZiel = textViewZiel;
        }

        static ViewHolder create(CoordinatorLayout rootView) {
            TextView textViewDatum = (TextView) rootView.findViewById(R.id.textViewDatum);
            TextView textViewZug = (TextView) rootView.findViewById(R.id.textViewZug);
            TextView textViewAbfahrt = (TextView) rootView.findViewById(R.id.textViewAbfahrt);
            TextView textViewZiel = (TextView) rootView.findViewById(R.id.textViewZiel);
            return new ViewHolder(rootView, textViewDatum, textViewZug, textViewAbfahrt, textViewZiel);
        }
    }
}