package com.example.chilipestdetection.services;

import com.example.chilipestdetection.models.HamaResponse;
import com.example.chilipestdetection.models.LoginRequest;
import com.example.chilipestdetection.models.LoginResponse;
import com.example.chilipestdetection.models.PenangananResponse;
import com.example.chilipestdetection.models.RegisterRequest;
import com.example.chilipestdetection.models.RegisterResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @POST("exec")
    @FormUrlEncoded
    Call<LoginResponse> login(@FieldMap Map<String, String> fields);

    @POST("exec")
    @FormUrlEncoded
    Call<RegisterResponse> register(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("exec")
    Call<HamaResponse> readHama(@Field("action") String action);

    @FormUrlEncoded
    @POST("exec")
    Call<HamaResponse> createHama(
            @Field("action") String action,
            @Field("nama_hama") String namaHama,
            @Field("type") String type,
            @Field("pengendalian_rekomndasi") String pengendalianRekomndasi,
            @Field("pestisida_yang_disarankan") String pestisidaYangDisarankan,
            @Field("catatan_tambahan") String catatanTambahan
    );

    @FormUrlEncoded
    @POST("exec")
    Call<HamaResponse> updateHama(
            @Field("action") String action,
            @Field("kode_hama") String kodeHama,
            @Field("nama_hama") String namaHama,
            @Field("type") String type,
            @Field("pengendalian_rekomndasi") String pengendalianRekomndasi,
            @Field("pestisida_yang_disarankan") String pestisidaYangDisarankan,
            @Field("catatan_tambahan") String catatanTambahan
    );

    @FormUrlEncoded
    @POST("exec")
    Call<HamaResponse> deleteHama(
            @Field("action") String action,
            @Field("kode_hama") String kodeHama
    );

    @FormUrlEncoded
    @POST("exec")
    Call<PenangananResponse> readPenanganan(
            @Field("action") String action
    );

    @FormUrlEncoded
    @POST("exec")
    Call<PenangananResponse> createPenanganan(
            @Field("action") String action,
            @Field("tanaman") String tanaman,
            @Field("hama") String hama,
            @Field("gambar") String gambar,
            @Field("gejala") String gejala,
            @Field("aturan_fuzzy") String aturan_fuzzy
    );

    @FormUrlEncoded
    @POST("exec")
    Call<PenangananResponse> updatePenanganan(
            @Field("action") String action,
            @Field("kode_p_hama") String kode_p_hama,
            @Field("tanaman") String tanaman,
            @Field("hama") String hama,
            @Field("gambar") String gambar,
            @Field("gejala") String gejala,
            @Field("aturan_fuzzy") String aturan_fuzzy
    );

    @FormUrlEncoded
    @POST("exec")
    Call<PenangananResponse> deletePenanganan(
            @Field("action") String action,
            @Field("kode_p_hama") String kode_p_hama
    );
}