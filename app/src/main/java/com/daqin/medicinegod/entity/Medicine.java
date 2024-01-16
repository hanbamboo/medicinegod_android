package com.daqin.medicinegod.entity;


public class Medicine {
    private String keyid;
    private String name;
    private byte[] img;
    private String desp;
    private long outdate;
    private String otc;
    private String barcode;
    private String yu;
    private String elabel;
    private String love;
    private String share;
    private String muse;
    private String company;
    private int delfalg;

    public int getDelfalg() {
        return delfalg;
    }

    public void setDelfalg(int delfalg) {
        this.delfalg = delfalg;
    }

    public String getKeyid() {
        return keyid;
    }

    public void setKeyid(String keyid) {
        this.keyid = keyid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImg() {
        return img;
    }

    public void setImg(byte[] img) {
        this.img = img;
    }

    public String getDesp() {
        return desp;
    }

    public void setDesp(String desp) {
        this.desp = desp;
    }

    public long getOutdate() {
        return outdate;
    }

    public void setOutdate(long outdate) {
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

    public String getLove() {
        return love;
    }

    public void setLove(String love) {
        this.love = love;
    }

    public String getShare() {
        return share;
    }

    public void setShare(String share) {
        this.share = share;
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

    public Medicine(String keyid, String name, byte[] img, String desp, long outdate, String otc, String barcode, String yu, String elabel, String love, String share, String muse, String company) {
        this.keyid = keyid;
        this.name = name;
        this.img = img;
        this.desp = desp;
        this.outdate = outdate;
        this.otc = otc;
        this.barcode = barcode;
        this.yu = yu;
        this.elabel = elabel;
        this.love = love;
        this.share = share;
        this.muse = muse;
        this.company = company;
    }
}
