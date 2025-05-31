package com.example.chilipestdetection.models;

public class PenangananRequest {
    private String action;
    private String tanaman;
    private String hama;
    private String gambar;
    private String gejala;
    private String aturan_fuzzy;

    public PenangananRequest() {}

    public PenangananRequest(String action, String tanaman, String hama, String gambar, String gejala, String aturan_fuzzy) {
        this.action = action;
        this.tanaman = tanaman;
        this.hama = hama;
        this.gambar = gambar;
        this.gejala = gejala;
        this.aturan_fuzzy = aturan_fuzzy;
    }

    // Getters and Setters
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getTanaman() { return tanaman; }
    public void setTanaman(String tanaman) { this.tanaman = tanaman; }

    public String getHama() { return hama; }
    public void setHama(String hama) { this.hama = hama; }

    public String getGambar() { return gambar; }
    public void setGambar(String gambar) { this.gambar = gambar; }

    public String getGejala() { return gejala; }
    public void setGejala(String gejala) { this.gejala = gejala; }

    public String getAturan_fuzzy() { return aturan_fuzzy; }
    public void setAturan_fuzzy(String aturan_fuzzy) { this.aturan_fuzzy = aturan_fuzzy; }
}

