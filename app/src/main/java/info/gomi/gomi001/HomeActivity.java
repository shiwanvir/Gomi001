package info.gomi.gomi001;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import java.time.Instant;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.util.Log.*;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private CardView postAD,searchAd,dumpWaste,truckPath,admin;
    private DrawerLayout d1;
    private ActionBarDrawerToggle abdt;
    private FirebaseAuth firebaseAuth;
    private CircleImageView navProfileImage;
    private TextView navUserName;
    DatabaseReference saveDetails;
    private Uri resultUri;
    private String profileImageUrl;
    private NavigationView nav_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            //when user not logeed
            startActivity(new Intent(this, MainActivity.class));

        }
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        saveDetails = FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(userId);
        //drawer layout
        d1 = (DrawerLayout) findViewById(R.id.d1);
        abdt = new ActionBarDrawerToggle(this, d1, R.string.Open, R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);
        d1.addDrawerListener(abdt);
        abdt.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nav_view = (NavigationView) findViewById(R.id.nav_view);

        View headerView = nav_view.getHeaderView(0);
        navProfileImage = (CircleImageView) headerView.findViewById(R.id.navProfileImage);
        navUserName = (TextView) headerView.findViewById(R.id.navUserName);
        getUserDetails();

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent i;
                int id = item.getItemId();
                if (id == R.id.myAds) {
                    //Toast.makeText(HomeActivity.this,"My Ads",Toast.LENGTH_SHORT).show();
                    StratMyAds();
                } else if (id == R.id.settings) {
                    //Toast.makeText(HomeActivity.this,"Settings",Toast.LENGTH_SHORT).show();
                    stratAdminpanel();
                } else if (id == R.id.MyProfile) {
                    stratMyProfile();

                } else if (id == R.id.contactUs) {
                    //Toast.makeText(HomeActivity.this,"Contact US",Toast.LENGTH_SHORT).show();
                    StratContactUsActivity();
                } else if (id == R.id.changePassword) {
                    //Toast.makeText(HomeActivity.this,"Contact US",Toast.LENGTH_SHORT).show();
                    StratChangePassword();
                } else if (id == R.id.logOut) {

                    logOut();
                }


                return true;
            }
        });
        int i = 1;
        //defining cards
        postAD = (CardView) findViewById(R.id.post_add);
        searchAd = (CardView) findViewById(R.id.search_ad);
        dumpWaste = (CardView) findViewById(R.id.dump_waste);
        truckPath = (CardView) findViewById(R.id.truck_path);
        admin = (CardView) findViewById(R.id.admin_page);
        if (i==0){
        admin.setVisibility(View.GONE);
         }
        //setting onclick listers to the cardviews
        postAD.setOnClickListener(this);
        searchAd.setOnClickListener(this);
        dumpWaste.setOnClickListener(this);
        truckPath.setOnClickListener(this);
    }

    private void logOut() {
        Toast.makeText(HomeActivity.this,"Login out.....",Toast.LENGTH_SHORT).show();
        //signing out from oneSignal subscription
        OneSignal.setSubscription(false);
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(this,MainActivity.class));
    }

    private void stratMyProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    private void stratAdminpanel() {
        Intent intent = new Intent(this, AdminPanelActivity.class);
        startActivity(intent);
    }


    private void StratContactUsActivity(){

        Intent intent=new Intent(this,ContatctUSActivity.class);
        startActivity(intent);
    }
    private void StratMyAds(){

        Intent intent = new Intent(this, MyAdsActivity.class);
        startActivity(intent);
    }
    private void StratChangePassword(){

        Intent intent = new Intent(this,ChangePasswordActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return abdt.onOptionsItemSelected(item)|| super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent i;
           switch (v.getId()){
            case R.id.post_add:i= new Intent(this,PostAdActivity.class);startActivity(i);break;
            case R.id.truck_path:i= new Intent(this,DriverMapActivity.class);startActivity(i);break;
               case R.id.dump_waste:i= new Intent(this,ResidentMapActivity.class);startActivity(i);break;
               case R.id.search_ad:i= new Intent(this,SearchAdActivity.class);startActivity(i);break;

            default:break;


        }


    }

    private void getUserDetails(){

        saveDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()&& dataSnapshot.getChildrenCount()>0){

                    Map<String,Object> map=( Map<String,Object>)dataSnapshot.getValue();
                    if(map.get("user_name")!=null){
                        String user_name=map.get("user_name").toString();
                        //userName.setHintAnimationEnabled(false);
                        navUserName.setText(user_name);


                    }

                    if(map.get("profileImageUrl")!=null){
                        profileImageUrl=map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load( profileImageUrl).into(navProfileImage);

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }
}
