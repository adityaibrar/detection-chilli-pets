package com.example.chilipestdetection.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HamaResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private List<HamaRequest> data;

    @SerializedName("message")
    private String message;

    @SerializedName("kode_hama")
    private String kodeHama;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<HamaRequest> getData() {
        return data;
    }

    public void setData(List<HamaRequest> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKodeHama() {
        return kodeHama;
    }

    public void setKodeHama(String kodeHama) {
        this.kodeHama = kodeHama;
    }
}
