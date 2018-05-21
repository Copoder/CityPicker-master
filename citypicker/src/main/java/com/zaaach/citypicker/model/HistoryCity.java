package com.zaaach.citypicker.model;

/**
 * Created by copo on 18-5-18.
 */

public class HistoryCity extends City{
    public HistoryCity(String name, String province, String code,String provinceId) {
        super(name, province, "历史", code,provinceId);
    }
}
