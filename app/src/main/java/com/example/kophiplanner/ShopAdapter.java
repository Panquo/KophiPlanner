package com.example.kophiplanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;


public class ShopAdapter extends BaseAdapter {
    Context context;
    private LinkedList<Shop> shops;
    private Available av=new Available(0,null);
    private LayoutInflater inflter;
    private String name;
    ShopAdapter(Context applicationContext, LinkedList<Shop> shops, Available av,String name, int code){
        this.context=applicationContext;
        if (av != null) {
            this.av = av;
        }
        this.name=name;
        this.shops=shops;
        //Log.e("OSKOUR",Integer.toString(code));
        inflter = (LayoutInflater.from(applicationContext));
        Shop deb = new Shop(-1,"deb","deb");
        shops.addFirst(deb);
        Shop s = new Shop(0,"null","null");
        shops.add(s);

    }

    public LinkedList<Boolean> getavs(){return av.getShops();}

    @Override
    public int getCount(){
        return shops.size();
    }

    public LinkedList<Shop> getShops(){
        LinkedList<Shop> s = new LinkedList<>();
        for(int i=0;i<shops.size();i++){
            if (shops.get(i).getkID()!=-1 && shops.get(i).getkID()!=0){
                s.add(shops.get(i));
            }
        }
        return s;
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

    public void addShop(Shop shop){
        shops.add(shops.size()-1,shop);
        LinkedList<Boolean> list = av.getShops();
        list.add(false);
        av.setShops(list);
        av.affShops();
        for(int i=0;i<shops.size();i++){
            System.out.println(shops.get(i).getName()+" : "+shops.get(i).getkID());
        }
    }
    public Shop getShop(int pos) {
        return shops.get(pos);
    }

    public void setShopImg(String imag, int pos){
        shops.get(pos).setImg(imag);
    }

    public void delShop(int pos){
        shops.remove(pos);
        LinkedList<Boolean> list = av.getShops();
        list.remove(pos-1);
        av.setShops(list);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        Shop s = shops.get(i);
        int id = s.getkID();
        //Log.e("FONCT", "JE SUIS PASSé ICI" + Integer.toString(getCount()));
        if (id == -1) {
            view = inflter.inflate(R.layout.view_shoplist1, null);
            holder = new ViewHolder();
            holder.edt = (EditText) view.findViewById(R.id.new_name);
            holder.edt.setText(name);
            holder.edt.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    name = s.toString();
                }
            });
        } else if (id == 0) {
            view = inflter.inflate(R.layout.view_shoplist2, null);


        } else if (shops.size() > 1) {
            //Log.e("OUI","OUI");
            view = inflter.inflate(R.layout.view_shoplist, null);
            TextView t = (TextView) view.findViewById(R.id.shop_name);
            t.setText(shops.get(i).getName());
            CheckBox chk = (CheckBox) view.findViewById(R.id.checkbox);
            chk.setTag(i);

            //si il n'existe pas de Available, on en créé un, rempli de 'false'.
            if (av.getShops() == null) {
                LinkedList<Boolean> avs = new LinkedList<Boolean>();
                for (int j = 0; j < shops.size(); j++) {
                    avs.add(false);
                }
                av = new Available(0, avs);
            }
            // On check si les magasins sont séléctionnés
            if (av.getShops().get(i-1) == null) {
                chk.setChecked(false);
            } else {
                chk.setChecked(av.getShops().get(i-1));
            }

            ImageButton dbut = (ImageButton) view.findViewById(R.id.delete_shop);
            dbut.setTag(i);

            ImageButton simg = (ImageButton) view.findViewById(R.id.shp_img);
            simg.setTag(i);
            //on met l'image demandée, ou une image default si pas correcte ou pas trouvée.
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

    private class ViewHolder {
        EditText edt;
    }

}
