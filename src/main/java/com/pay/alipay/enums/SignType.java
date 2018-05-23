package com.pay.alipay.enums;

/**
 * 签名方式
 * Author: czb
 */
public enum SignType {

    MD5("MD5"),

    DSA("DSA"),

    RSA("RSA");

    private String value;

    private SignType(String value){
        this.value = value;
    }

    public String value(){
        return value;
    }
}
