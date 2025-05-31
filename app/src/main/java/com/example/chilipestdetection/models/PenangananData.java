package com.example.chilipestdetection.models;

import android.os.Parcel;
import android.os.Parcelable;

public class PenangananData implements Parcelable {
    private String kode_p_hama;
    private String tanaman;
    private String hama;
    private String gambar;
    private String gejala;
    private double aturan_fuzzy;

    // Empty constructor
    public PenangananData() {
    }

    // Parcelable constructor
    protected PenangananData(Parcel in) {
        kode_p_hama = in.readString();
        tanaman = in.readString();
        hama = in.readString();
        gambar = in.readString();
        gejala = in.readString();
        aturan_fuzzy = in.readDouble();
    }

    public static final Creator<PenangananData> CREATOR = new Creator<PenangananData>() {
        @Override
        public PenangananData createFromParcel(Parcel in) {
            return new PenangananData(in);
        }

        @Override
        public PenangananData[] newArray(int size) {
            return new PenangananData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(kode_p_hama);
        dest.writeString(tanaman);
        dest.writeString(hama);
        dest.writeString(gambar);
        dest.writeString(gejala);
        dest.writeDouble(aturan_fuzzy);
    }

    // Getters and Setters
    public String getKode_p_hama() { return kode_p_hama; }
    public void setKode_p_hama(String kode_p_hama) { this.kode_p_hama = kode_p_hama; }

    public String getTanaman() { return tanaman; }
    public void setTanaman(String tanaman) { this.tanaman = tanaman; }

    public String getHama() { return hama; }
    public void setHama(String hama) { this.hama = hama; }

    public String getGambar() { return gambar; }
    public void setGambar(String gambar) { this.gambar = gambar; }

    public String getGejala() { return gejala; }
    public void setGejala(String gejala) { this.gejala = gejala; }

    public double getAturan_fuzzy() { return aturan_fuzzy; }
    public void setAturan_fuzzy(double aturan_fuzzy) { this.aturan_fuzzy = aturan_fuzzy; }
}