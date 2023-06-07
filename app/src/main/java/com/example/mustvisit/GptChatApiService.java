package com.example.mustvisit;

import android.os.AsyncTask;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import java.util.Collections;

public class GptChatApiService {
    private static final String MODEL_NAME = "gpt-3.5-turbo";
    private static final String API_KEY = "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";

    public static void queryChatGPT(String query, final ChatGPTResponseListener listener) {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... strings) {
                String query = strings[0];

                // Create an instance of OpenAiService
                OpenAiService openAiService = new OpenAiService(API_KEY);

                // Create a ChatCompletionRequest with your query and model name
                ChatCompletionRequest completionRequest = new ChatCompletionRequest();
                completionRequest.setModel(MODEL_NAME);

                ChatMessage message = new ChatMessage("user", query);
                completionRequest.setMessages(Collections.singletonList(message));

                try {
                    // Call the chat completion API
                    ChatCompletionResult completionResult = openAiService.createChatCompletion(completionRequest);

                    // Get the response from the completion
                    String response = completionResult.getChoices().get(0).getMessage().getContent().trim();

                    return response;
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error: " + e.getMessage();
                }
            }

            @Override
            protected void onPostExecute(String response) {
                if (listener != null) {
                    listener.onResponseReceived(response);
                }
            }
        }.execute(query);
    }

    public interface ChatGPTResponseListener {
        void onResponseReceived(String response);
    }
}
