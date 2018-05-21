package com.zaaach.citypicker.model;

public class LocatedCity extends City {

    public LocatedCity(String name, String province, String code,String provinceId) {
        super(name, province, "定位", code,provinceId);
    }
}
