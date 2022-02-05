package com.example.gocery.card_voucher.model;

import com.google.firebase.database.Exclude;

public class Voucher {
    private String store_name;
    private String voucher_value;
    private String voucher_exp_date;
    private String voucher_serial_number;
    private String voucher_description;
    private String applied_product;
    private String imageUrl;

    @Exclude
    private String key;

    public Voucher() {
    }

    public Voucher(String store_name, String voucher_value, String voucher_serial_number, String voucher_exp_date, String voucher_description, String applied_product, String imageUrl) {
        this.store_name = store_name;
        this.voucher_value = voucher_value;
        this.voucher_serial_number = voucher_serial_number;
        this.voucher_exp_date = voucher_exp_date;
        this.voucher_description = voucher_description;
        this.applied_product = applied_product;
        this.imageUrl = imageUrl;
    }

    public String getVoucher_serial_number() {
        return voucher_serial_number;
    }

    public void setVoucher_serial_number(String voucher_serial_number) {
        this.voucher_serial_number = voucher_serial_number;
    }

    public String getApplied_product() {
        return applied_product;
    }

    public void setApplied_product(String applied_product) {
        this.applied_product = applied_product;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getVoucher_value() {
        return voucher_value;
    }

    public void setVoucher_value(String voucher_value) {
        this.voucher_value = voucher_value;
    }

    public String getVoucher_exp_date() {
        return voucher_exp_date;
    }

    public void setVoucher_exp_date(String voucher_exp_date) {
        this.voucher_exp_date = voucher_exp_date;
    }

    public String getVoucher_description() {
        return voucher_description;
    }

    public void setVoucher_description(String voucher_description) {
        this.voucher_description = voucher_description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
