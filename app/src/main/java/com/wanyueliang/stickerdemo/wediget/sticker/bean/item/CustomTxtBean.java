package com.wanyueliang.stickerdemo.wediget.sticker.bean.item;

public class CustomTxtBean {

    public String fontFamilyId;
    public String align;//自定义文字 对齐方式,左:0,中间：1，右：2
    public String color;//自定义文字颜色 00000,f6de48
    public String alpha;//0-1，保留小数点后两位，0为全透明，1为不透明
    public String appearKindId;//自定义文字出场方式Id包含在哪个类下面（编辑的时候，默认选中该类型）
    public String appearId;//自定义文字出场方式Id
}
