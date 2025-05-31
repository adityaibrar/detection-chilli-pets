package com.example.chilipestdetection.models;

import java.util.List;

public class PenangananResponse {
    private boolean success;
    private List<PenangananData> data;
    private String message;
    private String kode_p_hama;

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public List<PenangananData> getData() { return data; }
    public void setData(List<PenangananData> data) { this.data = data; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getKode_p_hama() { return kode_p_hama; }
    public void setKode_p_hama(String kode_p_hama) { this.kode_p_hama = kode_p_hama; }
}

