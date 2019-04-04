package info.gomi.gomi001;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class BuyierMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, RoutingListener {

    private GoogleMap mMap;
    String userId, adId, latitide, longtide,adStatus;
    Double sellerLatitude, sellerLongtide;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private LatLng sellerLocation;
    LocationRequest mLocationRequest;
    Button buyerStartJourny;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyier_map);
        polylines = new ArrayList<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        userId = getIntent().getStringExtra("userId");
        adId = getIntent().getStringExtra("adId");
        latitide = getIntent().getStringExtra("latitude");
        longtide = getIntent().getStringExtra("longitude");
        //adStatus=getIntent().getStringExtra("adStatus");
        sellerLatitude = Double.parseDouble(latitide);
        sellerLongtide = Double.parseDouble(longtide);
        buyerStartJourny=findViewById(R.id.start_buyer_journey);
       sellerLocation=new LatLng(sellerLatitude,sellerLongtide);
        
        buyerStartJourny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String buyierId= FirebaseAuth.getInstance().getCurrentUser().getUid();
                getRouterToMaker(sellerLocation);
                getBuyerLoacation();

                //for savibg for sellers
                 final FirebaseDatabase database=FirebaseDatabase.getInstance();
                    DatabaseReference  ref= database.getReference();
                    DatabaseReference refForSeller=ref.child("post_ad_details").child(adId );
                    refForSeller.child("buyerId").setValue(buyierId);
                    refForSeller.child("adStatus").setValue("Booked");
                    //refForSeller.setValue(true);
                //final String bookedaddId=refForSeller.push().getKey();
                //BookedAdDetails bookedAdDetails=new BookedAdDetails(userId,adId,buyierId);
                //refForSeller.child(bookedaddId).setValue(bookedAdDetails);
            }
        });

    }


    private void getRouterToMaker(LatLng sellerLocation) {

        Routing routing = new Routing.Builder()
                .key("AIzaSyBjdmsEtYZ0s2l1FN-sKFeeXK2FL_8KOdk")
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),sellerLocation)
                .build();
        routing.execute();

    }

    private Marker mBuyerMaker;
    private void getBuyerLoacation() {
        String buyierId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference buyerLocationRef=FirebaseDatabase.getInstance().getReference().child("buyersAvailable").child(buyierId).child("l");

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
                            if(mBuyerMaker!=null){

                                mBuyerMaker.remove();
                            }
                            mBuyerMaker=mMap.addMarker(new MarkerOptions().position(buyerLatLng).title("Buyer").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_action_buyer)));
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sellerLocation = new LatLng(sellerLatitude, sellerLongtide);
        mMap.addMarker(new MarkerOptions().position(sellerLocation).title("Seller Location").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_action_seller_location1)));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        buildGooleApiClient();
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sellerLocation));
    }

    protected  synchronized  void buildGooleApiClient(){

        mGoogleApiClient =new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

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

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation=location;
        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
           //for saving data in db for buyres
        String buyierId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("buyersAvailable");
        GeoFire geoFire=new GeoFire(ref);
        geoFire.setLocation(buyierId,new GeoLocation(location.getLatitude(),location.getLongitude()));

    }

    @Override
    protected void onStop() {
        super.onStop();
        String buyierId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("buyersAvailable");
        GeoFire geoFire=new GeoFire(ref);
        geoFire.removeLocation(buyierId);

            }
    private List<Polyline> polylines;
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
    private void erasePolyline(){

        for(Polyline line:polylines){

            line.remove();
        }
        polylines.clear();

    }

    @Override
    public void onRoutingCancelled() {

    }
}
