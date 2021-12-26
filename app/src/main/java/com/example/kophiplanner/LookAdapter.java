package com.example.kophiplanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;

public class LookAdapter extends BaseAdapter {
    private LinkedList<Shop> shops;
    private Available av=new Available(0,null);
    private LayoutInflater inflter;
    private String name;
    LookAdapter(Context applicationContext, LinkedList<Shop> shops, Available av,String name, int code){
        if (av != null) {
            this.av = av;
        }
        this.name=name;
        this.shops=shops;
        Log.e("OSKOUR",Integer.toString(code));
        inflter = (LayoutInflater.from(applicationContext));
        Shop deb = new Shop(-1,"deb","deb");
        shops.addFirst(deb);


    }

    @Override
    public int getCount(){
        return shops.size();
    }

    @Override
    public Object getItem(int i){
        return shops.get(i);
    }
    @Override
    public long getItemId(int i){
        if(i==-1){
            return 0;
        }
        return shops.get(i).getkID();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup){

        Shop s = shops.get(i);
        int id = s.getkID();
        //Log.e("FONCT","JE SUIS PASSé ICI"+Integer.toString(getCount()));
        if(id==-1){
            view = inflter.inflate(R.layout.view_shoplist1,null);

            EditText et = (EditText) view.findViewById(R.id.new_name);
            et.setText(name);
            et.setEnabled(false);
            if (shops.size()==1){
                TextView mag = (TextView) view.findViewById(R.id.tshops);
                mag.setVisibility(View.GONE);
            }
        }else if(shops.size()>1){
            view = inflter.inflate(R.layout.view_looklist,null);
            TextView t = (TextView) view.findViewById(R.id.shop_name);
            t.setText(shops.get(i).getName());
            ImageView simg =view.findViewById(R.id.shp_img);
            simg.setTag(i);
            try {
                Log.e("Uri",shops.get(i).getImg());

                File f= new File(shops.get(i).getImg());
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap slctImg = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
                simg.setImageBitmap(slctImg);
            } catch (FileNotFoundException e){
                Log.e("tag","fichier non trouvé 2 : "+shops.get(i).getImg());
                simg.setImageResource(R.drawable.image_unavailable);
            } catch (java.io.IOException e){
                Log.e("tag", "onActivityResult: ioException");
                simg.setImageResource(R.drawable.image_unavailable);
            } catch (SecurityException e){
                Log.e("tag", "setImg: SECURITY EXCEPTION");
                simg.setImageResource(R.drawable.image_unavailable);

            }
        }



        return view;


    }

}
