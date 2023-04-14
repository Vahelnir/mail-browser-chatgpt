package chatgpt;

import com.theokanning.openai.*;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;

import javax.annotation.processing.Completion;

public class ChatGPTClient {
    private final String apiKey;
    private final OpenAiService openAiService;

    public ChatGPTClient(String apiKey) {
        this.apiKey = apiKey;
        this.openAiService = new OpenAiService(apiKey);
    }

    public String generateResponse(String email, String type, Boolean isConfirmed, String alternativeDate) throws Exception {
        String prompt;
        if (type.equals("email")) {
            prompt = "Ton nom est Maxime, voici un e-mail à traiter : " + email +
                    "\nRépondez de manière bienveillante et professionnelle en français.";
        } else if (type.equals("reunion")) {
            if (isConfirmed == null) {
                throw new IllegalArgumentException("isConfirmed must be provided for reunion type.");
            }
            if (isConfirmed) {
                prompt = "Ton nom est Maxime, voici une demande de réunion à traiter : " + email +
                        "\nConfirmez la réunion de manière bienveillante et professionnelle en français.";
            } else {
                prompt = "Ton nom est Maxime, voici une demande de réunion à traiter : " + email +
                        "\nNe pouvant pas confirmer la réunion, excusez-vous de manière professionnelle en français.";
                if (alternativeDate != null) {
                    prompt += " Proposez une autre date : " + alternativeDate + ".";
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid type provided.");
        }

        CompletionRequest request = CompletionRequest.builder()
                .prompt(prompt)
                .model("text-davinci-002")
                .maxTokens(100)
                .temperature(0.8)
                .build();

        CompletionResult completion = openAiService.createCompletion(request);
        return completion.getChoices().get(0).getText();
    }
}