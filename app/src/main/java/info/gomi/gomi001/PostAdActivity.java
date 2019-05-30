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
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
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

import es.dmoral.toasty.Toasty;
import info.gomi.gomi001.SellerMapActivity;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class PostAdActivity extends AppCompatActivity implements View.OnClickListener {
    Button from_camera;
    Button from_gllary;
    Button seller_location;
    Button details_save;
    Button cancelDetails;
    ImageView image;
    String pathToFile;
    Uri imageUri;
    TextInputLayout username;
    TextInputLayout phoneNo;
    TextInputLayout itemName;
    TextInputLayout itemTypeValidation;
    TextInputLayout imageValidation;
    Spinner itemType;
    TextInputLayout price;
    //EditText ad_image;
    EditText loc_latitude;
    EditText loc_longitude;
    private Class<SellerMapActivity> sellerMapActivityClass;
    DatabaseReference saveDeatils;
    FirebaseStorage storage;
    Location mLastLocation;
    StorageReference storageReference;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int PICK_IMAGE=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ad);
        //action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Post Advertisement");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //Firebase stoage int
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        //LatLng wateLocatrion =new LatLng(location.latitude,location.longitude);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

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
        cancelDetails=(Button)findViewById(R.id.cancel_details);
        username=findViewById(R.id.your_name);
        phoneNo=findViewById(R.id.phone_no);
        itemName=findViewById(R.id.item_name);
        itemType=(Spinner)findViewById(R.id.item_type);
        price=findViewById(R.id.price);
        loc_longitude=(EditText)findViewById(R.id.loc_longitude);
        loc_latitude=(EditText)findViewById(R.id.loc_latitude);
        itemTypeValidation=findViewById(R.id.item_Type_validation);
        imageValidation=findViewById(R.id.image_validation);
        seller_location.setOnClickListener(this);
        details_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(confrimationInputs()==true) {
                    addDetails();
                }
                else{
                    errorMessage();
                }
            }


        });
        loc_latitude=(EditText)findViewById(R.id.loc_latitude);
        loc_longitude=(EditText)findViewById(R.id.loc_longitude);
        Spinner itemType=(Spinner) findViewById(R.id.item_type);
        ArrayAdapter<String> myadapter=new ArrayAdapter<String>(PostAdActivity.this,
                R.layout.spinner_item,getResources().getStringArray(R.array.names));
            myadapter.setDropDownViewResource(R.layout.spinner_item);
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
               // Intent intent=new Intent(Intent.ACTION_PICK);
                //intent.setType("image/*");
                //startActivityForResult(intent,1);
                openGallary();
            }
        });
        image=findViewById(R.id.iamge);

        cancelDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

    }
    private  void errorMessage(){
        Toasty.error(this, "Please Fill Details Correctly", Toast.LENGTH_LONG).show();
    }

    private boolean validateUserName( ){
        String userName=username.getEditText().getText().toString().trim();
        if(userName.isEmpty()){
            username.setError("Filed cant't be empty ");
            return false;
        }
        else if(userName.length()>20){
            username.setError("Username too long");
            return false;
        }
        else if(!userName.matches("[a-zA-Z ]+")){
            username.setError("Incorrect Name");
            return false;
        }
        else{
            username.setError(null);
            username.setErrorEnabled(false);
            return true;
        }


    }
    private boolean validatePhoneNo(){
        String phoneno=phoneNo.getEditText().getText().toString().trim();

        if(phoneno.isEmpty()){
            phoneNo.setError("Filed cant't be empty ");
            return false;
        }
        else if(phoneno.length()>10){
            phoneNo.setError("Incorrect phone Number ");
            return false;
        }
        else if(!(android.util.Patterns.PHONE.matcher(phoneno).matches())){
            phoneNo.setError("phone Number contain characters");
            return false;
        }
        else{
            phoneNo.setError(null);
            phoneNo.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateItemName(){

        String item=itemName.getEditText().getText().toString().trim();
        if(item.isEmpty()){
            itemName.setError("Filed cant't be empty ");
            return false;
        }
        else if(item.length()>20){
            itemName.setError("Item Name too long");
            return false;
        }
        else if(!item.matches("[a-zA-Z ]+")){
            itemName.setError("Incorrect Item Name");
            return false;
        }
        else{
            itemName.setError(null);
            itemName.setErrorEnabled(false);
            return true;
        }

    }

    private boolean validateItemPrice(){
        String itemprice=price.getEditText().getText().toString().trim();

        if(itemprice.isEmpty()){
            price.setError("Filed cant't be empty ");
            return false;
        }
        else if(itemprice.length()>15){
            price.setError("Incorrect Item Price");
            return false;
        }
        else if(itemprice.matches("[a-zA-Z ]+")){
            price.setError("Item Price contain characters");
            return false;
        }
        else{
            price.setError(null);
            price.setErrorEnabled(false);
            return true;
        }
    }

   private boolean itemTypeValidation(){
        String itemtypetext=itemType.getSelectedItem().toString();

        if(itemtypetext.equals("[Select One]")){
            //Log.i(" notification Key", "now at hereeee") ;
            itemTypeValidation.setError("please select item Type");

            return false;
        }
        itemTypeValidation.setError(null);
        itemTypeValidation.setErrorEnabled(false);
        return true;


    }
    private boolean imageValidation(){
        if(imageUri==null){
            imageValidation.setError("Please Select a image");
            return false;
        }
        imageValidation.setError(null);
        imageValidation.setErrorEnabled(false);
        return true;
    }




    public boolean confrimationInputs(){
        if(!validateUserName()|!validatePhoneNo()|!validateItemName()|!validateItemPrice()|!itemTypeValidation()|!imageValidation()){
            return false;
        }
        return true;
    }

    private void openGallary() {
        Intent gallery=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PICK_IMAGE);
    }

    private void addDetails() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
      final String userId=user.getUid();
      String userName=username.getEditText().getText().toString().trim();
      final String addId=saveDeatils.push().getKey();
      String phoneNO=phoneNo.getEditText().getText().toString().trim();
      String item=itemName.getEditText().getText().toString().trim();
      String itemtype=itemType.getSelectedItem().toString();
      String itemprice=price.getEditText().getText().toString().trim();
      String imagepath=pathToFile;
      String longitude=loc_longitude.getText().toString().trim();
      String latitude=loc_latitude.getText().toString().trim();
      String addStatus="available";
      String buyerId="null";
      String search=itemtype.toLowerCase();
      PostAdDetails adDetails=
              new PostAdDetails(userId,addId,userName,phoneNO,item,itemtype,itemprice,imagepath,latitude,longitude,addStatus,search,buyerId);

        saveDeatils.child(addId).setValue(adDetails);
        if(imageUri!=null){


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

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                        }
                    });
                }
            });

        }

        //finish();
        Toasty.success(this, "Ad posted Sucessfully....", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){

            if(requestCode==1){
                Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);
                image.setImageBitmap(bitmap);


            }
            else if (requestCode==PICK_IMAGE){
            imageUri=data.getData();
            image.setImageURI(imageUri);

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
                Uri photoURI= FileProvider.
                        getUriForFile(PostAdActivity.this,"info.gomi.gomi001.fileprovider",photoFile);
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
