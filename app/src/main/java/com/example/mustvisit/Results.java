package com.example.mustvisit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Results extends AppCompatActivity implements GptChatApiService.ChatGPTResponseListener {

    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        query = "Tell me the 5 best beaches near this coordinates (44.38559, 9.012631) in the range of 20 kilometers. I want as response a numbered list without further descriptions. I want all elements in the list to contain {name} - {coordinates (x,y)} - {short description}.";
        GptChatApiService.queryChatGPT(query, this);
    }

    @Override
    public void onResponseReceived(String response) {
        Log.d("ChatGPT", "Response: " + response);
        TextView tv = findViewById(R.id.textView);
        tv.setText(response);
    }
}