package com.hcmus.mela.ai.client.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class AzureRequestBody {

    private String model;

    private List<AzureMessage> messages;
}
