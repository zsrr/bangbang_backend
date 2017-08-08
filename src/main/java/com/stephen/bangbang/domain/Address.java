package com.stephen.bangbang.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Address {
    protected String province;
    protected String city;

    public Address(String province, String city) {
        this.province = province;
        this.city = city;
    }

    @Column(length = 8)
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    @Column(length = 12)
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Address() {
    }
}
