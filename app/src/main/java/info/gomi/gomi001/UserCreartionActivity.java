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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserCreartionActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private TextView textViewUserEmail;
    private Button buttonLogout;
    private CircleImageView mProfileImage;
    TextInputLayout userName;
    TextInputLayout phoneNo;
    TextInputLayout password;
    TextInputLayout email;
    TextInputEditText editTextinputName;
    TextInputEditText editTextinputPhoneNo;
    TextInputEditText editTextinputPassword;
    TextInputEditText editTextinputEmail;
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
        setContentView(R.layout.activity_user_creartion);
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //Log.i("1 st user id :","1 st user id : "+userId);

        submit=(Button) findViewById(R.id.save_details);
        cancel=(Button) findViewById(R.id.cancel_details);
        userName=findViewById(R.id.user_name);
        phoneNo=findViewById(R.id.phone_no);
        password=findViewById(R.id.password);
        email=findViewById(R.id.user_email);
        editTextinputName = (TextInputEditText) findViewById(R.id.editTextuserName);
        editTextinputPhoneNo = (TextInputEditText) findViewById(R.id.ediTextphoneNo);
        editTextinputPassword=(TextInputEditText)findViewById(R.id.editTextPassword);
        editTextinputEmail=(TextInputEditText)findViewById(R.id.editTextUserEmail);
        mProfileImage=(CircleImageView) findViewById(R.id.profileImage);
        //getUserDetails();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDetails();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode== Activity.RESULT_OK){
            final Uri imageUri=data.getData();
            resultUri=imageUri;
            mProfileImage.setImageURI(resultUri);

        }
    }


    public void addDetails(){

        String user_password=password.getEditText().getText().toString().trim();
        String user_email=email.getEditText().getText().toString().trim();
        final String admin_email=mAuth.getCurrentUser().getEmail();
        Log.i("user email","user email"+admin_email);
        mAuth.createUserWithEmailAndPassword(user_email,user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                String user_Name=userName.getEditText().getText().toString().trim();
                String phone_num=phoneNo.getEditText().getText().toString().trim();
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                saveDetails= FirebaseDatabase.getInstance().getReference("Users").child("Drivers").child(userId);
                Map useerInfo=new HashMap();
                useerInfo.put("user_name",user_Name);
                useerInfo.put("phone_num",phone_num);
                saveDetails.updateChildren(useerInfo);
                Intent intent =new Intent(UserCreartionActivity.this,AdminConfirmationActivity.class);
                intent.putExtra("admin_email",admin_email);
                finish();
                startActivity(intent);

            }
        });


    }

    }
