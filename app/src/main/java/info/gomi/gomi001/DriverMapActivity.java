package info.gomi.gomi001;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
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
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.firebase.geofire.LocationCallback;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class DriverMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, RoutingListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    LatLng nearstDumpedLocation;
      float minDistance;
    private  Button pickUpWaste;
    private Button startJourny;
    Location nLocation;
    private FirebaseAuth mAuth;
    String notificationKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth=FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_map);
        //LatLng nearstDumpedLocation=new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        polylines = new ArrayList<>();
        startJourny=(Button) findViewById(R.id.star_journey);
        pickUpWaste=(Button)findViewById(R.id.pick_up_waste);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        pickUpWaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickWaste();
                Intent intent = getIntent();
                finish();
                startActivity(intent);

            }
        });


        startJourny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //finish();
                //startActivity(getIntent());
                //getDumpedwasteAround();
               //print();
                //getDumpedwasteAround();
                getRouterToMaker(nearstDumpedLocation);
            }
        });
    }

    private void PickWaste() {

        DatabaseReference picWaste=FirebaseDatabase.getInstance().getReference().child("dumpWaste");
        GeoFire geoFirePicwaste=new GeoFire(picWaste);
        DatabaseReference pickedWaste=FirebaseDatabase.getInstance().getReference().child("pickedWaste");
        GeoFire geoFirePickedwaste=new GeoFire(pickedWaste);

        geoFirePicwaste.removeLocation(nearestUserId);
        //get notification key of seller
        DatabaseReference  notiref= FirebaseDatabase.getInstance().getReference();
        DatabaseReference senNotificationToseller=notiref.child("Users").child("Customers").child(nearestUserId).child("notificationKey");
        senNotificationToseller.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationKey=dataSnapshot.getValue(String.class);
                //Log.i(" notification Key", "notification Key" + notificationKey);
                //send notification for user
                new SendAdNotification("your waste picked by driver","Dump waste",notificationKey);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        geoFirePickedwaste.setLocation(nearestUserId,new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()));


    }

    private void getRouterToMaker(LatLng nearstDumpedLocation) {
        //Log.i("here im in lines maker:", "line maker latiude" +nearstDumpedLocation.latitude );
        //Log.i("here im in lines maker:", "line maker longitude" +nearstDumpedLocation.longitude );
        Routing routing = new Routing.Builder()
                .key("AIzaSyBjdmsEtYZ0s2l1FN-sKFeeXK2FL_8KOdk")
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),nearstDumpedLocation)
                .build();
        routing.execute();

    }

    void print(){

    Toast.makeText(getApplicationContext(), "nearest distance is:" + minDistance, Toast.LENGTH_SHORT).show();
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

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("diversAvailable");

        GeoFire geoFire=new GeoFire(ref);
        geoFire.setLocation(userId,new GeoLocation(location.getLatitude(),location.getLongitude()));
        getDumpedwasteAround();

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
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("diversAvailable");

        GeoFire geoFire=new GeoFire(ref);
        geoFire.removeLocation(userId);

    }



    public int wastePackages=0;
    public String nearestUserId="";
    List<Marker> markerList =new ArrayList<Marker>();
    private void getDumpedwasteAround(){
        minDistance=1000000000;
        final DatabaseReference wateLocation=FirebaseDatabase.getInstance().getReference().child("dumpWaste");
         GeoFire geoFire =new GeoFire(wateLocation);
        GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude()),1000);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {

            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                getDriverLocation();
                for(Marker markerIncrment: markerList){

                    if(markerIncrment.getTag().equals(key)){

                            return;

                    }
                }
                LatLng wateLocatrion =new LatLng(location.latitude,location.longitude);
                Marker mWasteMaker=mMap.addMarker(new MarkerOptions().position(wateLocatrion).title(key).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_waste_bag)));
                mWasteMaker.setTag(key);



                //get dump point
                Location dumPoint=new Location("");
                dumPoint.setLatitude(wateLocatrion.latitude);
                dumPoint.setLongitude(wateLocatrion.longitude);
                //get driver last location
                Location driverLoc=new Location("");
                driverLoc.setLatitude(mLastLocation.getLatitude());
                driverLoc.setLongitude(mLastLocation.getLongitude());

                float distance=dumPoint.distanceTo(driverLoc);
               if(minDistance>distance){
                    minDistance=distance;
                    nearestUserId=key;
                  // nLocation.setLongitude(wateLocatrion.latitude);
                   //nLocation.setLongitude(wateLocatrion.longitude);
                   nearstDumpedLocation=new LatLng(dumPoint.getLatitude(),dumPoint.getLongitude());
                   //nearstDumpedLocation=dumPoint;
                }
                else{

                    minDistance=minDistance;
                    nearestUserId=nearestUserId;
                   //nearstDumpedLocation.setLongitude(wateLocatrion.latitude);
                   //nearstDumpedLocation.setLongitude(wateLocatrion.longitude);
                   nearstDumpedLocation=new LatLng(nearstDumpedLocation.latitude,nearstDumpedLocation.longitude);
                }
                Log.i("Distance", "Distance" + distance);
                Log.i(" Min Distance", "Min Distance" + minDistance);
                Log.i("Nearest User Id:", "Neasrt User Id:" +nearestUserId);
                Log.i("this", "Number of waste packges" + markerList.size());
                Log.i("waste location:", "latiude" +nearstDumpedLocation.latitude );
                Log.i("waste location:", "longitude" +nearstDumpedLocation.longitude );
                markerList.add(mWasteMaker);

            }

            @Override
            public void onKeyExited(String key) {
                for(Marker markerIncrment:markerList){
                    if(markerIncrment.getTag().equals(key)){
                        markerList.remove(markerIncrment);
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

            Toasty.info(getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingCancelled() {

    }

    private void erasePolyline(){

        for(Polyline line:polylines){

            line.remove();
        }
        polylines.clear();

    }

    private Marker mDriverMaker;
    private void getDriverLocation() {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference TruckDriverLocationRef=FirebaseDatabase.getInstance().getReference().child("diversAvailable").child(userId).child("l");
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
}
