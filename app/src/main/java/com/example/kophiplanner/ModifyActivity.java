package com.example.kophiplanner;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.widget.Toolbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.NonReadableChannelException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import jp.wasabeef.glide.transformations.BlurTransformation;


public class ModifyActivity extends AppCompatActivity {

    int idk;
    int qt;
    String img;
    String pays;
    Coffee cof = new Coffee();
    Available av = new Available();
    LinkedList<Shop> shops;
    int pos;


    int actual_take;
    ImageButton addPhoto;
    final int PICKFILE_RESULT_CODE = 12;
    final int PICKFILE_RESULT_CODE2 = 24;
    CoffeeDB cdb;
    AvailableDB adb;
    ShopDB sdb;
    int code;
    ListView shopv;

    ShopAdapter sadapter;

    boolean modif = false;


    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        code = getIntent().getIntExtra(ListActivity.MODCODE,4);


        switch (code){
            case 3:
                setContentView(R.layout.activity_modify);
                break;
            //case 0:
            case 1:
            case 2:
                setContentView(R.layout.activity_look);
                break;
        }

        addPhoto = (ImageButton) findViewById(R.id.buttontest);

        cdb = new CoffeeDB(this);
        adb = new AvailableDB(this);
        sdb = new ShopDB(this);

        RelativeLayout rlt = (RelativeLayout) findViewById(R.id.bottom_take);
        ImageView kstok = (ImageView) findViewById(R.id.kStock);
        kstok.setVisibility(View.VISIBLE);
        TextView ktext = (TextView) findViewById(R.id.numStock);
        ktext.setVisibility(View.VISIBLE);
       // this.configureToolbar();

        //Toast.makeText(this, Integer.toString(code), Toast.LENGTH_SHORT).show();

        idk = getIntent().getIntExtra("id",-1);
        //Toast.makeText(this, Integer.toString(idk), Toast.LENGTH_SHORT).show();


        if (idk!=9999 && idk!=-1 && idk!=999){

            shopv = findViewById(R.id.new_shop);
            TextView qtEdit = (TextView) findViewById(R.id.numStock);
            cdb.open();
            cof = cdb.findByID(idk);
            cdb.close();
            adb.open();
            //Log.e("IDK",Integer.toString(idk));
            av = adb.findByID(idk);
            int cols = adb.getColumnCount()-1;
            adb.close();
            sdb.open();
            shops = sdb.selectAll();
            sdb.close();
            img = cof.getkImage();
            setImg(img);
            qt = cof.getStock();
            pays = cof.getPays();
            Log.e("PAYS",pays);
            if(code==3){
                sadapter = new ShopAdapter(this,shops,av,pays,code);
                shopv.setAdapter(sadapter);
            }else{
                LinkedList<Shop> shopTemp = new LinkedList<>();
                Log.e("Shops : ", "shops : "+shops.size()+" , available : "+cols);
                while(shops.size()>cols){
                    int id = shops.get(shops.size()-1).getkID();
                    sdb.open();
                    sdb.delete(id);
                    sdb.close();
                    shops.removeLast();
                }
                Log.e("Shops : ", "shops : "+shops.size()+" , available : "+cols);

                for(int i=0;i<shops.size();i++){
                    if(av.getShops().get(i)){
                        shopTemp.add(shops.get(i));
                    }
                }
                shops=shopTemp;
                LookAdapter ladapter = new LookAdapter(this,shops,av,pays,code);
                shopv.setAdapter(ladapter);
            }



            //Toast.makeText(this, Integer.toString(qt), Toast.LENGTH_SHORT).show();
            qtEdit.setText(qt+"");
            setImg(img);
            setStock(qt);

        }else if (idk==-1){
            System.out.println("No id found");
            finish();
        }else if(idk==999){
            shopv = findViewById(R.id.new_shop);
            pays=getIntent().getStringExtra("pays");
            av = AvEmpty();
            sdb.open();
            shops = sdb.selectAll();
            sdb.close();
            sadapter = new ShopAdapter(this,shops,av,pays,code);
            shopv.setAdapter(sadapter);
        }else if(idk==9999){
            shopv = findViewById(R.id.new_shop);
            av = AvEmpty();
            sdb.open();
            shops = sdb.selectAll();
            sdb.close();
            sadapter = new ShopAdapter(this,shops,av,pays,code);
            shopv.setAdapter(sadapter);

        }
        this.configureToolbar();

