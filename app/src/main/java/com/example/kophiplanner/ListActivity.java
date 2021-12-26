package com.example.kophiplanner;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ListActivity extends AppCompatActivity {

    //----VARIABLES----//

    public static final String EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X";
    public static final String EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y";
    public final static String MODCODE = "code";

    GridRecyclerView recV ;
    CustomAdapter adapter = null;
    ListView listv;
    ListAdapter ladapter;

    MyApp app ;
    CoffeeDB db ;

    RelativeLayout rl;
    View rootLayout;
    FloatingActionButton fab;

    LinkedList<Coffee> coffees = new LinkedList<>();

    LinkedList<Coffee> kof_cup = new LinkedList<>();
    List<Integer> kof_cup_qt = new ArrayList<>();

    int code ;
    boolean qtasc;
    boolean nmasc;
    private int revealX;
    private int revealY;

    //----------------------------------------------------------------------------------------------

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        final Intent intent = getIntent();
        rootLayout = findViewById(R.id.root_layout);
            //-- circular reveal from front page --
        if (savedInstanceState == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) &&
                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {
            rootLayout.setVisibility(View.INVISIBLE);
            revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0);
            revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0);
            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        revealActivity(revealX, revealY);
                        rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
            }
        } else {
            rootLayout.setVisibility(View.VISIBLE);
        }

        app = (MyApp)getApplication();
        code = getIntent().getIntExtra(MainActivity.MAINCODE,3);
        db = new CoffeeDB(this);

        listv = findViewById(R.id.list_cups);
        rl = findViewById(R.id.bottom_cups);

        this.configureToolbar();
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        db.open();
        //db.display();
        if(code==1){
            coffees = db.selectAllNonEmpty();
        }else{
            coffees = db.selectAll();
        }
        db.close();

        // -- gridview config -- //
        adapter = new CustomAdapter(this,coffees);
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this,R.anim.grid_layout_animation_from_bottom);
        recV = findViewById(R.id.Gridview);
        recV.setAdapter(adapter);
        recV.setLayoutManager(new GridLayoutManager(this,3));
        recV.setLayoutAnimation(animation);

        qtasc=true;
        nmasc=true;

        fab = findViewById(R.id.add_coffee);
        fab.hide();

        final SwipeRefreshLayout srl = findViewById(R.id.srl);
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                db.open();
                coffees = db.selectAll();
                db.close();
                adapter = new CustomAdapter(ListActivity.this,coffees);
                recV.setAdapter(adapter);
                srl.setRefreshing(false);
            }
        });
        switch(code){

            /*case 0: //Remettre du stock

                rl.setVisibility(View.GONE);
                break;*/

            case 2: //Liste des cafés

                rl.setVisibility(View.GONE);
                fab.startAnimation(AnimationUtils.loadAnimation(this,R.anim.fab_show));
                fab.show();
                break;

            case 1: //Prendre un café

                rl.setVisibility(View.VISIBLE);
                ladapter = new ListAdapter(this, kof_cup, kof_cup_qt);
                listv.setAdapter(ladapter);
                ImageButton ok = findViewById(R.id.serve);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Coffee cofi;
                        db.open();
                        for (int i=0; i<kof_cup.size();i++){
                            Coffee x = kof_cup.get(i);
                            int z = kof_cup_qt.get(i);
                            cofi = x;
                            int qti = x.getStock()-z;
                            cofi.setQuant(qti);
                            Toast.makeText(ListActivity.this,"Il reste "+qti+" dosettes à "+x.getPays(),Toast.LENGTH_LONG).show();
                            db.update(cofi);
                        }
                        db.close();
                        finish();
                    }
                });
                break;

            case 3: //ERROR ?
                Toast.makeText(this, "ERROR LIST", Toast.LENGTH_SHORT).show();
        }



        recV.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recV ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(ListActivity.this, ModifyActivity.class);
                        //Toast.makeText(ListActivity.this, position+" "+coffees.size(), Toast.LENGTH_SHORT).show();

                        switch(code){
                            case 2: //on veut consulter un café

                                intent.putExtra(MODCODE,2);
                                intent.putExtra("id",coffees.get(position).getkID());
                                intent.putExtra("position",position);
                                startActivityForResult(intent,2);
                                break;

                            case 1: //on veut prendre un café

                                intent.putExtra(MODCODE,1);
                                intent.putExtra("id",coffees.get(position).getkID());
                                startActivityForResult(intent,1);
                                break;

                            /*case 0: //on veut modifier le stock

                                intent.putExtra(MODCODE,0);
                                intent.putExtra("id",coffees.get(position).getkID());
                                startActivityForResult(intent,0);
                                break;*/
                        }
                    }

                    @Override public void onLongItemClick(View view, int position) {
                        Toast.makeText(ListActivity.this, "NOT YET !", Toast.LENGTH_SHORT).show();
                    }
                })
        );
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_activity_list,menu);
        return true;
    }

    //----------------------------------------------------------------------------------------------

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void configureToolbar(){
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        switch(code){
            case 2:
                Objects.requireNonNull(getSupportActionBar()).setTitle("Liste des cafés");
                break;
            case 1:
                Objects.requireNonNull(getSupportActionBar()).setTitle("Prendre un café");
                break;
            /*case 0:
                Objects.requireNonNull(getSupportActionBar()).setTitle("Stocks");
                break;*/
        }

    }

    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.name:

                db.open();
                coffees = db.selectAll("name",nmasc);
                db.close();

                adapter = new CustomAdapter(this,coffees);
                recV.setAdapter(adapter);
                nmasc=!nmasc;
                qtasc=true;

                Toast.makeText(this, "Ordonné par noms, croissant = "+nmasc, Toast.LENGTH_LONG).show();

                return true;

            case R.id.quantity:

                db.open();
                coffees = db.selectAll("quantity",qtasc);
                db.close();

                adapter = new CustomAdapter(this,coffees);
                recV.setAdapter(adapter);
                qtasc=!qtasc;
                nmasc=true;

                Toast.makeText(this, "Ordonné par quantité, croissant = "+qtasc, Toast.LENGTH_LONG).show();

                return true;

            case android.R.id.home:

                fab.startAnimation(AnimationUtils.loadAnimation(this,R.anim.fab_hide));
                unRevealActivity();
                Intent intent = new Intent(ListActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onResume(){
        super.onResume();
        db.open();
        coffees = db.selectAll();
        db.close();
        adapter = new CustomAdapter(this,coffees);
        recV.setAdapter(adapter);

    }

    //----------------------------------------------------------------------------------------------

    public int rechCoff(Coffee kof, LinkedList<Coffee> kofs){
        for(int i=0;i<kofs.size();i++){
            if(kof.coffeeCp(kofs.get(i))){
                return i;
            }
        }
        return -1;
    }


    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        switch (requestCode) {
            case 0: //modifier
            case 2: //consulter

                if (resultCode == RESULT_OK) {
                    int idk = data.getIntExtra("idk", -1);
                    int pos = data.getIntExtra("position",-1);
                    db.open();
                    Coffee kof = db.findByID(idk);
                    db.close();
                    coffees.set(pos, kof);
                    adapter = new CustomAdapter(this, coffees);
                    adapter.notifyDataSetChanged();
                }
                break;

            case 1: //prendre

                if (resultCode == RESULT_OK) {
                    int qti = data.getIntExtra("qt", -1);
                    if(qti>0){
                        Coffee oskur = (Coffee) data.getSerializableExtra("kof");
                        int res=rechCoff(oskur,kof_cup);
                        if (res == -1) {
                            kof_cup.add(oskur);
                            kof_cup_qt.add(qti);
                        }else{
                            kof_cup.set(res,oskur);
                            int qtt= qti+kof_cup_qt.get(res);
                            kof_cup_qt.set(res,qtt);
                        }
                        listv.invalidateViews();

                    }
                }
                break;

            case 3: // ajouter un café

                if (resultCode == RESULT_OK) {
                    db.open();
                    coffees = db.selectAll();
                    db.close();
                    adapter = new CustomAdapter(this,coffees);
                    recV.setAdapter(adapter);
                }
                break;

            case 4: // ajouter un café (scan)

                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent(ListActivity.this, ModifyActivity.class);
                    intent.putExtra(MODCODE, 3);
                    intent.putExtra("id", 999);
                    intent.putExtra("pays", data.getStringExtra("pays"));
                    startActivityForResult(intent, 3);
                }else if(resultCode==12){
                    //Log.e("ISOK","les gars ya un pb la"+data.getIntExtra("ID",-1));
                    Intent intent3 = new Intent(ListActivity.this,ModifyActivity.class);
                    intent3.putExtra(MODCODE,3);
                    intent3.putExtra("id",data.getIntExtra("ID",-1));
                    startActivityForResult(intent3,3);
                }
                break;

        }
    }

    public void addCoffeeHandler(View v){
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("camera",false)){
            Intent intent2 = new Intent(ListActivity.this, ScanActivity.class);
            intent2.putExtra("code",4);
            startActivityForResult(intent2,4);
        }else{
            Intent intent = new Intent(ListActivity.this, ModifyActivity.class);
            intent.putExtra(MODCODE,3);
            intent.putExtra("id",9999);
            startActivityForResult(intent,3);
        }
    }
    public void onBackPressed() {
        fab.startAnimation(AnimationUtils.loadAnimation(this,R.anim.fab_hide));
        fab.hide();
        unRevealActivity();
        super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    protected void revealActivity(int x, int y) { //circular reveal
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);

            // create the animator for this view (the start radius is zero)
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, x, y, 0, finalRadius);
            circularReveal.setDuration(400);
            circularReveal.setInterpolator(new AccelerateInterpolator());

            // make the view visible and start the animation
            rootLayout.setVisibility(View.VISIBLE);
            circularReveal.start();
        } else {
            finish();
        }
    }

    protected void unRevealActivity() { //circular unreveal
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            finish();
        } else {
            float finalRadius = (float) (Math.max(rootLayout.getWidth(), rootLayout.getHeight()) * 1.1);
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(
                    rootLayout, revealX, revealY, finalRadius, 0);

            circularReveal.setDuration(400);
            circularReveal.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    rootLayout.setVisibility(View.INVISIBLE);
                    finish();
                }
            });


            circularReveal.start();
        }
    }

}
