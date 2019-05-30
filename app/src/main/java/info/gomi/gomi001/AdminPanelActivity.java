package info.gomi.gomi001;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class AdminPanelActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        //action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Admin Panel");

        BottomNavigationView bottomNav=findViewById(R.id.bottm_nav);
        bottomNav.setOnNavigationItemSelectedListener(bottomNavLister);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavLister=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    Intent i;
                    int id=menuItem.getItemId();
                    if(id==R.id.nav_home){
                        //Toast.makeText(HomeActivity.this,"My Ads",Toast.LENGTH_SHORT).show();
                        stratHome();
                    }
                    else if(id==R.id.nav_mails){
                        //Toast.makeText(HomeActivity.this,"Settings",Toast.LENGTH_SHORT).show();
                        //stratAdminpanel();
                        stratMessages();
                    }
                    else if(id==R.id.nav_userCreation){
                        stratUserCreation();

                    }
                    else if (id==R.id.nav_map){
                        //Toast.makeText(HomeActivity.this,"Contact US",Toast.LENGTH_SHORT).show();
                        //StratContactUsActivity();
                        stratAdminMap();
                    }
                    return true;
                }

            };



    private void stratHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void stratMessages() {
        Intent intent = new Intent(this, MailReadActivity.class);
        startActivity(intent);
    }


    private void stratAdminMap() {
        Intent intent = new Intent(this, AdminMapActivity.class);
        startActivity(intent);
    }

    private void stratUserCreation() {
        Intent intent = new Intent(this, UserCreartionActivity.class);
        startActivity(intent);
    }
}
