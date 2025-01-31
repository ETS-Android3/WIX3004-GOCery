package com.example.gocery.card_voucher;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.gocery.R;
import com.example.gocery.card_voucher.dao.DAOMemberCard;
import com.example.gocery.card_voucher.model.MemberCard;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddCardFragment extends Fragment {

    EditText et_card_name, et_card_owner, et_serial_no, et_exp_date, et_phone_no;
    Button btnAddNewCard;
    final Calendar c = getInstance();

    // database and storage
    DAOMemberCard mc_dao;
    StorageReference storageReference;

    final int REQUEST_IMAGE = 1;
    Uri imageUri;
    ImageView upl;
    String fileUrl;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddCardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddCardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddCardFragment newInstance(String param1, String param2) {
        AddCardFragment fragment = new AddCardFragment();
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
        return inflater.inflate(R.layout.fragment_add_card, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        // Initializing DatePickerDialog
        et_card_name = view.findViewById(R.id.TI_card_name);
        et_card_owner = view.findViewById(R.id.TI_card_owner);
        et_phone_no = view.findViewById(R.id.TI_card_phone_no);
        et_serial_no = view.findViewById(R.id.TI_card_serial_no);
        et_exp_date = view.findViewById(R.id.TI_card_exp_date);
        btnAddNewCard = view.findViewById(R.id.Btn_AddNewCard);

        // Initializing DatePickerDialog
        DatePickerDialog.OnDateSetListener date = (v, year, month, day) -> {
            c.set(YEAR, year);
            c.set(MONTH, month);
            c.set(DAY_OF_MONTH, day);

            // Update label appearance in view
            updateLabel();
        };

        // On click listener for displaying DatePickerDialog
        et_exp_date.setOnClickListener(v -> new DatePickerDialog(getActivity(), date, c.get(YEAR), c.get(MONTH), c.get(DAY_OF_MONTH)).show());

        upl = view.findViewById(R.id.IV_upload_img);

        // Selecting Image
        upl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        View.OnClickListener OCLAddCard = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String card_name = et_card_name.getText().toString();
                String card_owner = et_card_owner.getText().toString();
                String serial_no = et_serial_no.getText().toString();
                String exp_date = et_exp_date.getText().toString();
                String phone_no = et_phone_no.getText().toString();
                String image_url = null;

                if(imageUri != null){
                    image_url =  uploadImage();
                }

                if(TextUtils.isEmpty(card_name)){
                    et_card_name.setError("Card name is required");
                }
                if(TextUtils.isEmpty(card_owner)){
                    et_card_owner.setError("Card owner is required");
                }
                if(TextUtils.isEmpty(serial_no)){
                    et_serial_no.setError("Card serial number is required");
                }
                else {

                    DAOMemberCard dao = new DAOMemberCard();

                    MemberCard memCard = new MemberCard(card_name, card_owner, serial_no, exp_date, phone_no, image_url);

                    dao.addMemberCard(memCard).addOnSuccessListener(suc ->
                    {
                        Toast.makeText(getActivity(), "New card has been added successfully", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(view).navigate(R.id.DestOverview);
                    }).addOnFailureListener(er ->
                    {
                        Toast.makeText(getActivity(), ""+ er.getMessage(), Toast.LENGTH_SHORT).show();
                    });

                }

            }
        };
        btnAddNewCard.setOnClickListener(OCLAddCard);
    }


    private void updateLabel() {
        String format = "dd/MM/yyyy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.UK);
        et_exp_date.setText(dateFormat.format(c.getTime()));
    }

    public void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    public String uploadImage(){

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.UK);
        Date now = new Date();
        Log.e("IMAGE EXT", imageUri.toString());
        String fileName = "member_card_image/"+formatter.format(now);
        this.fileUrl = fileName;

        storageReference = FirebaseStorage.getInstance().getReference(fileName);
        storageReference.putFile(imageUri).addOnSuccessListener(suc->{
            Toast.makeText(getActivity(), "upload success", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(er->{
            Toast.makeText(getActivity(), "upload failed", Toast.LENGTH_SHORT).show();
            this.fileUrl = null;
        });

        return this.fileUrl;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_IMAGE && data != null && data.getData() != null){
            imageUri = data.getData();
            upl.setImageURI(imageUri);
        }
    }

}