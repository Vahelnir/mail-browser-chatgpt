package org.example;

import com.theokanning.openai.*;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.CompletionResult;
import org.example.config.ChatGptConfig;
import org.example.exception.InvalidEmailTypeException;
import org.example.service.ChatGptService;

public class ChatGptClient implements ChatGptService {
    private final OpenAiService openAiService;

    public ChatGptClient() {
        ChatGptConfig config = new ChatGptConfig("config.properties");
        this.openAiService = new OpenAiService(config.getApiKey());
    }

    /**
     * Génère une réponse à partir des informations fournies.
     *
     * @param context         Le contexte de la conversation.
     * @param email           Le contenu de l'e-mail.
     * @param type            Le type de l'e-mail.
     * @param isConfirmed     Indique si la réunion est confirmée (uniquement pour le type "reunion").
     * @param alternativeDate La date alternative proposée (uniquement pour le type "reunion").
     * @param maxTokens       Le nombre maximum de mots ou de tokens à générer dans la réponse.
     *                        Plus la valeur est élevée, plus la réponse sera longue et détaillée.
     * @param temperature     Le paramètre de température contrôle le degré de "créativité" de la réponse générée.
     *                        Une valeur plus élevée (par exemple 1.0) rendra la réponse plus aléatoire et surprenante,
     *                        tandis qu'une valeur plus basse (par exemple 0.5) rendra la réponse plus précise et
     *                        prévisible.
     * @return La réponse générée par ChatGPT.
     * @throws Exception Si une erreur se produit lors de la génération de la réponse.
     */
    public String generateResponse(String context, String email, String type, Boolean isConfirmed, String alternativeDate, Integer maxTokens, Double temperature) throws Exception {
        String defaultContext = "Ton nom est Maxime,"; // Contexte par défaut
        if (context == null || context.isEmpty()) {
            context = defaultContext;
        }

        if (maxTokens == null) {
            maxTokens = 100;  // Valeur par défaut pour maxTokens
        }

        if (temperature == null) {
            temperature = 0.8; // Valeur par défaut pour temperature
        }

        String prompt = context;

        switch (type) {
            case "email" -> prompt += " Voici un e-mail à traiter : " + email +
                    "\nRépondez de manière bienveillante et professionnelle en français.";
            case "reunion" -> {
                if (isConfirmed == null) {
                    throw new IllegalArgumentException("isConfirmed must be provided for reunion type.");
                }
                if (isConfirmed) {
                    prompt += " Voici une demande de réunion à traiter : " + email +
                            "\nConfirmez la réunion de manière bienveillante et professionnelle en français.";
                } else {
                    prompt += " Voici une demande de réunion à traiter : " + email +
                            "\nNe pouvant pas confirmer la réunion, excusez-vous de manière professionnelle en français.";
                    if (alternativeDate != null) {
                        prompt += " Proposez une autre date : " + alternativeDate + ".";
                    }
                }
            }
            case "service" -> prompt += " Voici une demande de service à traiter : " + email +
                    "\nRépondez de manière bienveillante et professionnelle en français.";
            case "question" -> prompt += " Voici une question à traiter : " + email +
                    "\nRépondez de manière bienveillante et professionnelle en français.";
            default -> throw new InvalidEmailTypeException("Invalid type provided: " + type);
        }

        CompletionRequest request = CompletionRequest.builder()
                .prompt(prompt)
                .model("text-davinci-002")
                .maxTokens(maxTokens)
                .temperature(temperature)
                .build();

        CompletionResult completion = openAiService.createCompletion(request);
        String response = completion.getChoices().get(0).getText();

        // Ajout d'une signature indiquant que la réponse a été générée automatiquement
        response += "\n\n---\nCette réponse a été générée automatiquement. Si elle ne répond pas correctement à votre demande, veuillez nous en excuser et reformuler votre demande.";

        return response;
    }
}