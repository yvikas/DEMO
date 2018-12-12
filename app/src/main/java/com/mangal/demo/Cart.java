package com.mangal.demo;

import android.content.DialogInterface;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mangal.demo.Common.Common;
import com.mangal.demo.Database.Database;
import com.mangal.demo.ViewHolder.CartAdapter;
import com.mangal.demo.model.Order;
import com.mangal.demo.model.Request;
import com.mangal.demo.model.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Cart extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database;
    DatabaseReference requests;
    TextView txtTotalPrice;
    Button btnPlace;

    List<Order> cart=new ArrayList<>();
    CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //FireBase
        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Requests");

        //Init
        recyclerView=(RecyclerView)findViewById(R.id.listCard);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice=(TextView)findViewById(R.id.total);
        btnPlace=(Button)findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(cart.size()>0)
                    showAlertDialog();
                else
                    Toast.makeText(Cart.this, "your cart is empty!!!", Toast.LENGTH_SHORT).show();


            }
        });

        loadListFood();


    }
    private void showAlertDialog()
    {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One more step");
        alertDialog.setMessage("Enter your address:");
        final EditText edtAddress= new EditText(Cart.this);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        edtAddress.setLayoutParams(lp);
        alertDialog.setView(edtAddress);//add edit Text to alert dialog
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //create new Request
                Request request=new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        edtAddress.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        cart

                );

                //submit to firebase
                //we will using system.CurrentMilli to key
                String order_number=String.valueOf(System.currentTimeMillis());
                requests.child(String.valueOf((System.currentTimeMillis())))
                        .setValue(request);
                new Database (getBaseContext()).cleanCart();

                sendNotificationOrder(order_number);
              Toast.makeText(Cart.this, "Thank you , Order Place", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();

    }

    private void sendNotificationOrder(String order_number)
    {
        DatabaseReference tokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query data =tokens.orderByChild("isServerToken").equalTo(true);
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot postSnapShot:dataSnapshot.getChildren())
                {
                    Token serverToken=postSnapShot.getValue(Token.class);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }




    private void loadListFood()
    {
        cart=new Database(this).getCarts();
        adapter=new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        //Calculate total price
        int total=0;
        for (Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        Locale locale=new Locale("en","us");
        NumberFormat fmt=NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
       if(item.getTitle().equals(Common.DELETE))
           deleteCart(item.getOrder());
        return true;
    }

    private  void deleteCart(int position)
    {
        //we will remove item at list<order> by position
        cart.remove(position);

        //after that ,we will delete all old data from SQLite

        new Database(this).cleanCart();
        //and final ,we will update new data from List<order> to SQLite
        for(Order item:cart)
            new Database(this).addToCart(item);
        loadListFood();

    }
}
