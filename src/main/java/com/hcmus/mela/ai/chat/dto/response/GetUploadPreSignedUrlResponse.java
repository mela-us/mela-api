package com.hcmus.mela.ai.chat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUploadPreSignedUrlResponse {

    private String preSignedUrl;

    private String fileUrl;
}
