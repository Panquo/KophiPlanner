package com.example.kophiplanner;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kophiplanner.Views.View_Holder;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<View_Holder> {
    Context context;
    LinkedList<Coffee> coffees;
    RecyclerItemClickListener listener;
    LayoutInflater inflter;
    public CustomAdapter(Context applicationContext, LinkedList<Coffee> coffees){
        this.context = applicationContext;
        this.listener = listener;
        this.coffees = coffees;
        inflter = (LayoutInflater.from(applicationContext));


    }

    @NonNull
    @Override
    public View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.activity_gridview, parent, false);
        return new View_Holder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull View_Holder holder, int position) {
        holder.updateWithCoffee(this.coffees.get(position),context);
    }


    @Override
    public long getItemId(int i){
        if(i==-1){
            return 0;
        }
        return coffees.get(i).getkID();
    }

    @Override
    public int getItemCount() {
        return coffees.size();
    }

    /*@Override
    public View getView(int i, View view, ViewGroup viewGroup){
        Log.e("FONCT","JE SUIS PASSé ICI" + Integer.toString(i));

        int id = coffees.get(i).getkID();

            view = inflter.inflate(R.layout.activity_gridview,null);
            Coffee coffee = coffees.get(i);
            TextView t1 = (TextView) view.findViewById(R.id.t1);
            t1.setText(coffee.getPays());
            ImageView kimg = (ImageView) view.findViewById(R.id.img_list);
            try {
                Uri uristring = Uri.parse(coffee.getkImage());
                Bitmap slctImg = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uristring);
                kimg.setImageBitmap(slctImg);
            } catch (FileNotFoundException e){
                Log.e("tag","fichier non trouvé 2 : "+coffee.getkImage());
                kimg.setImageResource(R.drawable.image_unavailable);
            } catch (java.io.IOException e){
                Log.e("tag", "onActivityResult: ioException");
                kimg.setImageResource(R.drawable.image_unavailable);
            } catch (SecurityException e){
                Log.e("tag", "setImg: SECURITY EXCEPTION");
                kimg.setImageResource(R.drawable.image_unavailable);

            }
            ImageView img = (ImageView) view.findViewById(R.id.stock_list);
            int quant=coffee.getStock();
            if(quant<=0){
                img.setImageResource(R.drawable.z);
            }else if(quant<=5){
                img.setImageResource(R.drawable.c);
            }else if(quant<=10){
                img.setImageResource(R.drawable.d);
            }else if(quant<=15){
                img.setImageResource(R.drawable.q);
            }else if(quant<=20){
                img.setImageResource(R.drawable.v);
            }else {
                img.setImageResource(R.drawable.vc);
            }

        return view;


    }*/
}
