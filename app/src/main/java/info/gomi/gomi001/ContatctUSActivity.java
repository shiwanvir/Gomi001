package info.gomi.gomi001;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.dmoral.toasty.Toasty;

public class ContatctUSActivity extends AppCompatActivity {

    Button submit;
    Button cancel;
    TextInputLayout email;
    TextInputLayout subject;
    TextInputLayout phoneNo;
    TextInputLayout message;
    DatabaseReference saveDetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contatct_us);
        saveDetails= FirebaseDatabase.getInstance().getReference("contact_us_details");
        submit=(Button) findViewById(R.id.save_details);
        cancel=(Button)findViewById(R.id.cancel_details);
        email=findViewById(R.id.your_email);
        subject=findViewById(R.id.subject);
        phoneNo=findViewById(R.id.phone_no);
        message=findViewById(R.id.your_message);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(confrimationInputs()){
                    addDetails();
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
                else{
                    errorMessage();

                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });



    }

    private void addDetails() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId=user.getUid();
        final String id=saveDetails.push().getKey();
        final String yourEmail=email.getEditText().getText().toString().trim();
        String yourSubject=subject.getEditText().getText().toString().trim();
        String  phoneno=phoneNo.getEditText().getText().toString().trim();
        String contactMessage=message.getEditText().getText().toString().trim();
        ContactUsModel contactUs=new ContactUsModel(yourEmail,userId,yourSubject,phoneno,contactMessage);
        saveDetails.child(id).setValue(contactUs).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
              successMessage();

            }
        });


    }

    private boolean validateSubject(){

        String contactSubject=subject.getEditText().getText().toString().trim();
        if(contactSubject.isEmpty()){
            subject.setError("Filed cant't be empty ");
            return false;
        }
        else if(contactSubject.length()>50){
            subject.setError("Subject too long");
            return false;
        }

        else{
            subject.setError(null);
            subject.setErrorEnabled(false);
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

    private boolean validateEmail(){
        final String emil=email.getEditText().getText().toString();

        if(emil.isEmpty()){
            email.setError("Filed cant't be empty ");
            return false;
        }

        else if (!Patterns.EMAIL_ADDRESS.matcher(emil).matches()){
            email.setError("Incorrect Emil Address");
            return false;
        }
        email.setError(null);
        email.setErrorEnabled(false);
        return true;
    }

    private boolean validateMessage(){

        String contactSubject=message.getEditText().getText().toString().trim();
        if(contactSubject.isEmpty()){
            message.setError("Filed cant't be empty ");
            return false;
        }
        else if(contactSubject.length()>50){
            message.setError("Subject too long");
            return false;
        }

        else{
            message.setError(null);
            message.setErrorEnabled(false);
            return true;
        }

    }


    public boolean confrimationInputs(){
        if(!validateSubject()|!validatePhoneNo()|!validateMessage()){
            return false;
        }
        return true;
    }


    private void successMessage() {
        Toasty.success(this, "Sent Message Sucessfully....", Toast.LENGTH_LONG).show();
    }

    private  void errorMessage(){
        Toasty.error(this, "Please Fill Details Correctly", Toast.LENGTH_LONG).show();
    }
}
