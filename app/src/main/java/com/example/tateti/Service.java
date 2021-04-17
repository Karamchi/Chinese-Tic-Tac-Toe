package com.example.tateti;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;

public interface Service {
    @GET("room-members")
    Call<Map<String, List<String>>> groupList();

}
