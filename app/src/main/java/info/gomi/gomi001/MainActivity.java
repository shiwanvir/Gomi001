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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
        private Button buttonRegister;
        private EditText editTextEmail;
        private EditText editTextPassword;
        private TextView textViewSignin;
        private FirebaseAuth firebaseAuth;

        private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null){
            //start profile
            finish();
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));

        }
        progressDialog =new ProgressDialog(this);
        buttonRegister=(Button) findViewById(R.id.buttonRegister);
        editTextEmail=(EditText)findViewById(R.id.editTextEmail);
        editTextPassword=(EditText)findViewById(R.id.editTextPassword);
        textViewSignin=(TextView)findViewById(R.id.textviewSign);

        buttonRegister.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
    }

    private void registerUser(){

        String email=editTextEmail.getText().toString().trim();
        String password=editTextPassword.getText().toString().trim();
        //String email="samn455@gmail.com";
        //String password="123edfrsd";
        if(TextUtils.isEmpty(email)){
            //if email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
           //stop the function
            return;
        }
        if(TextUtils.isEmpty(password)){
        //if password is empty
            Toast.makeText(this,"Please enter the password ",Toast.LENGTH_SHORT).show();
            //stop the function
            return;
        }
        //if validation is ok
        //show the progress bar to the user
        progressDialog.setMessage("Registering User...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            //user loged sucessfully

                                //start profile
                                finish();
                                startActivity(new Intent(getApplicationContext(),ProfileActivity.class));


                                     }
                        else{
                            Toast.makeText(MainActivity.this,"Registration faild please try again",Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }

    @Override
    public void onClick(View view) {
        if(view==buttonRegister){
            registerUser();
        }
        if(view==textViewSignin){
            //will open the login
            startActivity(new Intent(this,LoginActivity.class));
        }

    }
}
