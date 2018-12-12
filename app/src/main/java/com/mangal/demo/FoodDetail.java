package com.mangal.demo;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mangal.demo.Common.Common;
import com.mangal.demo.Database.Database;
import com.mangal.demo.model.Food;
import com.mangal.demo.model.Order;
import com.squareup.picasso.Picasso;

public class FoodDetail extends AppCompatActivity {

    TextView food_name,food_price,food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;
    String foodId="";
    FirebaseDatabase database;
    DatabaseReference food;
    Food CurrentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //Firebase
        database=FirebaseDatabase.getInstance();
        food=database.getReference("Food");

        //Init View
        numberButton=(ElegantNumberButton)findViewById(R.id.number_button);
        btnCart=(FloatingActionButton)findViewById(R.id.btnCart);

        //write function to add food into cart
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(getBaseContext()).addToCart(new Order(
                        foodId,
                        CurrentFood.getName(),
                        numberButton.getNumber(),
                        CurrentFood.getPrice(),
                        CurrentFood.getDiscount()
                ));
                Toast.makeText(FoodDetail.this, "ADDED to Cart", Toast.LENGTH_SHORT).show();

            }
        });

        food_description=(TextView)findViewById(R.id.food_description);
        food_name=(TextView)findViewById(R.id.food_name);
        food_price=(TextView)findViewById(R.id.food_price);
        food_image=(ImageView)findViewById(R.id.img_food);
        collapsingToolbarLayout=(CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.CollapsedAppbar);

        //Get food Id from Intent
        if(getIntent() !=null)
            foodId=getIntent().getStringExtra("foodId");
        if(!foodId.isEmpty())
        {
            if(Common.isConnectedToInternet(getBaseContext()))
                getDetailFood(foodId);
            else
            {
                Toast.makeText(FoodDetail.this, "Please check your connection", Toast.LENGTH_SHORT).show();
                return;
            }
        }

    }
    private void getDetailFood(String foodId)
    {
        food.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CurrentFood =dataSnapshot.getValue(Food.class);

                //Set Image
                Picasso.get().load(CurrentFood.getImage()).into(food_image);
                collapsingToolbarLayout.setTitle(CurrentFood.getName());
                food_price.setText(CurrentFood.getPrice());
                food_name.setText(CurrentFood.getName());
                food_description.setText(CurrentFood.getDescription());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
