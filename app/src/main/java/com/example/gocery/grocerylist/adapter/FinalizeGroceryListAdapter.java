package com.example.gocery.grocerylist.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gocery.R;
import com.example.gocery.grocerylist.model.GroceryItem;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class FinalizeGroceryListAdapter extends BaseAdapter {

    List<GroceryItem> groceryItems;
    Context context;
    LayoutInflater inflater;
    StorageReference storageReference;

    public FinalizeGroceryListAdapter(Context context, List<GroceryItem> groceryItems) {
        this.context = context;
        this.groceryItems = groceryItems;
        inflater = LayoutInflater.from(context);
    }

    public void setCurrentGroceryItems(ArrayList<GroceryItem> groceryItems) {
        this.groceryItems = groceryItems;
    }

    public List<GroceryItem> getList(){
        return this.groceryItems;
    }


    @Override
    public int getCount() {

        if(groceryItems.isEmpty()){
            return 0;
        }else{
            return groceryItems.size();
        }
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

        if(convertView == null){
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_finalize_grocery, parent, false);
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

        Log.e("image", gi.getImage() == null ? "null" : gi.getImage());
        if(gi.getImage() == null){
            itemImage.setVisibility(View.GONE);
        }else {
//            storageReference = FirebaseStorage.getInstance().getReference(gi.getImage());
//
//            try {
//                final File localFile = File.createTempFile(gi.getImage(), "jpeg");
//                storageReference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
//                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                    itemImage.setImageBitmap(bitmap);
//                });
//
//            } catch (Exception e) {
//
//            }
        }

        return convertView;
    }
}
