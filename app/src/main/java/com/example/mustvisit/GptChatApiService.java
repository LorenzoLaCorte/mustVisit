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

    public static void queryChatGPT(TopPlaces topPlaces, final ChatGPTResponseListener listener) {
        new AsyncTask<String, Void, TopPlaces>() {

            @Override
            protected TopPlaces doInBackground(String... input) {
                String query = input[0];
                int retries = Integer.parseInt(input[1]);

                Category category = topPlaces.category;

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

                    return new TopPlaces(category, query, response, retries);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new TopPlaces(category, query, null, retries);
                }
            }

            @Override
            protected void onPostExecute(TopPlaces topPlaces) {
                if (listener != null) {
                    if (topPlaces.response != null) {
                        listener.onResponseReceived(topPlaces);
                    } else {
                        // Handle the error case
                        listener.onError(topPlaces);
                    }
                }
            }
        }.execute(topPlaces.query, topPlaces.retries+"");
    }

    public interface ChatGPTResponseListener {
        void onResponseReceived(TopPlaces topPlaces);
        void onError(TopPlaces topPlaces);
    }
}
