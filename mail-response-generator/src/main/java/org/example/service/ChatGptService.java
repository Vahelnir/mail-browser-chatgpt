package org.example.service;

public interface ChatGptService {
    String generateResponse(String context, String email, String type, Boolean isConfirmed, String alternativeDate, Integer maxTokens, Double temperature) throws Exception;
}