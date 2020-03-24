package com.example.mysqlexampleproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* Item Adapter Class for adapting list items into layout */
public class ItemAdapter extends BaseAdapter {

    LayoutInflater mInflater;

    Map<String, Double> map;
    List<String> names;
    List<Double> prices;

    //constructor, initialize
    public ItemAdapter(Context c, Map m){
        mInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        map = m;
        names = new ArrayList<String>(map.keySet());
        prices = new ArrayList<Double>(map.values());
    }
    //getter method for number of items in map/DB
    @Override
    public int getCount(){
        return map.size();
    }
    //getter method for name of item at position
    @Override
    public Object getItem(int position){
        return names.get(position);
    }
    //getter method for item ID
    @Override
    public long getItemId(int position){
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        //inflate item layout, and put name & price
        View v = mInflater.inflate(R.layout.item_layout, null);
        TextView nameTextView = (TextView) v.findViewById(R.id.nameTextView);
        TextView priceTextView = (TextView) v.findViewById(R.id.priceTextView);
        //set TextViews for name and price of item
        nameTextView.setText(names.get(position));
        priceTextView.setText("$" + prices.get(position).toString());
        //return the view (layout) after inflating and setting text
        return v;
    }

}
