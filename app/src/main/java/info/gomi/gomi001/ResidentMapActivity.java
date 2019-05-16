package info.gomi.gomi001;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class ResidentMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private Button mdumpWaste;
    private LatLng pickUpLocation;
    public Marker mWasteMaker;
        //popUp
    Dialog AdcencelationDialog ;
    ImageView closePopupCancelAd;
    TextView messageTv,titleTv;
    Button buttonAccept;
    //schedule task
    private Handler mHandler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident_map);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mdumpWaste=(Button)findViewById(R.id.dump_waste);
        // Initialize a new instance of Handler


        mdumpWaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref=FirebaseDatabase.getInstance().getReference("dumpWaste");
                DatabaseReference refMywaste=FirebaseDatabase.getInstance().getReference("dumpWaste").child(userId);

                GeoFire geoFire=new GeoFire(ref);
                geoFire.setLocation(userId,new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()));
                pickUpLocation=new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                 mWasteMaker=mMap.addMarker(new MarkerOptions().position(pickUpLocation).title("Dump here").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_waste_bag)));
                mdumpWaste.setText("Waste Dumped..!");
                mdumpWaste.setClickable(false);
                Toasty.success(ResidentMapActivity.this, "your waste have dumped..! ", Toast.LENGTH_SHORT).show();
                getColosetDriver();
                refMywaste.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            //ShowPopUp();

                            if(!((Activity)getappContext()).isFinishing())
                            {
                                ShowPopUp();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                //check sheduled task
                //mHandler.postDelayed(mDumpedWasteAroundRunnable,5000);
                //mDumpedWasteAroundRunnable.run();

            }
        });



    }

    //for shedule tasks
    private Runnable mDumpedWasteAroundRunnable=new Runnable() {
        @Override
        public void run() {
            //Toasty.success(ResidentMapActivity.this, "im repeaatingggggggg! ", Toast.LENGTH_SHORT).show();

            getDumpedwasteAround();
            mHandler.postDelayed(this,5000);
        }
    };


    private int radius=100;
    private Boolean driverFound=false;
    private  String driverFoundId;
    private void getColosetDriver(){
    DatabaseReference diverLocation= FirebaseDatabase.getInstance().getReference().child("diversAvailable");
    GeoFire geoFire=new GeoFire(diverLocation);
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(pickUpLocation.latitude,pickUpLocation.longitude),radius);

            geoQuery.removeAllListeners();
            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    if(!driverFound){
                        Log.i(" hureeee", "I have founded a driver" );
                        driverFound=true;
                        driverFoundId=key;
                        getDriverLocation();
                    }


                }

                @Override
                public void onKeyExited(String key) {


                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {

                }

                @Override
                public void onGeoQueryReady() {
                    if(!driverFound){

                        radius++;
                        getColosetDriver();

                    }

                }

                @Override
                public void onGeoQueryError(DatabaseError error) {

                }
            });
    }
private Marker mDriverMaker;
    private void getDriverLocation() {

    DatabaseReference TruckDriverLocationRef=FirebaseDatabase.getInstance().getReference().child("diversAvailable").child(driverFoundId).child("l");
    TruckDriverLocationRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){

                List<Object> map=(List<Object>)dataSnapshot.getValue();
                double locationLat=0;
                double locationLang=0;
                if(map.get(0)!=null){
                    locationLat=Double.parseDouble(map.get(0).toString());


                }

                if(map.get(1)!=null){
                    locationLang=Double.parseDouble(map.get(1).toString());


                }

               LatLng driVerLatLong=new LatLng(locationLat,locationLang);
                if(mDriverMaker!=null){
                    mDriverMaker.remove();

                }
                //driVerLatLong
                mDriverMaker=mMap.addMarker(new MarkerOptions().position(driVerLatLong).title("Truck Driver").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_truck_image)));
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

    }

    public void ShowPopUp() {
        //popUp
        AdcencelationDialog=new Dialog(this);
        AdcencelationDialog.setContentView(R.layout.pop_up_dump_waste_pick);
        closePopupCancelAd=(ImageView)AdcencelationDialog.findViewById(R.id.closePopupCancelAd);
        buttonAccept=(Button)AdcencelationDialog.findViewById(R.id.btnAccept);
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



        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdcencelationDialog.dismiss();
                //AdcencelationDialog=null;

                Intent intent = getIntent();
                finish();
                startActivity(intent);

            }
        });

    }

    public Context getappContext(){
        return this;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);

    }
    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient=new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation=location;
        LatLng latLng= new LatLng(location.getLatitude(),location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        getDumpedwasteAround();
        getAllTruckdrivers();
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);



    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        super.onStop();

    }




    List<Marker> markerList =new ArrayList<Marker>();
    private void getDumpedwasteAround(){

        final DatabaseReference wateLocation=FirebaseDatabase.getInstance().getReference().child("dumpWaste");
        GeoFire geoFire =new GeoFire(wateLocation);
        GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()),1000);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {

            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //getDriverLocation();
                for(Marker markerIncrment: markerList){

                    if(markerIncrment.getTag().equals(key)){

                        return;

                    }
                }
                LatLng wateLocatrion =new LatLng(location.latitude,location.longitude);
                Marker mWasteMaker=mMap.addMarker(new MarkerOptions().position(wateLocatrion).title(key).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_waste_bag)));
                mWasteMaker.setTag(key);

                markerList.add(mWasteMaker);

            }

            @Override
            public void onKeyExited(String key) {
                for(Marker markerIncrment:markerList){
                    if(markerIncrment.getTag().equals(key)){
                        markerList.remove(markerIncrment);
                        //markerIncrment.remove();
                        return;

                    }
                }


            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for(Marker markerIncrment:markerList) {
                    if (markerIncrment.getTag().equals(key)) {
                        markerIncrment.setPosition(new LatLng(location.latitude,location.longitude));
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }

    List<Marker> DriversMarkerList =new ArrayList<Marker>();
    private void getAllTruckdrivers(){

        final DatabaseReference wateLocation=FirebaseDatabase.getInstance().getReference().child("diversAvailable");
        GeoFire geoFire =new GeoFire(wateLocation);
        GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()),1000);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {

            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                //getDriverLocation();
                for(Marker markerIncrment: DriversMarkerList){

                    if(markerIncrment.getTag().equals(key)){

                        return;

                    }
                }
                LatLng driVerLatLong =new LatLng(location.latitude,location.longitude);
                Marker mWasteMaker=mMap.addMarker(new MarkerOptions().position(driVerLatLong).title("Truck Driver").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_truck_image)));
                mWasteMaker.setTag(key);

                DriversMarkerList.add(mWasteMaker);

            }

            @Override
            public void onKeyExited(String key) {
                for(Marker markerIncrment:DriversMarkerList){
                    if(markerIncrment.getTag().equals(key)){
                        DriversMarkerList.remove(markerIncrment);
                        return;

                    }
                }

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                for(Marker markerIncrment:DriversMarkerList) {
                    if (markerIncrment.getTag().equals(key)) {
                        markerIncrment.setPosition(new LatLng(location.latitude,location.longitude));
                    }
                }
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
}
