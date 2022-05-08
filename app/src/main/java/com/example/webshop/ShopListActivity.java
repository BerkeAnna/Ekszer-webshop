package com.example.webshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ShopListActivity extends AppCompatActivity {
    private static final String LOG_TAG  = ShopListActivity.class.getName();
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    private boolean viewRow = true;
    private FrameLayout redCircle ;
    private TextView contentTextView;
    private int gridNumber=1; //egy oszlopban fognak megjelenni az elemek
    private int cartItems =0;
    private int qLimit = 5;

    private RecyclerView mRecyclerView;
    private ArrayList<ShoppingItem> mItemsData;
    private ShoppingItemAdapter madapter;

    private FirebaseFirestore mFirestore;
    private CollectionReference mItems;

    private NotificationHelper mnotihandler;

    private AlarmManager mAlarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Log.d(LOG_TAG, "Authenticated user");
        }else{
            Log.d(LOG_TAG, "Unauthenticated user");
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mItemsData = new ArrayList<>();

        madapter = new ShoppingItemAdapter(this, mItemsData);

        mRecyclerView.setAdapter(madapter);

        mFirestore = FirebaseFirestore.getInstance();
        mItems = mFirestore.collection("Items");

        mnotihandler = new NotificationHelper(this);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        setAlarmManager();

        queryData_ratedinfo();
        initializeData();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        this.registerReceiver(powerReceiver, filter);

    }

    BroadcastReceiver powerReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action==null) {
                return;
            }

            switch (action){
                case Intent.ACTION_POWER_CONNECTED:
                    qLimit = 5;
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    qLimit=2;
                    break;

            }
            queryData_ratedinfo();
        }
    };

    private void queryData_ratedinfo(){
        mItemsData.clear();

//        mItems.whereEqualTo();
//        keresni vmire -^
        mItems.orderBy("ratedInfo", Query.Direction.DESCENDING).limit(qLimit).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                ShoppingItem item = documentSnapshot.toObject(ShoppingItem.class);
                item.setID(documentSnapshot.getId());
                mItemsData.add(item);
            }

            if(mItemsData.size()==0){
                initializeData();
                queryData_ratedinfo();
            }
            madapter.notifyDataSetChanged();
        });


    }

    public void deleteItem(ShoppingItem item){
        DocumentReference dokref = mItems.document(item._getID());

        dokref.delete().addOnSuccessListener(success -> {
            Log.d(LOG_TAG, "Deleted " + item._getID() + " " + item.getName());

        })
                .addOnFailureListener(failure -> {
                    Toast.makeText(this, "Item " + item._getID() + " cannot be deleted", Toast.LENGTH_LONG).show();
                });
        queryData_price();
    }

    private void queryData_price(){
        mItemsData.clear();

//        mItems.whereEqualTo();
//        keresni vmire -^
        mItems.orderBy("price", Query.Direction.DESCENDING).limit(qLimit).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                ShoppingItem item = documentSnapshot.toObject(ShoppingItem.class);
                mItemsData.add(item);
            }

            if(mItemsData.size()==0){
                initializeData();
                queryData_ratedinfo();
            }
            madapter.notifyDataSetChanged();
        });


    }

    private void initializeData() {
        String[] itemList = getResources().getStringArray(R.array.shopping_item_names);
        String[] itemInfo = getResources().getStringArray(R.array.shopping_item_desc);;
        String[] itemPrice = getResources().getStringArray(R.array.shopping_item_price);;
        TypedArray itemsImageResource = getResources().obtainTypedArray(R.array.shopping_item_images);
        TypedArray itemsRating = getResources().obtainTypedArray(R.array.shopping_item_rates);

        //mItemsData.clear();
        for(int i=0 ; i<itemList.length; i++){
            mItems.add(new ShoppingItem(itemList[i], itemInfo[i], itemPrice[i], itemsRating.getFloat(i,0),
                    itemsImageResource.getResourceId(i, 0), 0));
        }
        itemsImageResource.recycle();
        //madapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.shop_list_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                madapter.getFilter().filter(s);
                return false;
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.log_out_button:
                Log.d(LOG_TAG, "Log out clicked");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
            case R.id.setting_button:
                Log.d(LOG_TAG, "Setting button clicked");
                return true;
            case R.id.cart:
                Log.d(LOG_TAG, "Cart clicked");
                return true;
            case R.id.view_selector:
                Log.d(LOG_TAG, "View selector clicked");

                if(viewRow){
                    changeSpanCount(item, R.drawable.ic_view, 1);
                }else{
                    changeSpanCount(item, R.drawable.view_row, 2);

                }

                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow= !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        final MenuItem alertMenuItem = menu.findItem(R.id.cart);
        FrameLayout rootView = (FrameLayout) alertMenuItem.getActionView();

        redCircle = (FrameLayout) rootView.findViewById(R.id.view_alert_red_circle);
        contentTextView = (TextView) rootView.findViewById(R.id.view_alert_count_textview);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(alertMenuItem);
            }
        });

        return super.onCreateOptionsMenu(menu);

    }
    public void updateAlertIcon(ShoppingItem item){
        cartItems = (cartItems+1);
        if(0 <cartItems){
            contentTextView.setText(String.valueOf(cartItems));
        }else{
            contentTextView.setText("");

        }
        mItems.document(item._getID()).update("count", item.getCount()+1)
                .addOnFailureListener( failure -> {
                    Toast.makeText(this, "Item " + item._getID() + " cannot be updated", Toast.LENGTH_LONG).show();
                });

        mnotihandler.send(item.getName());
        queryData_ratedinfo();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(powerReceiver);
    }

//    private void setAlarmManager() {
//        long repeatInterval = 600;
//        long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;
//
//        Intent intent = new Intent(this, AlarmReceiver.class);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//        mAlarmManager.setInexactRepeating(
//                AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                triggerTime,
//                repeatInterval,
//                pendingIntent
//        );


        //mAlarmManager.cancel(pendingIntent);
    }

