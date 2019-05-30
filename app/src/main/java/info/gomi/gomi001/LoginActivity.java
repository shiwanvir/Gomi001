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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textviewSignUp;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getCurrentUser()!=null){
             //start profile
            finish();
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));

        }
        editTextEmail=(EditText) findViewById(R.id.editTextEmail);
        editTextPassword=(EditText)findViewById(R.id.editTextPassword);
        buttonSignIn=(Button)findViewById(R.id.buttonSign);
        textviewSignUp=(TextView) findViewById(R.id.textviewSignUp);
        progressDialog=new ProgressDialog(this);
        buttonSignIn.setOnClickListener(this);
        textviewSignUp.setOnClickListener(this);


    }
    private void userLogin(){

        String email=editTextEmail.getText().toString().trim();
        String password=editTextPassword.getText().toString().trim();
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

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            //start profile
                                finish();
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));

                        }

                    }
                });

    }

    @Override
    public void onClick(View view) {
        if(view ==buttonSignIn){
            userLogin();
        }
        if(view ==textviewSignUp){
            finish();
            startActivity(new Intent(this,HomeActivity.class));

        }
    }
}
