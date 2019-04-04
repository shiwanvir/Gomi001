package info.gomi.gomi001;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class SearchAdDetailsActivity extends AppCompatActivity {

    TextView mItemTypeTv,mItemNameTv,mUserNameTv,mPriceTv,mPhoneNoTv,mAdStatusTv;
    ImageView mImageViewIv;
    String userId,adId,latitide,longtide,adStatus;
    Button seeSellerLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ad_details);
        //action bar

        ActionBar actionbar =getSupportActionBar();
        actionbar.setTitle("Advertisement Details");
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowCustomEnabled(true);
        //Initialize views
        mItemTypeTv=  findViewById(R.id.sadTypeOfitem);
        mItemNameTv=findViewById(R.id.sadItemName);
        mUserNameTv=findViewById(R.id.sadUserName);
        mPriceTv=findViewById(R.id.sadPrice);
        mPhoneNoTv=findViewById(R.id.sadPhoneNo);
        mImageViewIv=findViewById(R.id.sadImageview);
        seeSellerLocation=findViewById(R.id.see_location);
        mAdStatusTv=findViewById(R.id.sadAdStatus);


        //get  data from Intent
        byte[] bytes=getIntent().getByteArrayExtra("image");
        String itemType=getIntent().getStringExtra("itemType");
        String itemName=getIntent().getStringExtra("itemName");
        String userName=getIntent().getStringExtra("userName");
        String price=getIntent().getStringExtra("price");
        String phoneNo=getIntent().getStringExtra("phoneNo");
        String adStatus=getIntent().getStringExtra("adStatus");
        userId=getIntent().getStringExtra("userId");
        adId=getIntent().getStringExtra("adId");
        latitide=getIntent().getStringExtra("latitude");
        longtide=getIntent().getStringExtra("longitude");
        //adStatus=getIntent().getStringExtra("adStatus");

        Bitmap bmp= BitmapFactory.decodeByteArray(bytes,0,bytes.length);


      /*  Log.i(" User Id", "User ID" + userId);
        Log.i(" AD Id", "AD Id" + adId);
        Log.i(" Seller Latitude", "Seller Latitude" + latitide);
        Log.i(" Seller Longitude", "Seller Longitude" + longtide);*/
        Log.i("Ad Status", "Ad Status" + adStatus);
        //setdata to Views
        mItemTypeTv.setText(itemType);
        mItemNameTv.setText(itemName);
        mUserNameTv.setText(userName);
        mPriceTv.setText(price);
        mPhoneNoTv.setText(phoneNo);
        mAdStatusTv.setText(adStatus);
        mImageViewIv.setImageBitmap(bmp);


        seeSellerLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(),BuyierMapActivity.class);
                //ByteArrayOutputStream stream=new ByteArrayOutputStream();
                intent.putExtra("userId",userId);
                intent.putExtra("adId",adId);
                intent.putExtra("latitude",latitide);
                intent.putExtra("longitude",longtide);
                //intent.putExtra("adStatus",adStatus);
                startActivity(intent);
            }
        });
    }

//go to previous activity


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return true;
    }
}
