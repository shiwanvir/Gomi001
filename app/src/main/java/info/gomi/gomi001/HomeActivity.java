package info.gomi.gomi001;

import android.content.Intent;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.Instant;

import static android.util.Log.*;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private CardView postAD,searchAd,dumpWaste,truckPath;
    private DrawerLayout d1;
    private ActionBarDrawerToggle abdt;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()==null){
            finish();
            //when user not logeed
            startActivity(new Intent(this,MainActivity.class));

        }
        FirebaseUser user=firebaseAuth.getCurrentUser();
        //drawer layout
        d1=(DrawerLayout)findViewById(R.id.d1);
        abdt=new ActionBarDrawerToggle(this,d1,R.string.Open,R.string.Close);
        abdt.setDrawerIndicatorEnabled(true);
        d1.addDrawerListener(abdt);
        abdt.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView nav_view=(NavigationView) findViewById(R.id.nav_view);
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Intent i;
                int id=item.getItemId();
                if(id==R.id.myAds){
                    Toast.makeText(HomeActivity.this,"My Ads",Toast.LENGTH_SHORT).show();
                    stratMyProfile();
                }
                else if(id==R.id.settings){
                    Toast.makeText(HomeActivity.this,"Settings",Toast.LENGTH_SHORT).show();

                }
                else if (id==R.id.contactUs){
                    Toast.makeText(HomeActivity.this,"Contact US",Toast.LENGTH_SHORT).show();

                }
                else if(id==R.id.logOut){

                    logOut();
                }



                return true;
            }
        });

        //defining cards
        postAD=(CardView) findViewById(R.id.post_add);
        searchAd=(CardView)findViewById(R.id.search_ad);
        dumpWaste=(CardView)findViewById(R.id.dump_waste);
        truckPath=(CardView)findViewById(R.id.truck_path);
        //setting onclick listers to the cardviews
        postAD.setOnClickListener(this);
        searchAd.setOnClickListener(this);
        dumpWaste.setOnClickListener(this);
        truckPath.setOnClickListener(this);
    }

    private void logOut() {
        Toast.makeText(HomeActivity.this,"Login out.....",Toast.LENGTH_SHORT).show();
        firebaseAuth.signOut();
        finish();
        startActivity(new Intent(this,MainActivity.class));
    }

    private void stratMyProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
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
}
