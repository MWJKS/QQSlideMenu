package com.example.lizhe.qqsilemenu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListViewAdapter extends ArrayAdapter<String> {
    String[] mStrings;

    public ListViewAdapter(Context context, int resource, String[] objects) {
        super(context, resource, objects);
        mStrings=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=LayoutInflater.from(getContext()).inflate(R.layout.item_listview,null);
        TextView textView=view.findViewById(R.id.listView);
        textView.setText(mStrings[position]);
        return view;
    }
}
