package info.gomi.gomi001;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;

public class MyAdsDetailsActivity extends AppCompatActivity {
    TextView mItemTypeTv,mItemNameTv,mUserNameTv,mPriceTv,mPhoneNoTv,mAdStatusTv;
    ImageView mImageViewIv;
    String userId,adId,latitide,longtide,adStatus,buyerId,productName;
    Button seeSellerLocation,deleteAd;
    //popUp
    Dialog AdcencelationDialog ;
    ImageView closePopupCancelAd;
    TextView messageTv,titleTv;
    Button buttonAccept,btnCancel;
    boolean confirmation=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads_details);

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
        deleteAd=findViewById(R.id.deleteAd);


        //get  data from Intent
        byte[] bytes=getIntent().getByteArrayExtra("image");
        String itemType=getIntent().getStringExtra("itemType");
        String itemName=getIntent().getStringExtra("itemName");
        String userName=getIntent().getStringExtra("userName");
        String price=getIntent().getStringExtra("price");
        String phoneNo=getIntent().getStringExtra("phoneNo");
        final String adStatus=getIntent().getStringExtra("adStatus");
        userId=getIntent().getStringExtra("userId");
        adId=getIntent().getStringExtra("adId");
        latitide=getIntent().getStringExtra("latitude");
        longtide=getIntent().getStringExtra("longitude");
        buyerId=getIntent().getStringExtra("buyerId");
        //adStatus=getIntent().getStringExtra("adStatus");
        productName=getIntent().getStringExtra("itemName");

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
                if(adStatus.equals("available")){
                    Toasty.error(MyAdsDetailsActivity.this, "This add Have'nt a Buyer yet.. ", Toast.LENGTH_SHORT).show();
                    //Toasty.error()
                }
                else {
                    Intent intent = new Intent(view.getContext(), MyAdsMapActivity.class);
                    //ByteArrayOutputStream stream=new ByteArrayOutputStream();
                    intent.putExtra("userId", userId);
                    intent.putExtra("adId", adId);
                    intent.putExtra("latitude", latitide);
                    intent.putExtra("longitude", longtide);
                    intent.putExtra("buyerId", buyerId);
                    //intent.putExtra("adStatus",adStatus);
                    intent.putExtra("itemName",productName);
                    finish();
                    startActivity(intent);
                }
            }
        });

        deleteAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!buyerId.equals("null") ) {
                    Toasty.warning(MyAdsDetailsActivity.this, "You should cancel the booking first ", Toast.LENGTH_SHORT).show();
                    //return;
                }
                else if(!((Activity)getappContext()).isFinishing())
                    {
                        ShowPopUp();
                    }






            }
        });


    }



    public void ShowPopUp() {
        //popUp
        AdcencelationDialog=new Dialog(this);
        AdcencelationDialog.setContentView(R.layout.pop_up_delete_ad_confermation);
        closePopupCancelAd=(ImageView)AdcencelationDialog.findViewById(R.id.closePopupCancelAd);
        buttonAccept=(Button)AdcencelationDialog.findViewById(R.id.btnAccept);
        btnCancel=(Button)AdcencelationDialog.findViewById(R.id.btnCancel);
        titleTv=(TextView)AdcencelationDialog.findViewById(R.id.titleTv);
        messageTv=(TextView)AdcencelationDialog.findViewById(R.id.messageTv);
        AdcencelationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        AdcencelationDialog.show();

        closePopupCancelAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdcencelationDialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdcencelationDialog.dismiss();
            }
        });

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdcencelationDialog.dismiss();
                //AdcencelationDialog=null;

                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference();
                DatabaseReference deleteAd = ref.child("post_ad_details").child(adId);
                deleteAd.removeValue();
                finish();
                Intent intent = new Intent(v.getContext(), MyAdsActivity.class);
                startActivity(intent);

            }
        });

    }

    public Context getappContext(){
        return this;
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();

        return true;
    }
}
