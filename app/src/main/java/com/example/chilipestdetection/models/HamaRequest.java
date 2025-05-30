package com.example.chilipestdetection.models;

import com.google.gson.annotations.SerializedName;
public class HamaRequest {
    @SerializedName("kode_hama")
    private String kodeHama;

    @SerializedName("nama_hama")
    private String namaHama;

    @SerializedName("type")
    private String type;

    public HamaRequest() {}

    public HamaRequest(String kodeHama, String namaHama, String type) {
        this.kodeHama = kodeHama;
        this.namaHama = namaHama;
        this.type = type;
    }

    public String getKodeHama() {
        return kodeHama;
    }

    public void setKodeHama(String kodeHama) {
        this.kodeHama = kodeHama;
    }

    public String getNamaHama() {
        return namaHama;
    }

    public void setNamaHama(String namaHama) {
        this.namaHama = namaHama;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
