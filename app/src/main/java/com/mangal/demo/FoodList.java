package com.mangal.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mangal.demo.Common.Common;
import com.mangal.demo.Interface.ItemClickListener;
import com.mangal.demo.ViewHolder.FoodViewHolder;
import com.mangal.demo.model.Food;
import com.squareup.picasso.Picasso;

public class FoodList extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;
    String categoryId = "";

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

   /* //search Functionality
    FirebaseRecyclerAdapter<Food,FoodViewHolder> searchAdapter;
    List<String>suggestList=new ArrayList<>();
    MaterialSearchBar materialSearchBar;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //Firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Food");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        //Get Intent Here

        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");

        if (!categoryId.isEmpty() && categoryId !=null) {

            if(Common.isConnectedToInternet(getBaseContext()))
                loadListFood(categoryId);
            else
            {

                    Toast.makeText(FoodList.this, "Please check your connection", Toast.LENGTH_SHORT).show();
                    return;

            }
        }

        //Search
       /* materialSearchBar =(MaterialSearchBar)findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter your food");
        loadSuggest();*/


    }
   /* private void loadSuggest()
    {
        foodList.orderByChild("MenuId".equals(categoryId))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Food item=postSnapshot.getValue(Food.class);
                            suggestList.add(item.getName());//add name of food to suggest list
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }*/

    private void loadListFood(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("menuId").equalTo(categoryId))//Like: select * from foods where MenuId=)
         {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {

                viewHolder.food_name.setText(model.getName());
                Picasso.get().load(model.getImage()).into(viewHolder.food_image);

                final Food local = model;
                 viewHolder.setItemClickListener(new ItemClickListener() {
                    @ Override
                    public void onClick(View view, int position, boolean isLongClick) {

                       // Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                        //Toast.makeText(FoodList.this, "kdjhgaygd y", Toast.LENGTH_SHORT).show();
                        //Star New Activity
                        Intent foodDetail=new Intent(FoodList.this,FoodDetail.class);
                        foodDetail.putExtra("foodId",adapter.getRef(position).getKey());
                        startActivity(foodDetail);

                    }
                });
            }
        };

        //set Adapter
        //Log.d("TAG",""+adapter.getItemCount());
        //Toast.makeText(this, "Adapter set", Toast.LENGTH_SHORT).show();
        recyclerView.setAdapter(adapter);

    }
}
