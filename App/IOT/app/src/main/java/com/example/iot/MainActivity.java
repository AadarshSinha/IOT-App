package com.example.iot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.net.URI;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private TextView humidity;
    private TextView temperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        humidity=findViewById(R.id.humidity);
        temperature=findViewById(R.id.temperature);
        humidity.setText("0");
        temperature.setText("0");

        Server();

    }
    protected void Server(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.11:1111/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        JsonServer jsonServer = retrofit.create(JsonServer.class);

        Call<List<Post>> call = jsonServer.getPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {

                if (!response.isSuccessful()) {
                    humidity.setText("0");
                    temperature.setText("0");
                    return;
                }

                List<Post> posts = response.body();
                float a=posts.get(0).getHumidity();
                float b=posts.get(0).getTemperature();
                String x=String.valueOf(a);
                String y=String.valueOf(b);
                x+=" %";
                y+=" C";
                humidity.setText(x);
                temperature.setText(y);
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
//                humidity.setText("0");
                temperature.setText("0");
                humidity.setText("0");
            }
        });
        refresh(1000);
    }
    protected void refresh(int ms)
    {
        final Handler handler=new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Server();
            }
        };
        handler.postDelayed(runnable,ms);
    }
}