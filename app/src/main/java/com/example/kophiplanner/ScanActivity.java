package com.example.kophiplanner;
import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLDataException;
import java.sql.SQLException;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class ScanActivity extends AppCompatActivity {

    SurfaceView surfaceView;
    TextView txtBarcodeValue;
    BarcodeDetector barcodeDetector;
    FloatingActionButton addBut;
    RelativeLayout rl;
    CoffeeDB db;
    TextView tT;

    ImageButton valid;
    ImageButton l_c;
    ImageButton m_c;
    EditText hmn;

    int final_quant;
    Coffee kof;

    ImageView img;
    TextView name;
    ImageView qtImg;
    TextView qt;

    int actual_take;

    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;

    int code;
    String pays = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.layout_scan);

        initViews();
    }

    private void initViews() {
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);
        surfaceView = findViewById(R.id.surfaceView);
        addBut = findViewById(R.id.add_coffee);
        rl = (RelativeLayout) findViewById(R.id.TakeScan);
        tT = findViewById(R.id.taskTitle);

        valid = findViewById(R.id.ValidCoffee);
        l_c = findViewById(R.id.less_cup);
        m_c = findViewById(R.id.more_cup);
        hmn = findViewById(R.id.Scanned_Take);

        img = findViewById(R.id.CoffeeImg);
        name = findViewById(R.id.CoffeePays);
        qtImg = findViewById(R.id.CoffeeStock);
        qt = findViewById(R.id.CoffeeQt);

        code = getIntent().getIntExtra(MainActivity.MAINCODE,-1);
        MyApp app = (MyApp)getApplication();
        db= new CoffeeDB(ScanActivity.this);
        addBut.setVisibility(View.GONE);
        rl.setVisibility(View.GONE);
        tT.setVisibility(View.GONE);


        actual_take =0;
        valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(code){
                    case 0:
                        final_quant=kof.getStock()+actual_take;
                        kof.setQuant(final_quant);
                        db.open();
                        db.update(kof);
                        db.close();
                        Intent intent = new Intent();
                        intent.putExtra("pays",kof.getPays());
                        intent.putExtra("quant",final_quant);
                        setResult(RESULT_OK,intent);
                        finish();
                        break;
                    case 1:
                        final_quant=kof.getStock()-actual_take;
                        kof.setQuant(final_quant);
                        db.open();
                        db.update(kof);
                        db.close();
                        Intent intent2 = new Intent();
                        intent2.putExtra("pays",kof.getPays());
                        intent2.putExtra("quant",actual_take);
                        Log.e("ISOK",code+" "+final_quant);
                        setResult(RESULT_OK,intent2);
                        Log.e("ISOK",code+" "+final_quant);
                        finish();
                        break;
                    case 4:
                        kof.affiche();
                        Intent intent3 = new Intent();
                        intent3.putExtra("ID",kof.getkID());
                        setResult(12,intent3);
                        finish();
                        break;
                }

            }
        });

        m_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actual_take+1<=999) {
                    actual_take = actual_take + 1;
                    hmn.setText(Integer.toString(actual_take));
                }
            }
        });
        l_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actual_take-1>=0) {
                    actual_take = actual_take - 1;
                    hmn.setText(Integer.toString(actual_take));
                }
            }
        });
        hmn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if(!(s.toString().matches(""))) {
                    actual_take = Integer.parseInt(s.toString());
                    if(code==0){
                        if (actual_take > 999) {
                            actual_take = 999;
                            hmn.setText(Integer.toString(actual_take));
                        } else if (actual_take < 0) {
                            actual_take = 0;
                            hmn.setText(Integer.toString(actual_take));
                        }
                    }else if(code==1) {
                        if (actual_take > final_quant) {
                            actual_take = final_quant;
                            hmn.setText(Integer.toString(actual_take));
                        } else if (actual_take < 0) {
                            actual_take = 0;
                            hmn.setText(Integer.toString(actual_take));
                        }
                    }
                }
            }
        });
    }

    private void initialiseDetectorsAndSources() {

        //Toast.makeText(getApplicationContext(), "Barcode scanner started", Toast.LENGTH_SHORT).show();

        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScanActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(ScanActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                //Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {


                    txtBarcodeValue.post(new Runnable() {

                        @Override
                        public void run() {

                            if (barcodes.valueAt(0) != null) {
                                txtBarcodeValue.removeCallbacks(null);
                                pays = barcodes.valueAt(0).rawValue;
                                pays = pays.replaceAll(" ","_");
                                pays = pays.replaceAll("[^a-zA-Z0-9]", "");
                                txtBarcodeValue.setText(pays);
                                db.open();
                                try {
                                    kof = db.findByPays(pays);
                                    if(kof!=null) {
                                        addBut.setVisibility(View.GONE);
                                        setImg(kof.getkImage());
                                        setStock(kof.getStock());
                                        final_quant = kof.getStock();
                                        name.setText(kof.getPays());
                                        rl.setVisibility(View.VISIBLE);
                                        tT.setVisibility(View.VISIBLE);
                                        switch (code) {
                                            case 0:
                                                tT.setText("Ajout de Stock");

                                                valid.setImageResource(R.drawable.stock_icon);
                                                break;
                                            case 1:
                                                tT.setText("Prendre un café");
                                                valid.setImageResource(R.drawable.ic_local_cafe_black_24dp);
                                                break;
                                            case 4:
                                                tT.setText("Modifier un café");
                                                valid.setImageResource(R.drawable.ic_edit_24px);
                                                l_c.setVisibility(View.GONE);
                                                m_c.setVisibility(View.GONE);
                                                hmn.setVisibility(View.GONE);
                                                break;
                                        }
                                    } else {
                                        rl.setVisibility(View.GONE);
                                        tT.setVisibility(View.GONE);
                                        addBut.setVisibility(View.VISIBLE);
                                    }
                                    db.close();
                                }catch(SQLiteException e){
                                    rl.setVisibility(View.GONE);
                                    tT.setVisibility(View.GONE);
                                    addBut.setVisibility(View.VISIBLE);
                                    db.close();
                                }
                            }
                        }
                    });

                }
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }
    public void setStock(int quant){
        qt.setText(Integer.toString(quant));
        if(quant<=0){
            qtImg.setImageResource(R.drawable.z);
        }else if(quant<=5){
            qtImg.setImageResource(R.drawable.c);
        }else if(quant<=10){
            qtImg.setImageResource(R.drawable.d);
        }else if(quant<=15){
            qtImg.setImageResource(R.drawable.q);
        }else if(quant<=20){
            qtImg.setImageResource(R.drawable.v);
        }else {
            qtImg.setImageResource(R.drawable.vc);
        }
    }

    public void setImg(String imag) {

        Bitmap slctImg = getBitmap(imag);
        //Bitmap cprss = compress(slctImg);
        //kimg.setImageBitmap(slctImg);
        if(slctImg!=null){
            img.setImageBitmap(slctImg);
        }else{
            Bitmap unav = BitmapFactory.decodeResource(this.getResources(),
                    R.drawable.image_unavailable);

            img.setImageBitmap(unav);
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
                String img=saveToInternalStorage(bitmap,"img_"+kof.getkID());
                return getBitmap(img);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return bitmap ;
    }
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

    public void addCoffeeHandler2(View v){
        Log.e("données",pays);
            Intent intent = new Intent(ScanActivity.this, ModifyActivity.class);
            intent.putExtra("code",3);
            intent.putExtra("id",999);
            intent.putExtra("pays",pays);
            startActivity(intent);
            finish();

    }
}