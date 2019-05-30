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
import com.google.firebase.auth.FirebaseAuth;
import com.onesignal.OneSignal;

import es.dmoral.toasty.Toasty;

public class ChangePasswordActivity extends AppCompatActivity {

    TextInputLayout email;
    Button send;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        mAuth=FirebaseAuth.getInstance();
        email=(TextInputLayout)findViewById(R.id.mail_addres);
        send=(Button)findViewById(R.id.send_mail);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail= email.getEditText().getText().toString().trim();
                mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            Toasty.info(ChangePasswordActivity.this, "Please Check Your Mail Box..", Toast.LENGTH_SHORT, true).show();
                            logOut();
                        }
                        else{
                            String ErrorMessage=task.getException().getMessage();
                            Toasty.error(ChangePasswordActivity.this, "Error Occurred While Processing:"+ErrorMessage, Toast.LENGTH_SHORT, true).show();

                        }

                    }
                });
            }
        });
    }


    private void logOut() {
        //Toast.makeText(ChangePasswordActivity.this,"Login out.....",Toast.LENGTH_SHORT).show();
        //signing out from oneSignal subscription
        OneSignal.setSubscription(false);
        mAuth.signOut();
        finish();
        startActivity(new Intent(this,MainActivity.class));
    }
}
