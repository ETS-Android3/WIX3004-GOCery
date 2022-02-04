package com.example.gocery;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.bumptech.glide.Glide;
import com.example.gocery.dao.DAOVoucher;
import com.example.gocery.model.Voucher;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VoucherDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VoucherDetailFragment extends Fragment {

    TextView voucherValue, voucherAppliedProduct, voucherSerialNo, voucherDescription, voucherExpDate;
    ImageView vc_code_image;
    final int REQUEST_IMAGE = 1;
    Uri imageUri;

    Voucher voucher;
    String itemKey;

    // database and storage
    DAOVoucher vc_dao;
    FirebaseStorage firebaseStorage;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public VoucherDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CouponDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VoucherDetailFragment newInstance(String param1, String param2) {
        VoucherDetailFragment fragment = new VoucherDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_voucher_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        voucherSerialNo = view.findViewById(R.id.TV_viewvc_serial_no);
        voucherValue = view.findViewById(R.id.TV_viewvc_voucher_value);
        voucherAppliedProduct = view.findViewById(R.id.TV_viewvc_applied_product);
        voucherDescription = view.findViewById(R.id.TV_viewvc_desc);
        voucherExpDate = view.findViewById(R.id.TV_viewvc_exp_date);
        vc_code_image = view.findViewById(R.id.imageView2);


        vc_dao = new DAOVoucher();

        getParentFragmentManager().setFragmentResultListener("voucherDetail", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                setItemData(""+result.get("ITEM_KEY"));
            }
        });
    }

    private void setItemData(String item_key) {
        this.itemKey = item_key;
        vc_dao.getSingle(item_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                voucher = snapshot.getValue(Voucher.class);
                voucherValue.setText(voucher.getVoucher_value());
                voucherAppliedProduct.setText(voucher.getApplied_product());
                voucherSerialNo.setText(voucher.getVoucher_serial_number());
                voucherDescription.setText(voucher.getVoucher_description());
                if(voucher.getVoucher_exp_date().equalsIgnoreCase("")){
                    voucherExpDate.setText("No expiry date.");
                } else {
                    voucherExpDate.setText("Valid until " + voucher.getVoucher_exp_date());
                }
                if(voucher.getImageUrl() != null){
                    FirebaseApp firebaseApp = FirebaseApp.initializeApp(getContext());
                    firebaseStorage = FirebaseStorage.getInstance(firebaseApp);
                    StorageReference storageReference = firebaseStorage.getReference();
                    storageReference.child(""+voucher.getImageUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(getContext())
                                    .load(uri)
                                    .placeholder(R.drawable.bar_code)
                                    .error(R.drawable.bar_code)
                                    .into(vc_code_image);
                        }
                    });
                } else {
                    vc_code_image.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}