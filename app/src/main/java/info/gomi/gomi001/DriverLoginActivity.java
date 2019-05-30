package info.gomi.gomi001;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.onesignal.OneSignal;

import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class DriverLoginActivity extends AppCompatActivity {
    private TextInputLayout mEmail,mPassord;
    private Button mLogin,mRegistrtation;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private DatabaseReference deviceTokenRef;
    private DatabaseReference userStatusRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);
        deviceTokenRef=FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers");
        mAuth=FirebaseAuth.getInstance();
        firebaseAuthStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){
                    Intent intent=new Intent(DriverLoginActivity.this,HomeActivity.class);
                    startActivity(intent);
                    finish();
                    return;

               }

            }
        };


        mEmail=findViewById(R.id.email);
        mPassord=findViewById(R.id.password);
        mLogin=(Button)findViewById(R.id.login);
        mRegistrtation=(Button)findViewById(R.id.registration);

        mRegistrtation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get values from text filds
                final String emil = mEmail.getEditText().getText().toString();
                final String password = mPassord.getEditText().getText().toString();
                //check validation
                if(!confermationValidate()){
                    Toasty.error(DriverLoginActivity.this, "Please fill Details correctly..", Toast.LENGTH_SHORT).show();
                }
                else{
                    //create new user in firebase
                mAuth.createUserWithEmailAndPassword(emil, password).addOnCompleteListener(DriverLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {

                            String ErrorMessage=task.getException().getMessage();
                            Toasty.error(DriverLoginActivity.this, "Error Occurred While Registering:"+ErrorMessage, Toast.LENGTH_SHORT, true).show();
                        } else {
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers").child(userId);
                            DbRef.setValue(true);
                            Map userRole=new HashMap();
                            userRole.put("userRole","driv");
                            DbRef.updateChildren(userRole);
                            String deviceToken= FirebaseInstanceId.getInstance().getToken();
                            deviceTokenRef.child(userId).child("device_token")
                                    .setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        //
                                    }
                                }
                            });
                            //creaet reffernec for one signal api
                            OneSignal.startInit(DriverLoginActivity.this).init();
                            //subscribe user for the service
                            OneSignal.setSubscription(true);
                            //set notification key for identify each user uniquely
                            OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                                @Override
                                public void idsAvailable(String userId, String registrationId) {
                                    //set notification key fro user details
                                    FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers")
                                            .child(FirebaseAuth.getInstance().getUid()).child("notificationKey").setValue(userId);
                                }
                            });
                            OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);

                        }
                    }
                });
            }
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emil = mEmail.getEditText().getText().toString();
                final String password = mPassord.getEditText().getText().toString();
                if(!confermationValidate()){
                    Toasty.error(DriverLoginActivity.this, "Please fill Details correctly..", Toast.LENGTH_SHORT).show();
                }
                else{
                    mAuth.signInWithEmailAndPassword(emil, password).addOnCompleteListener(DriverLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {

                            String ErrorMessage=task.getException().getMessage();
                            Toasty.error(DriverLoginActivity.this, "Error Occurred While Login:"+ErrorMessage, Toast.LENGTH_SHORT, true).show();
                        } else if (task.isSuccessful()) {
                            String currentUserId = mAuth.getCurrentUser().getUid();
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();
                            deviceTokenRef.child(currentUserId).child("device_token")
                                    .setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //
                                    }
                                }
                            });

                            OneSignal.startInit(DriverLoginActivity.this).init();
                            //subscribe user for the service
                            OneSignal.setSubscription(true);
                            //set notification key for identify each user uniquely
                            OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                                @Override
                                public void idsAvailable(String userId, String registrationId) {
                                    FirebaseDatabase.getInstance().getReference().child("Users").child("Drivers")
                                            .child(FirebaseAuth.getInstance().getUid()).child("notificationKey").setValue(userId);
                                }
                            });
                            OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);


                        }

                    }
                });
            }
            }
        });

    }

    private boolean validateEmail(){
        final String emil=mEmail.getEditText().getText().toString();

        if(emil.isEmpty()){
            mEmail.setError("Filed cant't be empty ");
            return false;
        }

       else if (!Patterns.EMAIL_ADDRESS.matcher(emil).matches()){
            mEmail.setError("Incorrect Emil Address");
            return false;
        }
        mEmail.setError(null);
        mEmail.setErrorEnabled(false);
        return true;
    }


    private boolean validatePassword(){
        final String password=mPassord.getEditText().getText().toString();
        if(password.isEmpty()){
            mPassord.setError("Filed cant't be empty ");
            return false;
        }

        else if(password.length()<10){
            mPassord.setError("Too Short password");
            return false;
        }
        else if(password.length()>15){
            mPassord.setError("Too Long password");
            return false;
        }
        mPassord.setError(null);
        mPassord.setErrorEnabled(false);
        return true;

    }

    public  boolean confermationValidate(){

        if(!validateEmail()|!validatePassword()){
            return false;
        }
        return  true;
    }

    protected void onStart() {

        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }


    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}
