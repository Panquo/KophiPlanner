package com.example.kophiplanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListAdapter extends BaseAdapter {
    Context context;
    LinkedList<Coffee> coffees;
    LayoutInflater inflter;
    List<Integer> qt = new ArrayList<Integer>();
    public ListAdapter(Context applicationContext, LinkedList<Coffee> coffees, List<Integer> qt){
        this.context = applicationContext;
        this.coffees = coffees;
        this.qt=qt;
        inflter = (LayoutInflater.from(applicationContext));

    }
    @Override
    public int getCount(){
        return coffees.size();
    }

    @Override
    public Object getItem(int i){
        return null;
    }
    @Override
    public long getItemId(int i){
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup){
        view = inflter.inflate(R.layout.view_listview,null);
        Coffee coffee = coffees.get(i);
        TextView t1 = (TextView) view.findViewById(R.id.list_name);
        t1.setText(coffee.getPays());
        TextView t2 = (TextView) view.findViewById(R.id.list_qt);
        t2.setText(Integer.toString(qt.get(i)));

        return view;


    }
}
