package com.example.msg_b.checkmate;

//import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.SkuDetails;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.example.msg_b.checkmate.R;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class BillingActivity extends AppCompatActivity implements BillingProcessor.IBillingHandler {

    BillingProcessor mBillingProcessor;
    Button btn_heart20,btn_heart50, btn_heart120;
    ActionBar actionBar;
    private final String id_heart20 = "com.example.msg_b.checkmate.heart20";
    private final String id_heart50 = "com.example.msg_b.checkmate.heart50";
    private final String id_heart120 = "com.example.msg_b.checkmate.heart120";
    private final int ID_HEART = 999;

    private int purchaseUnit = 0; // 임시코드


    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("menuT", "onCreateOptionsMenu");
        MenuItem item = menu.add(Menu.NONE, ID_HEART, 0, "♥ " + HomeActivity.heart); // 임시코드
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM|MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return super.onCreateOptionsMenu(menu);
    }

    void updateMenuTitle(int addedHeart) {
        Log.d("menuT", "updateMenuTitle");
        HomeActivity.heart += addedHeart; // 임시코드
        invalidateOptionsMenu();
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        androidx.appcompat.app.ActionBar ab = getSupportActionBar();
        ab.setTitle("스토어");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);
        actionBar = getSupportActionBar();

        btn_heart20 = findViewById(R.id.Btn_heart20);
        btn_heart50 = findViewById(R.id.Btn_heart50);
        btn_heart120 = findViewById(R.id.Btn_heart120);

        btn_heart20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseUnit = 20; // 임시코드
                purchaseProduct(id_heart20);
            }
        });
        btn_heart50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseUnit = 50; // 임시코드
                purchaseProduct(id_heart50);
            }
        });
        btn_heart120.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseUnit = 120; // 임시코드
                purchaseProduct(id_heart120);
            }
        });


        initInApp();
//        updateMenuTitle(12);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mBillingProcessor.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
        Log.e("inappT", "onActivityResult");
    }


    // 인앱 결제 준비가 완료되었을 때 호출된다.
    @Override
    public void onBillingInitialized() {
        Log.e("inappT", "onBillingInitialized");




    }


    @Override
    public void onProductPurchased(String productId, TransactionDetails details) {
        Log.e("inappT", "onProductPurchased : " + productId);

//        updateMenuTitle(purchaseUnit); // 임시코드
        Toast.makeText(this, "하트 "+purchaseUnit+"개가 충전되었습니다.", Toast.LENGTH_SHORT).show();

        SkuDetails skuDetails = mBillingProcessor.getPurchaseListingDetails(productId);
        if(productId.equals(id_heart20)) {
            Log.e("inappT", "heart20");
            Toast.makeText(this, "하트 20개 구매", Toast.LENGTH_SHORT).show();
            updateMenuTitle(20);
        }
        else if(productId.equals(id_heart50)){
            Log.e("inappT", "heart50");
            Toast.makeText(this, "하트 50개 구매", Toast.LENGTH_SHORT).show();
            updateMenuTitle(50);
        }
        else if(productId.equals(id_heart120)) {
            Log.e("inappT", "heart120");
            Toast.makeText(this, "하트 120개 구매", Toast.LENGTH_SHORT).show();
            updateMenuTitle(120);
        }

    }

    @Override
    public void onPurchaseHistoryRestored() {
        Log.e("inappT", "onPurchasedHistoryRestored");
    }

    @Override
    public void onBillingError(int errorCode, Throwable error) {
        Log.e("inappT", "onBillingError");
    }


    private void initInApp() {
        mBillingProcessor = new BillingProcessor(this, getString(R.string.GOOGLE_PLAY_CONSOLE_KEY), this);
        mBillingProcessor.initialize();
    }



    private void purchaseProduct(final String productId) {
        final String PRODUCT_ID = productId;

        if(mBillingProcessor.isPurchased(PRODUCT_ID)) {
            // 구매하였으면 소비하여 없앤 후 다시 구매하게 한다.
            mBillingProcessor.consumePurchase(PRODUCT_ID);
            Log.e("inappT", "isPurchased");
        }

        mBillingProcessor.purchase(this, PRODUCT_ID);
    }


}
