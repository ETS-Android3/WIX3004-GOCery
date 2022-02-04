package com.example.gocery.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.gocery.R;
import com.example.gocery.model.Voucher;

import java.util.ArrayList;

public class VoucherLVAdapter extends BaseAdapter {
    ArrayList<Voucher> voucher;
    Context context;
    LayoutInflater inflater;

    public VoucherLVAdapter(Context context, ArrayList<Voucher> voucher) {
        this.context = context;
        this.voucher = voucher;
        inflater = LayoutInflater.from(context);
    }

    public void setVoucher(ArrayList<Voucher> voucher) {
        this.voucher = voucher;
    }

    @Override
    public int getCount() {
        return voucher.size();
    }

    @Override
    public Object getItem(int i) {
        return voucher.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Voucher vc = (Voucher) this.getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.voucher_lv_item, parent, false);

            viewHolder.voucherName = convertView.findViewById(R.id.TV_vc_name);
            viewHolder.voucherValue = convertView.findViewById(R.id.TV_vc_value);
            viewHolder.voucherExpDate = convertView.findViewById(R.id.TV_vc_exp_date);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.voucherName.setText(vc.getStore_name());
        viewHolder.voucherValue.setText(vc.getVoucher_value());
        if(vc.getVoucher_exp_date().equalsIgnoreCase("")){
            viewHolder.voucherExpDate.setText("No expiry date");
        } else {
            viewHolder.voucherExpDate.setText("Valid until " + vc.getVoucher_exp_date());
        }

        // Return the completed view to render on screen
        return convertView;
    }

    private static class ViewHolder {
        TextView voucherName;
        TextView voucherValue;
        TextView voucherExpDate;
    }
}