        switch (code){
            case 1:
                rlt.setVisibility(View.VISIBLE);
                Button valid_take = (Button) findViewById(R.id.ok_cup);
                ImageButton more_take = findViewById(R.id.more_cup);
                ImageButton less_take = findViewById(R.id.less_cup);
                final EditText num_take = (EditText) findViewById(R.id.take_num);
                actual_take = 0;

                valid_take.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.putExtra("kof",cof);
                        intent.putExtra("qt",actual_take);
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                });

                more_take.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(actual_take+1<=qt) {
                            actual_take = actual_take + 1;
                            num_take.setText(Integer.toString(actual_take));
                        }
                    }
                });
                less_take.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(actual_take>=1) {
                            actual_take = actual_take - 1;
                            num_take.setText(Integer.toString(actual_take));
                        }
                    }
                });
                 num_take.addTextChangedListener(new TextWatcher() {
                     @Override
                     public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                     @Override
                     public void onTextChanged(CharSequence s, int start, int before, int count) { }

                     @Override
                     public void afterTextChanged(Editable s) {
                         if(!(s.toString().matches(""))) {
                             actual_take = Integer.parseInt(s.toString());
                             if (actual_take > qt) {
                                 actual_take = qt;
                                 num_take.setText(Integer.toString(actual_take));
                             } else if (actual_take < 0) {
                                 actual_take = 0;
                                 num_take.setText(Integer.toString(actual_take));
                             }
                         }
                     }
                 });

                 break;


            /*case 0:
                rls.setVisibility(View.VISIBLE);
                rlt.setVisibility(View.GONE);
                Button valid_stock1 = (Button) findViewById(R.id.ok_stock);
                Button more_stock1 = (Button) findViewById(R.id.more_stock);
                Button less_stock1 = (Button) findViewById(R.id.less_stock);
                final EditText num_stock1 = (EditText) findViewById(R.id.stock_num);
                actual_take =0;
                valid_stock1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        qt+=actual_take;
                        setCof();
                        cdb.open();
                        cdb.update(cof);
                        cdb.close();
                        Intent intent = new Intent();
                        intent.putExtra("idk",idk);
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                });

                more_stock1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(actual_take+1<=999) {
                            actual_take = actual_take + 1;
                            num_stock1.setText(Integer.toString(actual_take));
                        }
                    }
                });
                less_stock1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(actual_take>=1) {
                            actual_take = actual_take - 1;
                            num_stock1.setText(Integer.toString(actual_take));
                        }
                    }
                });
                num_stock1.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) { }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(!(s.toString().matches(""))) {
                            actual_take = Integer.parseInt(s.toString());
                            if (actual_take > 999) {
                                actual_take = 999;
                                num_stock1.setText(Integer.toString(actual_take));
                            } else if (actual_take < 0) {
                                actual_take = 0;
                                num_stock1.setText(Integer.toString(actual_take));
                            }
                        }
                    }
                });

                break;*/
            case 2:
                RelativeLayout rlrlr = findViewById(R.id.bottom_nbcup);
                rlrlr.setVisibility(View.GONE);
                rlt.setVisibility(View.GONE);

                break;
            case 3:


                addPhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent fileintent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            fileintent.setType("image/*");
                            try {
                                startActivityForResult(Intent.createChooser(fileintent,"Select Picture"), PICKFILE_RESULT_CODE);
                            } catch (ActivityNotFoundException e) {
                                Log.e("tag", "No activity can handle picking a file");
                            }
                        }
                });

                ImageButton more_stock = findViewById(R.id.more_stock);
                ImageButton less_stock = findViewById(R.id.less_stock);
                final EditText quant = findViewById(R.id.quantity);
                final TextView num_stock = findViewById(R.id.numStock);
                quant.setText("0");
                actual_take=0;
                more_stock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(actual_take+1<=999) {
                            actual_take = actual_take + 1;
                            quant.setText(Integer.toString(actual_take));
                        }
                    }
                });
                less_stock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(actual_take>=1) {
                            actual_take = actual_take - 1;
                            quant.setText(Integer.toString(actual_take));
                        }
                    }
                });
                quant.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) { }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(!(s.toString().matches(""))) {
                            actual_take = Integer.parseInt(s.toString());
                            if (actual_take > 999) {
                                actual_take = 999;
                                quant.setText(Integer.toString(actual_take));
                            } else if (actual_take < 0) {
                                actual_take = 0;
                                quant.setText(Integer.toString(actual_take));
                            }
                        }
                    }
                });

                /*addShop = (Button) findViewById(R.id.add_shop);
                addShop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        RelativeLayout rl = (RelativeLayout) findViewById(R.id.shop_popup);
                        rl.setVisibility(View.VISIBLE);
                    }
                });*/


                break;
            case 4:
                Toast.makeText(this, "ERROR MOD", Toast.LENGTH_SHORT).show();
                break;
        }

    }



    @Override
    protected void onResume(){
        super.onResume();
        shopv.invalidateViews();
    }

    public void ShopAddHandler(View view){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        final View popupView = inflater.inflate(R.layout.popup_window, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.setAnimationStyle(R.style.anim_popup);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        View container = (View) popupWindow.getContentView().getParent();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.3f;
        wm.updateViewLayout(container, p);
        Button bt=(Button) popupView.findViewById(R.id.ok_add);
        Button cancel = popupView.findViewById(R.id.cancel);
        final EditText etname = (EditText) popupView.findViewById(R.id.shop_name);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupWindow.dismiss();
            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etname.getText().toString().matches("")) {
                    Toast.makeText(getBaseContext(), "Please complete the informations", Toast.LENGTH_SHORT).show();
                } else {
                    popupWindow.dismiss();
                    String y = correct(etname.getText().toString());
                    Shop s = new Shop(9999,"", y);
                    sdb.open();
                    sdb.insert(s);
                    sdb.close();
                    adb.open();
                    adb.add_column(y);
                    adb.close();
                    //Toast.makeText(getBaseContext(), "is ok", Toast.LENGTH_SHORT).show();
                    sadapter.addShop(s);
                    shopv.invalidateViews();
                }
            }
        });

    }

    public void checkHandler(View view){
        CheckBox chk = view.findViewById(R.id.checkbox);
        int position = Integer.parseInt(chk.getTag().toString());
        Boolean b = av.getShops().get(position-1);
        if(b){
            av.getShops().set(position-1,false);
        }else{
            av.getShops().set(position-1,true);
        }
    }

    public void addImageHandler(View view){
        ImageButton imgb = view.findViewById(R.id.shp_img);
        pos = Integer.parseInt(imgb.getTag().toString());
        Intent fileintent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        fileintent.setType("image/*");
        try {
            startActivityForResult(Intent.createChooser(fileintent,"Select Picture"), PICKFILE_RESULT_CODE2);
        } catch (ActivityNotFoundException e) {
            Log.e("tag", "No activity can handle picking a file");
        }

    }

    public void deleteShopHandler(View view){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        ImageButton imgb = view.findViewById(R.id.delete_shop);
        pos = Integer.parseInt(imgb.getTag().toString());
        final Shop s = sadapter.getShop(pos);

        final View popupView = inflater.inflate(R.layout.popup_delete_coffee, null);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        ImageView modify = findViewById(R.id.kImage);
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.setAnimationStyle(R.style.anim_popup);
        popupWindow.showAtLocation(modify, Gravity.CENTER, 0, 0);
        View container = (View) popupWindow.getContentView().getParent();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.3f;
        wm.updateViewLayout(container, p);
        Button bt= popupView.findViewById(R.id.ok_del);
        Button cancel = popupView.findViewById(R.id.cancel);
        final TextView alrtTitle = popupView.findViewById(R.id.frame_title);
        final TextView alert = popupView.findViewById(R.id.alert);
        alrtTitle.setText("Supprimer "+s.getName()+" ?");
        alert.setText("Vous êtes sur le point de supprimer le magasin "+s.getName()+"\nVoulez-vous continuer ?");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

                //LinkedList<Shop> damn = sadapter.getShops();
                //Log.e("Img",Integer.toString(damn.size()));
                deleteFromInternalStorage(s.getImg());
                sdb.open();
                sdb.delete(s.getkID());
                sdb.close();
                sadapter.delShop(pos);
                adb.open();
                adb.deleteRow(sadapter.getShops());
                adb.close();
                shopv.invalidateViews();
            }
        });

    }


    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        switch(code){
            case 3:
                getMenuInflater().inflate(R.menu.menu_save,menu);
                break;
            case 2:
                getMenuInflater().inflate(R.menu.menu_modif,menu);
                break;
            case 1:
            //case 0:
                getMenuInflater().inflate(R.menu.menu_activity_list,menu);
                break;
            case 4:
                Toast.makeText(this, "ERROR MENU OPT", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void configureToolbar(){
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Toast.makeText(this, Integer.toString(code), Toast.LENGTH_SHORT).show();

        switch (code) {
            case 3:
                if(getIntent().getIntExtra("rc",0)==33){
                    getSupportActionBar().setTitle("Modifier le café "+pays);
                }else{
                    getSupportActionBar().setTitle("Ajouter un café");
                }
                break;
            case 2:
            case 1:
            //case 0:
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Café : "+pays);
                break;
            case 4:
                //Toast.makeText(this, "ERROR MENU TITLE", Toast.LENGTH_SHORT).show();
        }

    }
    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //Toast.makeText(this, "loul"+idk, Toast.LENGTH_SHORT).show();
        switch (item.getItemId()){

            case android.R.id.home:
                this.onBackPressed();
                finish();

                return true;
            case R.id.menu_save_save:


                if (isEmpty(R.id.new_name)) {
                    Toast.makeText(this, "Please complete the informations", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("idk",Integer.toString(idk));
                    switch (idk) {
                        case 999:
                        case 9999:

                            //Toast.makeText(this, "loul", Toast.LENGTH_SHORT).show();
                            EditText et = (EditText) findViewById(R.id.new_name);
                            shops = sadapter.getShops();
                            pays = et.getText().toString();
                            av.setShops(sadapter.getavs());
                            av.affShops();
                            for(int i=0;i<shops.size();i++){
                                System.out.println(shops.get(i).getName()+" : "+shops.get(i).getkID());
                            }
                            adb.open();
                            adb.setShops(shops);
                            adb.insert(av);
                            adb.close();
                            //System.out.println(img+" et "+name);
                            //Toast.makeText(this, "loul", Toast.LENGTH_SHORT).show();
                            if(img==null){
                                img="no_image";
                            }

                            cdb.open();
                            try {
                            Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(img));
                            Log.e("oui","id : "+cdb.getlastID());
                            img=saveToInternalStorage(bm,"img_"+cdb.getlastID());
                            } catch (FileNotFoundException e){
                                Log.e("FNFE","fichier non trouvé");

                            } catch (java.io.IOException e){
                                Log.e("IOE", "onActivityResult: IOException");

                            } catch (SecurityException e){
                                Log.e("SE", "onActivityResult: Security Exception");

                            }
                            Log.e("yes",img);
                            qt=0;
                            qt+=actual_take;

                            setCof();

                            cdb.insert(cof);
                            cdb.close();
                            Intent intent2 = new Intent();
                            setResult(RESULT_OK,intent2);
                            supportFinishAfterTransition();
                            return true;


                        default:

                            et = (EditText) findViewById(R.id.new_name);
                            shops = sadapter.getShops();
                            pays = et.getText().toString();
                            qt = cof.getStock();

                            adb.open();
                            adb.setShops(shops);
                            adb.update(av);
                            adb.close();

                            cdb.open();
                            try {
                                Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(img));
                                img=saveToInternalStorage(bm,"img_"+idk);
                            } catch (FileNotFoundException e){
                                Log.e("FNFE","fichier non trouvé");

                            } catch (java.io.IOException e){
                                Log.e("IOE", "onActivityResult: IOException");

                            } catch (SecurityException e){
                                Log.e("SE", "onActivityResult: Security Exception");


                            }
                            if(actual_take!=0){
                                Toast.makeText(this,"Vous avez ajouté "+actual_take+" dosettes à "+pays,Toast.LENGTH_LONG).show();
                            }
                            qt+=actual_take;

                            setCof();
                            cdb.update(cof);
                            cdb.close();
                            intent2 = new Intent();
                            intent2.putExtra("kof", cof);
                            setResult(RESULT_OK,intent2);
                            finish();
                            return true;

                    }
                }
                return super.onOptionsItemSelected(item);

            case R.id.menu_modif_edit:
                Intent intent3 = new Intent(ModifyActivity.this,ModifyActivity.class);
                intent3.putExtra(ListActivity.MODCODE,3);
                intent3.putExtra("id",idk);
                intent3.putExtra("rc", 33);
                Pair<View, String> p1 = Pair.create(findViewById(R.id.imgcard), "img_card");
                Pair<View, String> p2 = Pair.create(findViewById(R.id.shop_rl), "shoplist");
                Pair<View, String> p4 = Pair.create(findViewById(R.id.kStock), "quantity");
                Pair<View, String> p3 = Pair.create(findViewById(R.id.numStock), "quantity_num");
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(this, p1, p2, p3, p4);
                startActivityForResult(intent3,33,options.toBundle());
                return true;
            case R.id.menu_supp:
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

                final View popupView = inflater.inflate(R.layout.popup_delete_coffee, null);
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                ImageView modify = findViewById(R.id.kImage);
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                popupWindow.setAnimationStyle(R.style.anim_popup);
                popupWindow.showAtLocation(modify, Gravity.CENTER, 0, 0);
                View container = (View) popupWindow.getContentView().getParent();
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
                p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                p.dimAmount = 0.3f;
                wm.updateViewLayout(container, p);
                Button bt= popupView.findViewById(R.id.ok_del);
                Button cancel = popupView.findViewById(R.id.cancel);
                final TextView alrtTitle = popupView.findViewById(R.id.frame_title);
                final TextView alert = popupView.findViewById(R.id.alert);
                alrtTitle.setText("Supprimer "+pays+" ?");
                alert.setText("Vous êtes sur le point de supprimer le café "+pays+"\nVoulez-vous continuer ?");
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
                bt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                            deleteFromInternalStorage(img);
                            cdb.open();
                            cdb.delete(idk);
                            cdb.close();
                            adb.open();
                            adb.delete(idk);
                            adb.close();
                            finish();
                    }
                });
                return true;
            case R.id.option_tri:
                Toast.makeText(this, "Soon", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //----------------------------------------------------------------------------------------------

    public boolean isEmpty(int id){
        EditText et = (EditText) findViewById(id);
        String s = et.getText().toString();
        if(s.matches("")){
            return true;
        }
        return false;
    }
    //----------------------------------------------------------------------------------------------



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    String s = data.getStringExtra("type");
                    Uri imgUri = data.getData();
                    getContentResolver().takePersistableUriPermission(imgUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    //String temp = imgUri.toString();
                    //Uri uristring = Uri.parse(stringuri);
                    cdb.open();
                    int x = cdb.getlastID() + 1;
                    Log.e("ouep", "id : " + x);
                    try {
                        Bitmap slctImg = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
                        img = saveToInternalStorage(slctImg, "img_" + idk);
                    } catch (FileNotFoundException e) {
                        Log.e("FNFE", "fichier non trouvé");

                    } catch (IOException e) {
                        Log.e("IOE", "onActivityResult: IOException");

                    } catch (SecurityException e) {
                        Log.e("SE", "onActivityResult: Security Exception");


                    }
                    //TextView txt = findViewById(R.id.new_pays);
                    cdb.close();
                    setImg(img);
                    //txt.setText(stringuri);

                    break;
                }
            case PICKFILE_RESULT_CODE2:
                String s = data.getStringExtra("type");
                Uri imgUri = data.getData();
                getContentResolver().takePersistableUriPermission(imgUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                String temp = imgUri.toString();
                Uri uristring = Uri.parse(temp);
                try {
                    Bitmap slctImg = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uristring);
                    temp = saveToInternalStorage(slctImg, "shop_" + pos);
                } catch (FileNotFoundException e) {
                    Log.e("FNFE", "fichier non trouvé");

                } catch (IOException e) {
                    Log.e("IOE", "onActivityResult: IOException");

                } catch (SecurityException e) {
                    Log.e("SE", "onActivityResult: Security Exception");


                }
                //TextView txt = findViewById(R.id.new_pays);

                sadapter.setShopImg(temp, pos);
                Shop shp = sadapter.getShop(pos);
                sdb.open();
                sdb.update(shp);
                sdb.close();
                break;
            case 33:

                if (resultCode == RESULT_OK) {
                    Coffee kof = (Coffee) data.getSerializableExtra("kof");
                    img = kof.getkImage();
                    pays = kof.getPays();
                    qt = kof.getStock();
                    configureToolbar();
                    TextView num_stock = findViewById(R.id.numStock);
                    num_stock.setText(Integer.toString(qt));
                    setStock(qt);
                    TextView nameEdit = findViewById(R.id.new_name);
                    shopv = findViewById(R.id.new_shop);
                    adb.open();
                    av = adb.findByID(idk);
                    adb.close();
                    sdb.open();
                    shops = sdb.selectAll();
                    sdb.close();
                    LinkedList<Shop> shopTemp = new LinkedList<>();
                    for (int i = 0; i < shops.size(); i++) {
                        if (av.getShops().get(i)) {
                            shopTemp.add(shops.get(i));
                        }
                    }
                    shops = shopTemp;
                    LookAdapter ladapter = new LookAdapter(this, shops, av, pays, code);
                    shopv.setAdapter(ladapter);
                    setImg(img);
                    nameEdit.setText(pays);
                    modif = true;
                    break;
                } else if (resultCode == RESULT_CANCELED) {
                    modif = false;
                    break;
                }

        }
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void onBackPressed() {


        //Toast.makeText(this, code, Toast.LENGTH_SHORT).show();
        /*if(code==0) {
            setResult(RESULT_CANCELED);
            finish();
        }else */if(code==1){
            Intent intent = new Intent();
            intent.putExtra("kof",cof);
            intent.putExtra("qt",actual_take);
            setResult(RESULT_OK,intent);
            finish();
        }else if(code==2){
            if(modif) {
                supportFinishAfterTransition();
                int npos = getIntent().getIntExtra("position",-1);
                Intent intent = new Intent();
                intent.putExtra("idk", idk);
                intent.putExtra("position",npos);
                setResult(RESULT_OK, intent);
                finish();
            }else{
                setResult(RESULT_CANCELED);
            }
        }else if(code==3){
            setResult(RESULT_CANCELED);
        }
        super.onBackPressed();

    }
    //----------------------------------------------------------------------------------------------

    public void setStock(int quant){
        ImageView img = (ImageView) findViewById(R.id.kStock);
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
    }

    public void setImg(String imag) {
        ImageView kimg = (ImageView) findViewById(R.id.kImage);

        Bitmap slctImg = getBitmap(imag);
        //Bitmap cprss = compress(slctImg);
        //kimg.setImageBitmap(slctImg);
        if(slctImg!=null){
            MultiTransformation<Bitmap> multi = new MultiTransformation<Bitmap>(
                    new BlurTransformation(25) /*,
                    new GrayscaleTransformation()*/);
            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    /*.override(100,100)*/;
            Glide.with(ModifyActivity.this)
                    .load(slctImg)
                    .centerCrop()
                    .transform(multi)
                    .skipMemoryCache(true)
                    .apply(requestOptions)
                    .into((ImageView) findViewById(R.id.kBack));
            Glide.with(ModifyActivity.this)
                    .load(slctImg)
                    .skipMemoryCache(true)
                    .apply(requestOptions)
                    .into((ImageView) findViewById(R.id.kImage));
        }else{
            Bitmap unav = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.image_unavailable);

            RequestOptions requestOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            Glide.with(ModifyActivity.this)
                    .load(unav)
                    .skipMemoryCache(true)
                    .apply(requestOptions)
                    .into((ImageView) findViewById(R.id.kImage));
        }

    }

    public Bitmap getBitmap(String path) {
        Bitmap bitmap=null;
        try {
            File f= new File(path);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, options);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            try{
                Uri uristring = Uri.parse(path);
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uristring);
                img=saveToInternalStorage(bitmap,"img_"+idk);
                return getBitmap(img);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return bitmap ;
    }


    public void setCof(){
        cof.setQuant(qt);
        cof.setkID(idk);
        cof.setkImage(img);
        cof.setPays(pays);
    }

    public Bitmap compress(Bitmap img){

        int width = img.getWidth();
        int height = img.getHeight();

        float ratio = (float)width/(float)height;
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) 200) / width;
        float scaleHeight = ((float) 200)/ratio / height;
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        return resizedBitmap;
    }

    public String correct(String s){
        s=s.replace(" ","_");
        s=s.replace(",","");
        s=s.replace("'","");
        s=s.replace("-","_");
        return s;
    }

    public Available AvEmpty(){
        sdb.open();
        int s = (int) sdb.shopSize();
        sdb.close();
        LinkedList<Boolean> bool = new LinkedList<>();
        for(int z=0;z<s;z++){
            bool.add(false);
        }
        return new Available(0,bool);
    }
//-----------------------------------------------------------------------------------------------

    private String saveToInternalStorage(Bitmap bitmapImage,String name){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,name+".jpg");

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
        Log.e("path",mypath.getAbsolutePath());
        return mypath.getAbsolutePath();
    }

    private void deleteFromInternalStorage(String image){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory,image);
        if(mypath.exists()){
            mypath.delete();
        }
    }
}
