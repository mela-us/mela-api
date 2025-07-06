package com.hcmus.mela.ai.client.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AzureMessage {

    private String role;

    private Object content;
}
