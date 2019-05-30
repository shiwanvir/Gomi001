package info.gomi.gomi001;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import static info.gomi.gomi001.SearchAdActivity.GetAdDetailsViewHolder.*;

public class SearchAdActivity extends AppCompatActivity {
    LinearLayoutManager mLayoutManager;//for sorting purposes
    SharedPreferences mSharedPref;//for saving sort settings
    RecyclerView mRecylaRecyclerView;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_ad);

        //action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Advertisement List");


        mSharedPref=getSharedPreferences("SortSettings",MODE_PRIVATE);
        String mSorting=mSharedPref.getString("Sort","newest");// default newst adds will showing first

            if(mSorting.equals("newest")){
                mLayoutManager=new LinearLayoutManager(this);
                //load newest items first
                mLayoutManager.setReverseLayout(true);
                mLayoutManager.setStackFromEnd(true);

            }

            else if(mSorting.equals("oldest")){

                mLayoutManager=new LinearLayoutManager(this);
                //load oldest items first
                mLayoutManager.setReverseLayout(false);
                mLayoutManager.setStackFromEnd(false);
            }



        mRecylaRecyclerView = findViewById(R.id.serachReayclerView);
        mRecylaRecyclerView.setHasFixedSize(true);

        mRecylaRecyclerView.setLayoutManager(mLayoutManager);

        mRef  = FirebaseDatabase.getInstance().getReference("post_ad_details");
        //mRef = mFirebaseDatabase.


    }

    private void firebaseSearch(String searchText){
        String query=searchText.toLowerCase();
        Query firebaseSearchQuery =mRef.orderByChild("search").startAt(query).endAt(query+"\uf8ff");

        FirebaseRecyclerOptions<Model> options=
                new FirebaseRecyclerOptions.Builder<Model>()
                        .setQuery(firebaseSearchQuery,Model.class)
                        .build();
        FirebaseRecyclerAdapter<Model,GetAdDetailsViewHolder> adpter=
                new FirebaseRecyclerAdapter<Model, GetAdDetailsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull GetAdDetailsViewHolder holder, int position, @NonNull Model model)
                    {
                        holder.mItemTypeView.setText(model.getItemType());
                        holder.mItemNameView.setText(model.getItemName());
                        holder.mUserNameView.setText(model.getUserName());
                        holder.mpriceView.setText(model.getPrice());
                        holder.mPhoneNo.setText(model.getPhoneNo());
                        holder.mUserId.setText(model.getUserId());
                        holder.mAdId.setText(model.getAdId());
                        holder.mLatitude.setText(model.getLatitude());
                        holder.mlongitude.setText(model.getLogitude());
                        holder.mBuyerId.setText(model.getBuyerId());
                        holder.mAdStatus.setText(model.getAdStatus());
                        Picasso.get().load(model.getAdImageUrl()).into(holder.mImageView);


                    }

                    @NonNull
                    @Override
                    public GetAdDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row,viewGroup,false);
                        GetAdDetailsViewHolder viewHolder=new GetAdDetailsViewHolder(view);
                        viewHolder.SetOnclickListner(new ClickListner() {
                            @Override
                            public void onItemClick(View view, int position) {
                                TextView mItemTypeTv=view.findViewById(R.id.rTypeOfitem);
                                TextView mItemNametv=findViewById(R.id.ritemName);
                                TextView mUserNameTv=findViewById(R.id.rUserName);
                                TextView mpriceTv=findViewById(R.id.rPrice);
                                TextView mPhoneNOTv=findViewById(R.id.rPhoneNo);
                                ImageView mimageViewIv=findViewById(R.id.rImageview);
                                TextView mUserIdTv=findViewById(R.id.rUserId);
                                TextView mAdIdTv=findViewById(R.id.rAdId);
                                TextView mLatitudeTv=findViewById(R.id.rLatitude);
                                TextView mLongitudeTV=findViewById(R.id.rlongitude);
                                TextView mBuyerTv=findViewById(R.id.rbuyerId);
                                TextView mAdStatusTv=findViewById(R.id.rAdStatus);


                                String mItemType=mItemTypeTv.getText().toString();
                                String mItemName=mItemNametv.getText().toString();
                                String mUserName=mUserNameTv.getText().toString();
                                String mPrice=mpriceTv.getText().toString();
                                String mPhoneNo=mPhoneNOTv.getText().toString();
                                Drawable mDrawable=mimageViewIv.getDrawable();
                                Bitmap mBitmap=((BitmapDrawable)mDrawable).getBitmap();
                                String mUserId=mUserIdTv.getText().toString();
                                String mAdId=mAdIdTv.getText().toString();
                                String mLatitude=mLatitudeTv.getText().toString();
                                String mLongitude=mLongitudeTV.getText().toString();
                                String mBuyerId=mBuyerTv.getText().toString();
                                String mAdStatus=mAdStatusTv.getText().toString();


                                //phrase data to new activity

                                Intent intent =new Intent(view.getContext(),SearchAdDetailsActivity.class);
                                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                                mBitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                                byte[] bytes=stream.toByteArray();
                                intent.putExtra("image",bytes);//put image as a bit map
                                intent.putExtra("itemType",mItemType);
                                intent.putExtra("itemName",mItemName);
                                intent.putExtra("userName",mUserName);
                                intent.putExtra("price",mPrice);
                                intent.putExtra("phoneNo",mPhoneNo);
                                intent.putExtra("userId",mUserId);
                                intent.putExtra("adId",mAdId);
                                intent.putExtra("latitude",mLatitude);
                                intent.putExtra("longitude",mLongitude);
                                intent.putExtra("buyerId",mBuyerId);
                                intent.putExtra("adStatus",mAdStatus);
                                finish();
                                startActivity(intent);
                            }

                            @Override
                            public void onItemLongclick(View view, int position) {

                            }
                        });


                        return viewHolder;
                    }
                };
        mRecylaRecyclerView.setAdapter(adpter);
        adpter.startListening();


    }



    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Model> options=
                new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(mRef,Model.class)
                .build();
        FirebaseRecyclerAdapter<Model,GetAdDetailsViewHolder> adpter=
                new FirebaseRecyclerAdapter<Model, GetAdDetailsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull GetAdDetailsViewHolder holder, int position, @NonNull Model model)
                    {
                        holder.mItemTypeView.setText(model.getItemType());
                        holder.mItemNameView.setText(model.getItemName());
                        holder.mUserNameView.setText(model.getUserName());
                        holder.mpriceView.setText(model.getPrice());
                        holder.mPhoneNo.setText(model.getPhoneNo());
                        holder.mUserId.setText(model.getUserId());
                        holder.mAdId.setText(model.getAdId());
                        holder.mLatitude.setText(model.getLatitude());
                        holder.mlongitude.setText(model.getLogitude());
                        holder.mBuyerId.setText(model.getBuyerId());
                        holder.mAdStatus.setText(model.getAdStatus());
                        Picasso.get().load(model.getAdImageUrl()).into(holder.mImageView);


                    }

                    @NonNull
                    @Override
                    public GetAdDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row,viewGroup,false);
                        GetAdDetailsViewHolder viewHolder=new GetAdDetailsViewHolder(view);
                        viewHolder.SetOnclickListner(new ClickListner() {
                            @Override
                            public void onItemClick(View view, int position) {
                                TextView mItemTypeTv=view.findViewById(R.id.rTypeOfitem);
                                TextView mItemNametv=findViewById(R.id.ritemName);
                                TextView mUserNameTv=findViewById(R.id.rUserName);
                                TextView mpriceTv=findViewById(R.id.rPrice);
                                TextView mPhoneNOTv=findViewById(R.id.rPhoneNo);
                                ImageView mimageViewIv=findViewById(R.id.rImageview);
                                TextView mUserIdTv=findViewById(R.id.rUserId);
                                TextView mAdIdTv=findViewById(R.id.rAdId);
                                TextView mLatitudeTv=findViewById(R.id.rLatitude);
                                TextView mLongitudeTV=findViewById(R.id.rlongitude);
                                TextView mBuyerTv=findViewById(R.id.rbuyerId);
                                TextView mAdStatusTv=findViewById(R.id.rAdStatus);

                                String mItemType=mItemTypeTv.getText().toString();
                                String mItemName=mItemNametv.getText().toString();
                                String mUserName=mUserNameTv.getText().toString();
                                String mPrice=mpriceTv.getText().toString();
                                String mPhoneNo=mPhoneNOTv.getText().toString();
                                Drawable mDrawable=mimageViewIv.getDrawable();
                                Bitmap mBitmap=((BitmapDrawable)mDrawable).getBitmap();
                                String mUserId=mUserIdTv.getText().toString();
                                String mAdId=mAdIdTv.getText().toString();
                                String mLatitude=mLatitudeTv.getText().toString();
                                String mLongitude=mLongitudeTV.getText().toString();
                                String mBuyerId=mBuyerTv.getText().toString();
                                String mAdStatus=mAdStatusTv.getText().toString();

                                //phrase data to new activity

                                Intent intent =new Intent(view.getContext(),SearchAdDetailsActivity.class);
                                ByteArrayOutputStream stream=new ByteArrayOutputStream();
                                mBitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                                byte[] bytes=stream.toByteArray();
                                intent.putExtra("image",bytes);//put image as a bit map
                                intent.putExtra("itemType",mItemType);
                                intent.putExtra("itemName",mItemName);
                                intent.putExtra("userName",mUserName);
                                intent.putExtra("price",mPrice);
                                intent.putExtra("phoneNo",mPhoneNo);
                                intent.putExtra("userId",mUserId);
                                intent.putExtra("adId",mAdId);
                                intent.putExtra("latitude",mLatitude);
                                intent.putExtra("longitude",mLongitude);
                                intent.putExtra("buyerId",mBuyerId);
                                intent.putExtra("adStatus",mAdStatus);
                                //Log.i("lati","latitide"+mLatitude);
                                //Log.i("longi","longitude"+mLatitude);
                                finish();
                                startActivity(intent);
                            }

                            @Override
                            public void onItemLongclick(View view, int position) {

                            }
                        });


                        return viewHolder;
                    }
                };
        mRecylaRecyclerView.setAdapter(adpter);
        adpter.startListening();
    }

    public static  class  GetAdDetailsViewHolder extends  RecyclerView.ViewHolder {

       TextView mItemTypeView, mItemNameView,mUserNameView, mpriceView, mPhoneNo,mUserId,mAdId,mLatitude,mlongitude,mBuyerId,mAdStatus;
       ImageView mImageView;
        public GetAdDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemTypeView=itemView.findViewById(R.id.rTypeOfitem);
            mItemNameView=itemView.findViewById(R.id.ritemName);
            mUserNameView=itemView.findViewById(R.id.rUserName);
            mpriceView=itemView.findViewById(R.id.rPrice);
            mPhoneNo=itemView.findViewById(R.id.rPhoneNo);
            mImageView=itemView.findViewById(R.id.rImageview);
            mUserId=itemView.findViewById(R.id.rUserId);
            mAdId=itemView.findViewById(R.id.rAdId);
            mLatitude=itemView.findViewById(R.id.rLatitude);
            mlongitude=itemView.findViewById(R.id.rlongitude);
            mBuyerId=itemView.findViewById(R.id.rbuyerId);
            mAdStatus=itemView.findViewById(R.id.rAdStatus);


            //item Click
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mClickListner.onItemClick(view, getAdapterPosition());
                }
            });
            //item long click
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                   mClickListner.onItemLongclick(view,getAdapterPosition());
                    return true;
                }
            });

        }
    private  ClickListner mClickListner;


        //interface to sendCallbacks

        public  interface ClickListner{

        void onItemClick(View view,int position);
        void onItemLongclick(View view,int position);


        }

        public  void SetOnclickListner(ClickListner clickListner){
            mClickListner=clickListner;

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem item=menu.findItem(R.id.action_serach);
        SearchView searchView=(SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                firebaseSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //filtering against the text
                firebaseSearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        //handle other action bar item clicks here


        if(id==R.id.action_sort){
            //show dialog
            showSortDialog();

            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showSortDialog() {

        String[] sortOptions={"Newest","Oldest"};
        // alert dialog creation

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Sort by")
                .setIcon(R.drawable.ic_action_sort)
                .setItems(sortOptions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 0 indicates newst post 1indicates oldest
                        if(which==0){
                            //sort by newst

                            SharedPreferences.Editor editor=mSharedPref.edit();
                            editor.putString("Sort","newest");
                            editor.apply();
                            recreate();

                        }
                        else if(which==1){
                            //sort by oldest

                            SharedPreferences.Editor editor=mSharedPref.edit();
                            editor.putString("Sort","oldest");
                            editor.apply();
                            recreate();

                        }
                    }
                });
            builder.show();




    }
}
