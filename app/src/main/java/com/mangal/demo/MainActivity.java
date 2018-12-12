package com.mangal.demo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mangal.demo.Common.Common;
import com.mangal.demo.model.user;

import static com.mangal.demo.Common.Common.currentUser;

public class MainActivity extends AppCompatActivity  {
/*Button btnsignIn,btnsignUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnsignIn=(Button)findViewById(R.id.btnSignIn);
        btnsignUp=(Button)findViewById(R.id.btnSignUp);

        btnsignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signIn=new Intent(MainActivity.this, com.mangal.demo.signIn.class);
                startActivity(signIn);

            }
        });*/
    EditText edtPhone,edtPssword;
    Button btnSignIn;
    TextView btnsignUp;
    AwesomeValidation awesomeValidation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtPhone=findViewById(R.id.EdtPhone);
        edtPssword=findViewById(R.id.EdtPasswaord);
        btnSignIn=(Button)findViewById(R.id.SignIn);
        btnsignUp=(TextView)findViewById(R.id.btnSignUp) ;

        awesomeValidation =new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(MainActivity.this,R.id.EdtPhone, RegexTemplate.TELEPHONE,R.string.Phone);

        //init firebase
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("user");
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(Common.isConnectedToInternet(getBaseContext())) {


                    final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
                    mDialog.setMessage("Please waiting.....");
                    mDialog.show();
                    table_user.addValueEventListener(new ValueEventListener()

                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //check if user not exixt in data base
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                mDialog.dismiss();
                                //get user information
                                user User = dataSnapshot.child(edtPhone.getText().toString()).getValue(user.class);

                                User.setPhone(edtPhone.getText().toString());//Set Phone
                                if(TextUtils.isEmpty(edtPhone.getText()))
                                {
                                    edtPhone.setError("eneter phone");
                                    edtPhone.requestFocus();
                                    return;
                                }
                                if(TextUtils.isEmpty(edtPssword.getText()))
                                {
                                    edtPssword.setError("Enter Password");
                                    edtPssword.requestFocus();
                                    return;
                                }

                                if (User.getPassword().equals(edtPssword.getText().toString())) {
                                    //Toast.makeText(signIn.this, "sign In succssfully", Toast.LENGTH_SHORT).show();
                                    Intent homeIntent = new Intent(MainActivity.this, home.class);
                                    Common.currentUser = User;
                                    startActivity(homeIntent);
                                    finish();

                                } else {
                                    Toast.makeText(MainActivity.this, "wrong password", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(MainActivity.this, "user not exist in database", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Please check your connection", Toast.LENGTH_SHORT).show();
                    return;
                }
            }


        });


        btnsignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUp=new Intent(MainActivity.this, com.mangal.demo.signUp.class);
                startActivity(signUp);
            }
        });
    }
}