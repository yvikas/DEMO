package com.mangal.demo.Service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.mangal.demo.Common.Common;
import com.mangal.demo.model.Token;

public class MyFirebaseIdService extends FirebaseInstanceIdService{

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String tokenRefreshed= FirebaseInstanceId.getInstance().getToken();
        updateTokenToFirebase(tokenRefreshed);
    }
    private void updateTokenToFirebase(String tokenRefreshed)

    {

        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token token =new Token(tokenRefreshed,false);//false because this token send from client app
        tokens.child(Common.currentUser.getPhone()).setValue(token);
    }
}
