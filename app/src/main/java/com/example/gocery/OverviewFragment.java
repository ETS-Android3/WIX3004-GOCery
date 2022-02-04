package com.example.gocery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.gocery.adapter.MemberCardLVAdapter;
import com.example.gocery.adapter.VoucherLVAdapter;
import com.example.gocery.dao.DAOMemberCard;
import com.example.gocery.dao.DAOVoucher;
import com.example.gocery.model.MemberCard;
import com.example.gocery.model.Voucher;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverviewFragment extends Fragment {
    ListView voucherLV;
    GridView memberCardLV;
    DAOVoucher vc_dao;
    VoucherLVAdapter vc_adapter;
    DAOMemberCard mc_dao;
    MemberCardLVAdapter mc_adapter;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OverviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OverviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OverviewFragment newInstance(String param1, String param2) {
        OverviewFragment fragment = new OverviewFragment();
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
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton btnAddCard = view.findViewById(R.id.Btn_AddCard);
        FloatingActionButton btnAddVoucher = view.findViewById(R.id.Btn_AddNewCard);
        ExtendedFloatingActionButton extendedAdd = view.findViewById(R.id.Btn_ExtendedAdd);
        TextView tvAddCard = view.findViewById(R.id.TV_AddCard);
        TextView tvAddVoucher= view.findViewById(R.id.TV_AddVoucher);

        btnAddCard.setVisibility(View.GONE);
        btnAddVoucher.setVisibility(View.GONE);
        tvAddCard.setVisibility(View.GONE);
        tvAddVoucher.setVisibility(View.GONE);

        extendedAdd.shrink();

        // Initialise new DataAccessObject
        vc_dao = new DAOVoucher();
        mc_dao = new DAOMemberCard();

        // Initializing variables for listviews.
        voucherLV = view.findViewById(R.id.LV_voucher_list);
        memberCardLV = view.findViewById(R.id.GV_member_list);

        // Construct the data source
        ArrayList<Voucher> voucherList = new ArrayList<>();
        ArrayList<MemberCard> memberList = new ArrayList<>();

        // Create the adapter to convert the array to views
        vc_adapter = new VoucherLVAdapter(getContext(), voucherList);
        mc_adapter = new MemberCardLVAdapter(getContext(), memberList);

        voucherLV.setAdapter(vc_adapter);
        memberCardLV.setAdapter(mc_adapter);

        loadData();

        memberCardLV.setLongClickable(true);
        memberCardLV.setOnItemLongClickListener((parent, childView, position, id) -> {

            MemberCard memberCard = (MemberCard) mc_adapter.getItem(position);
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Member Card")
                    .setMessage("Are you sure you want to delete " + memberCard.getCard_name() + " record?")
                    .setNegativeButton("CANCEL", null)
                    .setPositiveButton("DELETE", (dialog, which) -> mc_dao.remove(memberCard.getKey()).addOnSuccessListener(suc -> Toast.makeText(getActivity(), "Member card record deleted successfully.", Toast.LENGTH_SHORT).show()).addOnFailureListener(er -> Toast.makeText(getActivity(), "Error: Deletion Failed", Toast.LENGTH_SHORT).show()))
                    .show();
            return true;
        });

        memberCardLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MemberCard memberCard = (MemberCard) mc_adapter.getItem(position);

                // Pass data to the update item
                Bundle result = new Bundle();
                result.putString("ITEM_KEY", memberCard.getKey());
                getParentFragmentManager().setFragmentResult("cardDetail", result);
                Navigation.findNavController(view).navigate(R.id.DestCardDetail);
            }
        });

        voucherLV.setLongClickable(true);
        voucherLV.setOnItemLongClickListener((parent, childView, position, id) -> {

            Voucher voucher = (Voucher) vc_adapter.getItem(position);
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Voucher")
                    .setMessage("Are you sure you want to delete " + voucher.getStore_name() + " record?")
                    .setNegativeButton("CANCEL", null)
                    .setPositiveButton("DELETE", (dialog, which) -> vc_dao.remove(voucher.getKey()).addOnSuccessListener(suc -> Toast.makeText(getActivity(), "Voucher record deleted successfully.", Toast.LENGTH_SHORT).show()).addOnFailureListener(er -> Toast.makeText(getActivity(), "Error: Deletion Failed", Toast.LENGTH_SHORT).show()))
                    .show();
            return true;
        });

        voucherLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Voucher voucher = (Voucher) vc_adapter.getItem(position);

                // Pass data to the update item
                Bundle result = new Bundle();
                result.putString("ITEM_KEY", voucher.getKey());
                getParentFragmentManager().setFragmentResult("voucherDetail", result);
                Navigation.findNavController(view).navigate(R.id.DestVoucherDetail);
            }
        });

        View.OnClickListener expand_btn = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(btnAddCard.getVisibility() == View.VISIBLE){
                    btnAddCard.hide();
                    btnAddVoucher.hide();
                    tvAddCard.setVisibility(View.GONE);
                    tvAddVoucher.setVisibility(View.GONE);
                    extendedAdd.shrink();
                } else {
                    btnAddCard.show();
                    btnAddVoucher.show();
                    tvAddCard.setVisibility(View.VISIBLE);
                    tvAddVoucher.setVisibility(View.VISIBLE);
                    extendedAdd.extend();
                }
            }
        };

        View.OnClickListener addCard = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.DestAddCard);
            }
        };
        View.OnClickListener addVoucher = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.DestAddVoucher);
            }
        };
        extendedAdd.setOnClickListener(expand_btn);
        btnAddCard.setOnClickListener(addCard);
        btnAddVoucher.setOnClickListener(addVoucher);
    }

    private void loadData() {
        vc_dao.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean valid = true;
                ArrayList<Voucher> voucherList = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Voucher vc = data.getValue(Voucher.class);
                    if(!vc.getVoucher_exp_date().equalsIgnoreCase("")){
                        try {
                            valid = checkDate(vc.getVoucher_exp_date());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    if (vc != null && valid) {
                        vc.setKey(data.getKey());
                        voucherList.add(vc);
                    }
                }

                // Update custom adapter
                vc_adapter.setVoucher(voucherList);
                vc_adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        mc_dao.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<MemberCard> memberList = new ArrayList<>();

                for (DataSnapshot data : snapshot.getChildren()) {
                    MemberCard mc = data.getValue(MemberCard.class);
                    if (mc != null) {
                        mc.setKey(data.getKey());
                        memberList.add(mc);
                    }
                }

                // Update custom adapter
                mc_adapter.setMember(memberList);
                mc_adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private boolean checkDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = sdf.format(new Date());
        Date today = sdf.parse(strDate);
        Date expDate = sdf.parse(date);

        int result = today.compareTo(expDate);
        System.out.println("result: " + result);


        if (result <= 0) {
            return true;
        } else {
            return false;
        }
    }

}