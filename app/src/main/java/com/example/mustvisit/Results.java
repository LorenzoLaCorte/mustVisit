package com.example.mustvisit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.mustvisit.GptChatApiService;
import com.example.mustvisit.GptChatApiService.GptChatApiCallback;
public class Results extends AppCompatActivity implements GptChatApiCallback {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Example usage of the GptChatApiService
        String query = "Hello, how are you?";
        GptChatApiService.queryGptChatApi(query, this);
    }

    @Override
    public void onResponse(String response) {
        // Handle the response from the API
        Log.d("GptChatApi", "Response: " + response);
    }

    @Override
    public void onFailure(Throwable throwable) {
        // Print the stack trace of the exception
        throwable.printStackTrace();

        // Handle the failure case
        Log.e("GptChatApi", "Request failed: " + throwable.getMessage());
    }

}