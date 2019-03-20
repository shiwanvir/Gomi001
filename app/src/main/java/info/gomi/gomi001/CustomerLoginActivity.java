package info.gomi.gomi001;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerLoginActivity extends AppCompatActivity {

    private EditText mEmail,mPassord;
    private Button mLogin,mRegistrtation;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);
        mAuth=FirebaseAuth.getInstance();
        firebaseAuthStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null){

                    Intent intent=new Intent(CustomerLoginActivity.this,HomeActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }

            }
        };

        mEmail=(EditText)findViewById(R.id.email);
        mPassord=(EditText)findViewById(R.id.password);
        mLogin=(Button)findViewById(R.id.login);
        mRegistrtation=(Button)findViewById(R.id.registration);

        mRegistrtation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emil = mEmail.getText().toString();
                final String password = mPassord.getText().toString();
                if(TextUtils.isEmpty(emil)){
                    //if email is empty
                    Toast.makeText(CustomerLoginActivity.this, "Email is Empty", Toast.LENGTH_SHORT).show();
                    //stop the function
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    //if password is empty
                    Toast.makeText(CustomerLoginActivity.this, "Password is Empty", Toast.LENGTH_SHORT).show();
                    //stop the function
                    return;
                }
                else{
                mAuth.createUserWithEmailAndPassword(emil, password).addOnCompleteListener(CustomerLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {

                            Toast.makeText(CustomerLoginActivity.this, "Sign Up Error", Toast.LENGTH_SHORT).show();
                        } else {
                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference().child("Users").child("Customers").child(userId);
                            DbRef.setValue(true);


                        }
                    }
                });
            }
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emil=mEmail.getText().toString();
                final String password=mPassord.getText().toString();
                mAuth.signInWithEmailAndPassword(emil,password).addOnCompleteListener(CustomerLoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(!task.isSuccessful()){

                            Toast.makeText(CustomerLoginActivity.this,"Login Error",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

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
