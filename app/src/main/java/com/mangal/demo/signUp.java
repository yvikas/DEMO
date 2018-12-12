package com.mangal.demo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mangal.demo.Common.Common;
import com.mangal.demo.model.user;

public class signUp extends AppCompatActivity {
    EditText Edtphone,Edtname,Edtpassword;
    Button EdtsignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Edtphone=findViewById(R.id.edtPhone);
        Edtname=findViewById(R.id.edtName);
        Edtpassword=findViewById(R.id.edtPassword);
        EdtsignUp=(Button)findViewById(R.id.btnSignUp);
       final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("user");



        EdtsignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(TextUtils.isEmpty(Edtname.getText()))
                {
                    Edtname.setError("eneter name");
                    Edtname.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(Edtphone.getText()))
                {
                    Edtphone.setError("eneter phone");
                    Edtphone.requestFocus();
                    return;
                }

                if(TextUtils.isEmpty(Edtpassword.getText()))
                {
                    Edtpassword.setError("eneter password");
                    Edtpassword.requestFocus();
                    return;
                }

                if (Common.isConnectedToInternet(getBaseContext())) {


                    final ProgressDialog mDialog = new ProgressDialog(signUp.this);
                    mDialog.setMessage("Please waiting.....");
                    mDialog.show();
                    table_user.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            //check if already user phone
                            if (dataSnapshot.child(Edtphone.getText().toString()).exists()) {
                                mDialog.dismiss();
                                Toast.makeText(signUp.this, "phone number already register", Toast.LENGTH_SHORT).show();
                            }
                            else
                                {
                                mDialog.dismiss();
                               // Toast.makeText(signUp.this, "Sign Up succussfull !", Toast.LENGTH_SHORT).show();
                                user User = new user(Edtname.getText().toString(), Edtpassword.getText().toString());
                                table_user.child(Edtphone.getText().toString()).setValue(User);
                                Toast.makeText(signUp.this, "Sign Up succussfull !", Toast.LENGTH_SHORT).show();
                                //Intent homeIntent = new Intent(signUp.this, home.class);
                               // Common.currentUser = User;
                               // startActivity(homeIntent);
                                finish();
                               // finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                        Toast.makeText(signUp.this, "Please check your connection", Toast.LENGTH_SHORT).show();
                        return;

                }
            }
        });
    }
}
