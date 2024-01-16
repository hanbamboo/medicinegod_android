package com.daqin.medicinegod.entity;

public class Version {
    private double version;
    private String versionName;
    private String versionTips;

    @Override
    public String toString() {
        return "Version{" +
                "version=" + version +
                ", versionName='" + versionName + '\'' +
                ", versionTips='" + versionTips + '\'' +
                '}';
    }

    public Version() {
    }

    public Version(double version, String versionName, String versionTips) {
        this.version = version;
        this.versionName = versionName;
        this.versionTips = versionTips;
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getVersionTips() {
        return versionTips;
    }

    public void setVersionTips(String versionTips) {
        this.versionTips = versionTips;
    }
}
