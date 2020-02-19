package com.example.bottomtest.ui.home.db;

import org.litepal.crud.DataSupport;

public class Province extends DataSupport {
    private  int id;
    private  String provinceName;
    private int provinceCode;

    public Province() {
    }
    public Province(Province province) {
        this.id = province.getId();
        this.provinceName = province.getProvinceName();
        this.provinceCode = province.getProvinceCode();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
