package info.gomi.gomi001;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import info.gomi.gomi001.SellerMapActivity;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class PostAdActivity extends AppCompatActivity implements View.OnClickListener {
    Button from_camera;
    Button from_gllary;
    Button seller_location;
    Button details_save;
    ImageView image;
    String pathToFile;
    Uri imageUri;
    EditText username;
    EditText phoneNo;
    EditText itemName;
    Spinner itemType;
    EditText price;
    //EditText ad_image;
    EditText loc_latitude;
    EditText loc_longitude;
    private Class<SellerMapActivity> sellerMapActivityClass;
    DatabaseReference saveDeatils;
    FirebaseStorage storage;
    Location mLastLocation;
    StorageReference storageReference;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ad);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //Firebase stoage int
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        //LatLng wateLocatrion =new LatLng(location.latitude,location.longitude);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    loc_latitude.setText(Double.toString(location.getLatitude()));
                    loc_longitude.setText(Double.toString(location.getLongitude()));
                }
            }
        });
       //loc_latitude.setText(Double.toString(mLastLocation.getLatitude()));
       //loc_longitude.setText(Double.toString(mLastLocation.getLongitude()));
        saveDeatils= FirebaseDatabase.getInstance().getReference("post_ad_details");
        from_camera=findViewById(R.id.from_camera);
        from_gllary=findViewById(R.id.from_gallary);
        seller_location=(Button) findViewById(R.id.see_location);
        details_save=(Button) findViewById(R.id.save_details);
        username=(EditText) findViewById(R.id.your_name);
        phoneNo=(EditText)findViewById(R.id.phone_no);
        itemName=(EditText)findViewById(R.id.item_name);
        itemType=(Spinner)findViewById(R.id.item_type);
        price=(EditText)findViewById(R.id.price);
        loc_longitude=(EditText)findViewById(R.id.loc_longitude);
        loc_latitude=(EditText)findViewById(R.id.loc_latitude);
        seller_location.setOnClickListener(this);
        details_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDetails();
            }


        });
        loc_latitude=(EditText)findViewById(R.id.loc_latitude);
        loc_longitude=(EditText)findViewById(R.id.loc_longitude);
        Spinner itemType=(Spinner) findViewById(R.id.item_type);
        ArrayAdapter<String> myadapter=new ArrayAdapter<String>(PostAdActivity.this,

                android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.names));
            myadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            itemType.setAdapter(myadapter);


        if(Build.VERSION.SDK_INT>=23){
            requestPermissions(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},2);

        }

        from_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 dispatchPictureTakerAction();
            }
        });

        from_gllary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });
        image=findViewById(R.id.iamge);


    }

    private void addDetails() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
      final String userId=user.getUid();
      String userName=username.getText().toString().trim();
      final String addId=saveDeatils.push().getKey();
      String phoneNO=phoneNo.getText().toString().trim();
      String item=itemName.getText().toString().trim();
      String itemtype=itemType.getSelectedItem().toString();
      String itemprice=price.getText().toString().trim();
      String imagepath=pathToFile;
      String longitude=loc_longitude.getText().toString().trim();
      String latitude=loc_latitude.getText().toString().trim();
      String addStatus="available";
      String buyerId="null";
      String search=itemtype.toLowerCase();
      PostAdDetails adDetails=new PostAdDetails(userId,addId,userName,phoneNO,item,itemtype,itemprice,imagepath,latitude,longitude,addStatus,search,buyerId);

        saveDeatils.child(addId).setValue(adDetails);
        if(imageUri!=null){
               /* final ProgressDialog progressDialog=new ProgressDialog(this);
            StorageReference ref=storageReference.child("images/"+ addId);
            ref.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(PostAdActivity.this,"Ad. posted Sucessfully..",Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PostAdActivity.this,"Ad. posted faild..",Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("upload.."+(int)progress+"%");
                        }
                    });*/

            final StorageReference filepath=FirebaseStorage.getInstance().getReference().child("images").child(addId);
            Bitmap bitmap=null;
            try {
                bitmap =MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,20,baos);
            byte[] data=baos.toByteArray();
            UploadTask uploadTask=filepath.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("adImageUrl", uri.toString());
                            saveDeatils.child(addId).updateChildren(newImage);

                            //Toast.makeText(this,"Ad posted Sucessfully....",Toast.LENGTH_LONG).show();
                            //finish();
                            //return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //finish();
                            //return;
                        }
                    });
                }
            });

        }

        //finish();
        Toast.makeText(this,"Ad posted Sucessfully....",Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){

            if(requestCode==1){
                Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);
                image.setImageBitmap(bitmap);


            }
        }
    }

    private void dispatchPictureTakerAction() {
        Intent takePic=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePic.resolveActivity(getPackageManager() )!=null){
            File photoFile=null;
            photoFile=createPhotoFile();
            if(photoFile!=null ) {
                 pathToFile = photoFile.getAbsolutePath();
                Uri photoURI= FileProvider.getUriForFile(PostAdActivity.this,"info.gomi.gomi001.fileprovider",photoFile);
                imageUri=photoURI;
                takePic.putExtra(MediaStore.EXTRA_OUTPUT,photoURI);
                startActivityForResult(takePic,1);


            }

        }

    }

    private File createPhotoFile() {
        String name=new SimpleDateFormat("yyyymmdd_HHmmss").format(new Date());
        File storageDir=getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image=null;
        try {
            image=File.createTempFile(name,".jpg",storageDir);
        } catch (IOException e) {
            Log.d("Mylog","Excep"+e.toString());
        }
        return image;
    }


    @Override
    public void onClick(View v) {
        startActivity(new Intent(getApplicationContext(),SellerMapActivity.class));


    }
}
