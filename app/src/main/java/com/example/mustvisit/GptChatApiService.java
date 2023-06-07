package com.example.mustvisit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GptChatApiService {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "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";
    private static final int MAX_TOKENS = 500;
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    public interface GptChatApiCallback {
        void onResponse(String response);

        void onFailure(Throwable throwable);
    }

    public static void queryGptChatApi(final String query, final GptChatApiCallback callback) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();

                JSONObject requestBody = new JSONObject();
                try {
                    requestBody.put("prompt", query);
                    requestBody.put("max_tokens", MAX_TOKENS); // Adjust max_tokens as needed
                } catch (JSONException e) {
                    callback.onFailure(e);
                    return;
                }

                Request request = new Request.Builder()
                        .url(API_URL)
                        .addHeader("Authorization", "Bearer " + API_KEY)
                        .post(RequestBody.create(JSON_MEDIA_TYPE, requestBody.toString()))
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                String responseBody = response.body().string();
                                JSONObject jsonResponse = new JSONObject(responseBody);
                                String completions = jsonResponse.getJSONArray("choices")
                                        .getJSONObject(0)
                                        .getString("text");
                                callback.onResponse(completions);
                            } catch (JSONException e) {
                                callback.onFailure(e);
                            }
                        } else {
                            callback.onFailure(new Exception("Request failed with response code: " + response.code()));
                        }
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        callback.onFailure(e);
                    }
                });
            }
        });
    }
}
