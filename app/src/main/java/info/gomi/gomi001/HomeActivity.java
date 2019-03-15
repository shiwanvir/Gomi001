package info.gomi.gomi001;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;

import java.time.Instant;

import static android.util.Log.*;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    private CardView postAD,searchAd,dumpWaste,truckPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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

    @Override
    public void onClick(View v) {
        Intent i;
           switch (v.getId()){
            case R.id.post_add:i= new Intent(this,PostAdActivity.class);startActivity(i);break;
            case R.id.truck_path:i= new Intent(this,DriverMapActivity.class);startActivity(i);break;
               case R.id.dump_waste:i= new Intent(this,ResidentMapActivity.class);startActivity(i);break;

            default:break;


        }


    }
}
