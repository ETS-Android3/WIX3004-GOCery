package com.example.gocery.grocerylist.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gocery.R;
import com.example.gocery.grocerylist.dao.DAOCurrentGroceryItem;
import com.example.gocery.grocerylist.model.GroceryItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CurrentGroceryListAdapter extends BaseAdapter {

    List<GroceryItem> groceryItems;
    Context context;
    LayoutInflater inflater;
    StorageReference storageReference;

    public CurrentGroceryListAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public CurrentGroceryListAdapter(Context context, List<GroceryItem> groceryItems) {
        this.context = context;
        this.groceryItems = groceryItems;
        inflater = LayoutInflater.from(context);

        FirebaseApp firebaseApp = FirebaseApp.initializeApp(context);
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance(firebaseApp);
        storageReference = firebaseStorage.getReference();
    }

    public void setCurrentGroceryItems(ArrayList<GroceryItem> groceryItems) {
        this.groceryItems = groceryItems;
    }

    @Override
    public int getCount() {
        return groceryItems.size();
    }

    @Override
    public Object getItem(int position) {
        return groceryItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        GroceryItem gi = (GroceryItem) this.getItem(position);
        final ViewHolder holder;

        if(convertView == null){
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_current_grocery, parent, false);

            holder = new ViewHolder();
            holder.checkBox = convertView.findViewById(R.id.cb_itemStatus);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        TextView itemName = convertView.findViewById(R.id.tv_itemName);
        TextView itemDesc = convertView.findViewById(R.id.tv_itemDesc);
        TextView itemAmount = convertView.findViewById(R.id.tv_itemAmount);
        ImageView itemImage = convertView.findViewById(R.id.iv_imageItem);


        // Set the items
        itemName.setText(gi.getName());
        itemAmount.setText(gi.getQuantity()+"x");

        if(!gi.getDescription().equalsIgnoreCase("")){
            itemDesc.setText(gi.getDescription());
        }else {
            itemDesc.setVisibility(View.GONE);
        }

        if(gi.getStatus() == true){
            holder.checkBox.setChecked(true);
        }else{
            holder.checkBox.setChecked(false);
        }

        Log.e("image", gi.getImage() == null ? "null" : gi.getImage());
        if(gi.getImage() == null || gi.getImage() == ""){
            itemImage.setVisibility(View.GONE);
        }else{
            storageReference.child(""+gi.getImage()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context)
                            .load(uri)
                            .placeholder(R.drawable.spinning_loading)
                            .error(R.drawable.gocery_logo_only)
                            .into(itemImage);
                }
            });
        }

        holder.checkBox.setOnClickListener(v->{
            HashMap<String, Object> hashMap = new HashMap<>();
            if(holder.checkBox.isChecked()){
                hashMap.put("status", true);
            }else{
                hashMap.put("status", false);
            }

            String key = gi.getKey();
            DAOCurrentGroceryItem dao = new DAOCurrentGroceryItem();
            dao.update(key, hashMap).addOnFailureListener(er->{
                Toast.makeText(v.getContext(), "Error", Toast.LENGTH_SHORT).show();
            })
            ;
        });

        return convertView;
    }



    private class ViewHolder{
        CheckBox checkBox;
    }



}
