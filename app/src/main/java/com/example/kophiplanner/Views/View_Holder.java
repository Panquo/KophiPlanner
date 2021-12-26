package com.example.kophiplanner.Views;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.TestLooperManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.kophiplanner.Coffee;
import com.example.kophiplanner.CoffeeDB;
import com.example.kophiplanner.ListActivity;
import com.example.kophiplanner.ModifyActivity;
import com.example.kophiplanner.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class View_Holder extends RecyclerView.ViewHolder {

    TextView tV;
    ImageView iV;
    ImageView sV;

    public View_Holder(View itemView) {
        super(itemView);
        tV = (TextView) itemView.findViewById(R.id.t1);
        iV = (ImageView) itemView.findViewById(R.id.img_list);
        sV = (ImageView) itemView.findViewById(R.id.stock_list);
    }

    public void updateWithCoffee(Coffee kof, Context context){
        this.tV.setText(kof.getPays());
        Log.e("noimg?","id : "+kof.getkID()+" - img : "+kof.getkImage());
        if(kof.getkImage().equals("no_image")){
            Bitmap unav = BitmapFactory.decodeResource(context.getResources(),R.drawable.image_unavailable);
            iV.setImageBitmap(unav);
        }else {
            Bitmap slctImg = getBitmap(kof, context);
            if (slctImg != null) {
                //Log.e("imageudt", kof.getkImage());
                iV.setImageBitmap(slctImg);
            } else {
                Bitmap unav = BitmapFactory.decodeResource(context.getResources(), R.drawable.image_unavailable);
                CoffeeDB cdb = new CoffeeDB(context);
                kof.setkImage("no_image");
                cdb.open();
                cdb.update(kof);
                cdb.close();
                iV.setImageBitmap(unav);
            }

        }
        int quant = kof.getStock();
        Log.e("quant",": "+quant);
        if (quant <= 0) {
            sV.setImageResource(R.drawable.z);
        } else if (quant <= 5) {
            sV.setImageResource(R.drawable.c);
        } else if (quant <= 10) {
            sV.setImageResource(R.drawable.d);
        } else if (quant <= 15) {
            sV.setImageResource(R.drawable.q);
        } else if (quant <= 20) {
            sV.setImageResource(R.drawable.v);
        } else {
            sV.setImageResource(R.drawable.vc);
        }
    }
    public Bitmap getBitmap(Coffee coffee,Context context) {
        Bitmap bitmap = null;
        try {
            //Log.e("image",coffee.getkImage());
            File f = new File(coffee.getkImage());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);
            bitmap = getResizedBitmap(bitmap,200);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            try {
                Uri uristring = Uri.parse(coffee.getkImage());
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uristring);
                coffee.setkImage(saveToInternalStorage(bitmap, "img_" + coffee.getkID(), context));
                CoffeeDB cdb = new CoffeeDB(context);
                cdb.open();
                cdb.update(coffee);
                cdb.close();
                return getBitmap(coffee, context);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
        //-----------------------------------------------------------------------------------------------

    private String saveToInternalStorage(Bitmap bitmapImage,String name,Context context) {
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, name + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Log.e("path",mypath.getAbsolutePath());
        return mypath.getAbsolutePath();
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }
}