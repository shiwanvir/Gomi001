package info.gomi.gomi001;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import info.gomi.gomi001.SellerMapActivity;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class PostAdActivity extends AppCompatActivity implements View.OnClickListener   {
    Button from_camera;
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
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_ad);
       //Firebase stoage int
       storage= FirebaseStorage.getInstance();
       storageReference=storage.getReference();

        saveDeatils= FirebaseDatabase.getInstance().getReference("post_ad_details");
        from_camera=findViewById(R.id.from_camera);
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
        image=findViewById(R.id.iamge);


    }

    private void addDetails() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
      String userId=user.getUid();
      String userName=username.getText().toString().trim();
      String addId=saveDeatils.push().getKey();
      String phoneNO=phoneNo.getText().toString().trim();
      String item=itemName.getText().toString().trim();
      String itemtype=itemType.getSelectedItem().toString();
      String itemprice=price.getText().toString().trim();
      String imagepath=pathToFile;
      String longitude=loc_longitude.getText().toString().trim();
      String latitude=loc_latitude.getText().toString().trim();
      String addStatus="1";
      PostAdDetails adDetails=new PostAdDetails(userId,addId,userName,phoneNO,item,itemtype,itemprice,imagepath,latitude,longitude,addStatus);

        saveDeatils.child(addId).setValue(adDetails);
        if(imageUri!=null){
                final ProgressDialog progressDialog=new ProgressDialog(this);
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
                    });
        }


       // Toast.makeText(this,"Ad posted Sucessfully....",Toast.LENGTH_LONG).show();
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
       double lati=SellerMapActivity.latitude;
       double longi=SellerMapActivity.longitude;

       loc_latitude.setText(Double.toString(lati));
       loc_longitude.setText(Double.toString(longi));

    }
}
