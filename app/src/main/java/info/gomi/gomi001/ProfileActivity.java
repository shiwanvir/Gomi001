package info.gomi.gomi001;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.WindowInsets;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.auth.FirebaseAuth.*;

public class ProfileActivity extends AppCompatActivity  {
  private FirebaseAuth firebaseAuth;
  private TextView textViewUserEmail;
   private Button buttonLogout;
   private CircleImageView mProfileImage;
   TextInputLayout userName;
   TextInputLayout phoneNo;
   TextInputEditText editTextinputName;
   TextInputEditText editTextinputPhoneNo;
    Button submit;
    Button cancel;
    private Uri resultUri;
    private String profileImageUrl;
    private FirebaseAuth mAuth;
    DatabaseReference saveDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth=FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        saveDetails= FirebaseDatabase.getInstance().getReference("Users").child("Customers").child(userId);
        submit=(Button) findViewById(R.id.save_details);
        cancel=(Button) findViewById(R.id.cancel_details);
        userName=findViewById(R.id.user_name);
        phoneNo=findViewById(R.id.phone_no);
       editTextinputName = (TextInputEditText) findViewById(R.id.editTextuserName);
        editTextinputPhoneNo = (TextInputEditText) findViewById(R.id.ediTextphoneNo);
        mProfileImage=(CircleImageView) findViewById(R.id.profileImage);
        getUserDetails();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDetails();
            }
        });


        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==Activity.RESULT_OK){
            final Uri imageUri=data.getData();
            resultUri=imageUri;
            mProfileImage.setImageURI(resultUri);

        }
    }


    public void addDetails(){
         String user_Name=userName.getEditText().getText().toString().trim();
         String phone_num=phoneNo.getEditText().getText().toString().trim();
         Map useerInfo=new HashMap();
         useerInfo.put("user_name",user_Name);
         useerInfo.put("phone_num",phone_num);
         saveDetails.updateChildren(useerInfo);


        if(resultUri!=null){
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
           final StorageReference filepath= FirebaseStorage.getInstance().getReference().child("profle_images").child(userId);
            Bitmap bitmap =null;

            try {
                bitmap= MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(),resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream compresImage=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,20,compresImage);
            byte[] data=compresImage.toByteArray();
            UploadTask uploadTask=filepath.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map newImage = new HashMap();
                            newImage.put("profileImageUrl", uri.toString());
                            saveDetails.updateChildren(newImage);
                           // Log.i("Distance", "hureee");
                            finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            finish();
                            return;
                        }
                    });
                }
            });
        }
    }

    private void getUserDetails(){

        saveDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()&& dataSnapshot.getChildrenCount()>0){

                    Map<String,Object> map=( Map<String,Object>)dataSnapshot.getValue();
                    if(map.get("user_name")!=null){
                    String user_name=map.get("user_name").toString();
                    userName.setHintAnimationEnabled(false);
                    editTextinputName.setText(user_name);


                    }
                    if(map.get("phone_num")!=null){
                     String phone_no=map.get("phone_num").toString();
                     phoneNo.setHintAnimationEnabled(false);
                     editTextinputPhoneNo.setText(phone_no);


                    }
                    if(map.get("profileImageUrl")!=null){
                        profileImageUrl=map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load( profileImageUrl).into(mProfileImage);

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }



}
