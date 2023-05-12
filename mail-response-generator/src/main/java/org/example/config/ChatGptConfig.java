package org.example.config;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

public class ChatGptConfig {
    private final Properties properties = new Properties();

    public ChatGptConfig(String fileName) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (is != null) {
                properties.load(is);
            } else {
                throw new IOException("Cannot find configuration file " + fileName);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read configuration file " + fileName, e);
        }
    }

    public String getApiKey() {
        return properties.getProperty("openai.api.key");
    }
}