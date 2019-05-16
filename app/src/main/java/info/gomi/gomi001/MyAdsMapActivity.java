package info.gomi.gomi001;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
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

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class MyAdsMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, RoutingListener {

    private GoogleMap mMap;
    String userId, adId, latitide, longtide,adStatus,buyerId,notificationKey,productName;
    Double sellerLatitude, sellerLongtide;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private LatLng sellerLocation;
    private LatLng initBuyerLocation;
    LocationRequest mLocationRequest;
    Button cancelBooking;
    private List<Polyline> polylines;
    DatabaseReference notificationRef;
    Dialog AdcencelationDialog;
    ImageView closePopupCancelAd;
    TextView messageTv,titleTv;
    Button buttonAccept;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ads_map);


        polylines = new ArrayList<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        cancelBooking=(Button)findViewById(R.id.cancel_booking);
        mapFragment.getMapAsync(this);
        userId = getIntent().getStringExtra("userId");
        adId = getIntent().getStringExtra("adId");
        latitide = getIntent().getStringExtra("latitude");
        longtide = getIntent().getStringExtra("longitude");
        buyerId=getIntent().getStringExtra("buyerId");
        productName=getIntent().getStringExtra("itemName");

        //get user ad posted location not the current location for the tracking
        sellerLatitude = Double.parseDouble(latitide);
        sellerLongtide = Double.parseDouble(longtide);
        sellerLocation=new LatLng(sellerLatitude,sellerLongtide);

        //toListen ad Status
        DatabaseReference adStatusLister= FirebaseDatabase.getInstance().getReference("post_ad_details").child(adId).child("adStatus");
        adStatusLister.addValueEventListener(new ValueEventListener() {

              @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                   String cahngedStatus=dataSnapshot.getValue(String.class);
                    if(cahngedStatus.equals("available")){

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
        getBuyerLoacation();
        cancelBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    //cancel booking ad
                final FirebaseDatabase database=FirebaseDatabase.getInstance();
                DatabaseReference  ref= database.getReference();
                DatabaseReference cancelAdbooking=ref.child("post_ad_details").child(adId );
                cancelAdbooking.child("buyerId").setValue("null");
                cancelAdbooking.child("adStatus").setValue("available");
                mBuyerMaker.remove();
                //remove buyer id from buyers available
               // Task<Void> buyerLocationRef=FirebaseDatabase.getInstance().getReference().child("buyersAvailable").child(buyerId).removeValue();

                //Log.i(" buyer Id", "buyer Id" + buyerId);
                //get notification key of seller
                DatabaseReference  notiref= FirebaseDatabase.getInstance().getReference();
                DatabaseReference senNotificationToseller=notiref.child("Users").child("Customers").child(buyerId).child("notificationKey");
                senNotificationToseller.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        notificationKey=dataSnapshot.getValue(String.class);
                        //Log.i(" notification Key", "notification Key" + notificationKey);
                        new SendAdNotification("your ad booking canceled by the Owner",productName,notificationKey);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //Intent intent = getIntent();
               // finish();
                //startActivity(intent);

                //Intent intent=new Intent(v.getContext(),MyAdsActivity.class);
                //startActivity(intent);
            }
        });
        //getRouterToMaker(initBuyerLocation);
        //Log.i("Int loc", "int lat" +initBuyerLocation.latitude);
        //Log.i("Int loc", "int lat" +initBuyerLocation.longitude);
    }

    public void ShowPopUp() {


        //popUp
       AdcencelationDialog=new Dialog(this);


        AdcencelationDialog.setContentView(R.layout.pop_up_ad_cancellation);
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

                finish();
                //startActivity(intent);

                Intent intent=new Intent(v.getContext(),MyAdsActivity.class);
                startActivity(intent);

            }
        });





    }


    public Context getappContext(){
        return this;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sellerLocation =new LatLng(sellerLatitude,sellerLongtide);
        mMap.addMarker(new MarkerOptions().position(sellerLocation).title("My Location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_action_seller_location1)));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        buildGooleApiClient();
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sellerLocation));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        //getRouterToMaker(initBuyerLocation);
    }

    private  void errorMessage(){
        Toasty.error(this, "Please Fill Details Correctly", Toast.LENGTH_LONG).show();
    }
    protected  synchronized  void buildGooleApiClient(){

        mGoogleApiClient =new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }



    private void getRouterToMaker(LatLng initBuyerLocation) {

        Routing routing = new Routing.Builder()
                .key("AIzaSyBjdmsEtYZ0s2l1FN-sKFeeXK2FL_8KOdk")
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(sellerLatitude,sellerLongtide),initBuyerLocation)
                .build();
        routing.execute();

    }

    private Marker mBuyerMaker;
    private void getBuyerLoacation() {
        //String buyierId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        //get ad booked buyer id by the ad details
        //get db reference form the db
        DatabaseReference buyerLocationRef=FirebaseDatabase.getInstance().getReference().child("buyersAvailable").child(buyerId).child("l");

        buyerLocationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    List<Object> map=(List<Object>) dataSnapshot.getValue();
                    double locationLat=0;
                    double locationLong=0;

                    if(map.get(0)!=null){
                        locationLat=Double.parseDouble(map.get(0).toString());

                    }

                    if(map.get(1)!=null){
                        locationLong=Double.parseDouble(map.get(1).toString());

                    }

                    LatLng buyerLatLng=new LatLng(locationLat,locationLong);
                    initBuyerLocation=buyerLatLng;
                    if(mBuyerMaker!=null){

                        mBuyerMaker.remove();
                    }
                    mBuyerMaker=mMap.addMarker(new MarkerOptions().position(buyerLatLng).title("Buyer").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_action_buyer)));
                    getRouterToMaker(buyerLatLng);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private static final int[] COLORS = new int[]{android.R.color.holo_red_dark};
    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(15 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingCancelled() {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        //to get the buyier time to time changed location
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
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

    private void erasePolyline(){

        for(Polyline line:polylines){

            line.remove();
        }
        polylines.clear();

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation=location;
        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

    }

    @Override
    public void onBackPressed() {
        // do something
        super.onBackPressed();



    }
}
