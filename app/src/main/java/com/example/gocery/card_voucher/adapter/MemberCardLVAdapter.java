package com.example.gocery.card_voucher.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gocery.R;
import com.example.gocery.card_voucher.model.MemberCard;

import java.util.ArrayList;

public class MemberCardLVAdapter extends BaseAdapter {
    ArrayList<MemberCard> memberCard;
    Context context;
    LayoutInflater inflater;

    public MemberCardLVAdapter(Context context, ArrayList<MemberCard> memberCard) {
        this.context = context;
        this.memberCard = memberCard;
        inflater = LayoutInflater.from(context);
    }

    public void setMember(ArrayList<MemberCard> memberCard) {
        this.memberCard = memberCard;
    }

    @Override
    public int getCount() {
        return memberCard.size();
    }

    @Override
    public Object getItem(int i) {
        return memberCard.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        MemberCard mc = (MemberCard) this.getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.member_rv_item, parent, false);

            viewHolder.storeName = convertView.findViewById(R.id.TV_card_store_name);
            viewHolder.cardOwner = convertView.findViewById(R.id.TV_card_owner);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.storeName.setText(mc.getCard_name());
        viewHolder.cardOwner.setText(mc.getCard_owner());

        // Return the completed view to render on screen
        return convertView;
    }

    private static class ViewHolder {
        TextView storeName;
        TextView cardOwner;
    }

}
