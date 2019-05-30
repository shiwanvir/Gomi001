package info.gomi.gomi001;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;


public class AdminConfirmationActivity extends AppCompatActivity {
   private Button confrimPassword;
    TextInputLayout password;
    TextInputEditText editTextinputpassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_confirmation);
        mAuth=FirebaseAuth.getInstance();
         confrimPassword=(Button)findViewById(R.id.admin_confirm);

        final String admin_email=getIntent().getStringExtra("admin_email");
         password=findViewById(R.id.admin_password);
         editTextinputpassword=(TextInputEditText) findViewById(R.id.adminEditTextPassword);

         confrimPassword.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 String adminpassword=password.getEditText().getText().toString().trim();

                 mAuth.signInWithEmailAndPassword(admin_email,adminpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {

                         if (!task.isSuccessful()) {

                             String ErrorMessage=task.getException().getMessage();
                             Toasty.error(AdminConfirmationActivity.this, "Error Occurred While Login:"+ErrorMessage, Toast.LENGTH_SHORT, true).show();


                         } else if (task.isSuccessful()) {

                             Intent i=new Intent(AdminConfirmationActivity.this,AdminPanelActivity.class);
                             finish();
                             startActivity(i);
                         }

                     }
                 });
             }
         });

    }
}
