package com.hcmus.mela.ai.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class ConversationMetadataDto {

    private String status;

    private Date createdAt;

    private Date updatedAt;
}
