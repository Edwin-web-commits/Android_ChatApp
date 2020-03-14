package com.example.gochat.Fragments;

import com.example.gochat.Notifications.MyResponse;
import com.example.gochat.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(

            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAApRCAxzw:APA91bFlygVrdYL1UI81xzZDX18qWtaKNvBUwUA9AEGekGoxqPTYDYzcedfJGw-XqS6ftX2D8edHvEZ8nNbi77fpogTpoC8enjBEj3lrVvvEsnfNUsEQDubQR7A3vj48FwEMWyQoE2Vk"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
