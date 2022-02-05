package com.example.gocery.card_voucher.dao;

import com.example.gocery.card_voucher.model.MemberCard;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class DAOMemberCard {

    private DatabaseReference dbRef;

    public DAOMemberCard(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        dbRef = db.getReference("MemberCard");
    }

    public Task<Void> addMemberCard(MemberCard memCd) {
        return dbRef.push().setValue(memCd);
    }

    public Task<Void> update(String key, HashMap<String, Object> hashMap){
        return dbRef.child(key).updateChildren(hashMap);
    }

    public Task<Void> remove(String key) {
        return dbRef.child(key).removeValue();
    }

    public Query get() {
        return dbRef;
    }

    public Query getSingle(String item_key) {
        return dbRef.child(item_key);
    }
}
