package com.hcmus.mela.ai.client.config;

/**
 * Interface defining common properties for AI feature configurations.
 * Any AI feature in the application should implement this interface
 * to ensure consistency in configuration across different features.
 */
public interface AiFeatureProperties {

    String getModel();

    String getProvider();

    String getPath();
}
