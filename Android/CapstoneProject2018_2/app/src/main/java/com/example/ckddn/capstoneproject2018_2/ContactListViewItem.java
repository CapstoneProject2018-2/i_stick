package com.example.ckddn.capstoneproject2018_2;

import android.graphics.drawable.Drawable;

//Parent Mode에서 담당하는 관리대상자들이 표시되는 메뉴에서 연락처 Model
//implemented by 양인수

public class ContactListViewItem {
    private String uno; //  implement by ckddn: add for parent menu application
    private Drawable iconDrawable;
    private String titleStr;
    private String descStr;

    public void setIcon(Drawable icon){
        iconDrawable = icon;
    }

    public void setTitle(String title){
        titleStr = title;
    }

    public void setDesc(String desc){
        descStr = desc;
    }

    public Drawable getIcon(){
        return this.iconDrawable;
    }

    public String getTitle(){
        return this.titleStr;
    }

    public String getDesc(){
        return  this.descStr;
    }

    public String getUno() {
        return uno;
    }

    public void setUno(String uno) {
        this.uno = uno;
    }
}

