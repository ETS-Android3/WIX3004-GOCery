package com.example.gocery.card_voucher;

import android.content.Intent;
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
import com.example.gocery.R;
import com.example.gocery.card_voucher.dao.DAOMemberCard;
import com.example.gocery.card_voucher.model.MemberCard;
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
 * Use the {@link CardDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CardDetailFragment extends Fragment {
    TextView serialNo, cardOwner, phoneNo, cardName, cardExpDate;
    ImageView vc_code_image;
    final int REQUEST_IMAGE = 1;
    Uri imageUri;

    MemberCard memberCard;
    String itemKey;

    // database and storage
    DAOMemberCard mc_dao;
    FirebaseStorage firebaseStorage;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CardDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CardDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CardDetailFragment newInstance(String param1, String param2) {
        CardDetailFragment fragment = new CardDetailFragment();
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
        return inflater.inflate(R.layout.fragment_card_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        serialNo = view.findViewById(R.id.TV_vcard_serial_no);
        cardOwner = view.findViewById(R.id.TV_vcard_owner);
        phoneNo = view.findViewById(R.id.TV_vcard_phone_no);
        cardName = view.findViewById(R.id.TV_vcard_name);
        cardExpDate = view.findViewById(R.id.TV_vcard_exp_date);
        vc_code_image = view.findViewById(R.id.IV_vc_imageView);

        mc_dao = new DAOMemberCard();

        getParentFragmentManager().setFragmentResultListener("cardDetail", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                setItemData(""+result.get("ITEM_KEY"));
            }
        });
    }

    private void setItemData(String item_key) {
        this.itemKey = item_key;
        mc_dao.getSingle(item_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                memberCard = snapshot.getValue(MemberCard.class);
                cardName.setText(memberCard.getCard_name());
                cardOwner.setText(memberCard.getCard_owner());
                serialNo.setText(memberCard.getCard_serial_number());
                if(memberCard.getCard_phone_no().equalsIgnoreCase("")){
                    phoneNo.setText("No registered phone number");
                } else {
                    phoneNo.setText(memberCard.getCard_phone_no());
                }
                if(memberCard.getCard_exp_date().equalsIgnoreCase("")){
                    cardExpDate.setText("No expiry date");
                } else {
                    cardExpDate.setText(memberCard.getCard_exp_date());
                }
                if(memberCard.getCard_image() != null){
                    FirebaseApp firebaseApp = FirebaseApp.initializeApp(getContext());
                    firebaseStorage = FirebaseStorage.getInstance(firebaseApp);
                    StorageReference storageReference = firebaseStorage.getReference();
                    storageReference.child(""+memberCard.getCard_image()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE && data != null && data.getData() != null){
            imageUri = data.getData();
            vc_code_image.setImageURI(imageUri);
        }else{
            imageUri = null;
            vc_code_image.setImageURI(null);
        }
    }

}