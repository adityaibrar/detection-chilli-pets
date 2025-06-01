package com.example.chilipestdetection.models;

import com.google.gson.annotations.SerializedName;
public class HamaRequest {
    @SerializedName("kode_hama")
    private String kodeHama;

    @SerializedName("nama_hama")
    private String namaHama;

    @SerializedName("type")
    private String type;

    @SerializedName("pengendalian_rekomndasi")
    private String pengendalianRekomndasi;

    @SerializedName("pestisida_yang_disarankan")
    private String pestisidaYangDisarankan;

    @SerializedName("catatan_tambahan")
    private String catatanTambahan;

    public HamaRequest() {}

    public HamaRequest(String kodeHama, String namaHama, String type, String pengendalianRekomndasi, String pestisidaYangDisarankan, String catatanTambahan) {
        this.kodeHama = kodeHama;
        this.namaHama = namaHama;
        this.type = type;
        this.pengendalianRekomndasi = pengendalianRekomndasi;
        this.pestisidaYangDisarankan = pestisidaYangDisarankan;
        this.catatanTambahan = catatanTambahan;
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

    public String getPengendalianRekomndasi(){
        return pengendalianRekomndasi;
    }
    public void  setPengendalianRekomndasi(String pengendalianRekomndasi){
        this.pengendalianRekomndasi = pengendalianRekomndasi;
    }

    public String getPestisidaYangDisarankan(){
        return pestisidaYangDisarankan;
    }

    public void setPestisidaYangDisarankan(String pestisidaYangDisarankan){
        this.pestisidaYangDisarankan = pestisidaYangDisarankan;
    }

    public String getCatatanTambahan(){
        return catatanTambahan;
    }

    public void setCatatanTambahan(String catatanTambahan){
        this.catatanTambahan = catatanTambahan;
    }
}
