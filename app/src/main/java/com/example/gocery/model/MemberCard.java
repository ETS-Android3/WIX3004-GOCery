package com.example.gocery.model;
public class MemberCard {
    private String card_name;
    private String card_owner;
    private String card_serial_number;
    private String card_exp_date;
    private String card_phone_no;
    private String card_image;

    private String key;


    public MemberCard() {}

    public MemberCard(String card_name, String card_owner, String card_serial_number, String card_exp_date, String card_phone_no, String card_image) {
        this.card_name = card_name;
        this.card_owner = card_owner;
        this.card_serial_number = card_serial_number;
        this.card_exp_date = card_exp_date;
        this.card_phone_no = card_phone_no;
        this.card_image = card_image;
    }

    public String getCard_phone_no() {
        return card_phone_no;
    }

    public void setCard_phone_no(String card_phone_no) {
        this.card_phone_no = card_phone_no;
    }

    public String getCard_image() {
        return card_image;
    }

    public void setCard_image(String card_image) {
        this.card_image = card_image;
    }

    public String getCard_name() {
        return card_name;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }

    public String getCard_owner() {
        return card_owner;
    }

    public void setCard_owner(String card_owner) {
        this.card_owner = card_owner;
    }

    public String getCard_serial_number() {
        return card_serial_number;
    }

    public void setCard_serial_number(String card_serial_number) {
        this.card_serial_number = card_serial_number;
    }

    public String getCard_exp_date() {
        return card_exp_date;
    }

    public void setCard_exp_date(String card_exp_date) {
        this.card_exp_date = card_exp_date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
