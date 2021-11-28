package com.example.iot;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JsonServer {
    @GET("data")
    Call<List<Post>> getPosts();
}
