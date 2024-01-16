package com.daqin.medicinegod.entity;


import com.alibaba.excel.annotation.ExcelProperty;

public class MedicineTemplate {

    @ExcelProperty("药品名")
    private String name = "药品名示例";
    @ExcelProperty("药品图片")
    private String img = "请缩放到单元格内，无图片则填无";
    @ExcelProperty("药品描述")
    private String desp = "药品描述示例";
    @ExcelProperty("药品过期时间")
    private String outdate = "过期时间如：2020-07-01";
    @ExcelProperty("药品标识")
    private String otc = "如：非处方药-绿(红)底/处方药/无";
    @ExcelProperty("药品条码")
    private String barcode = "条码，必须为13位条码";
    @ExcelProperty("药品余量")
    private String yu = "余量，请保证必为数字，单位与用量中单位保持一致";
    @ExcelProperty("药品药效标签")
    private String elabel = "药效标签(如：感冒@@发烧)，多个请用@@分隔";
    @ExcelProperty("药品用法用量")
    private String muse = "如：1-包-3-次-1天 （代表一天三次一次一包，请自行修改单位）";
    @ExcelProperty("药品出产公司")
    private String company = "公司名示例";
    @ExcelProperty("药品分组")
    private String group = "如：家/学校等（请自行修改，填无代表默认）";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public String getOutdate() {
        return outdate;
    }

    public void setOutdate(String outdate) {
        this.outdate = outdate;
    }

    public String getOtc() {
        return otc;
    }

    public void setOtc(String otc) {
        this.otc = otc;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getYu() {
        return yu;
    }

    public void setYu(String yu) {
        this.yu = yu;
    }

    public String getElabel() {
        return elabel;
    }

    public void setElabel(String elabel) {
        this.elabel = elabel;
    }

    public String getMuse() {
        return muse;
    }

    public void setMuse(String muse) {
        this.muse = muse;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void clear() {
        this.name = "";
        this.img = "";
        this.barcode = "";
        this.company = "";
        this.desp = "";
        this.elabel = "";
        this.muse = "";
        this.yu = "";
        this.otc = "";
        this.group = "";
        this.outdate = "";
    }
}
