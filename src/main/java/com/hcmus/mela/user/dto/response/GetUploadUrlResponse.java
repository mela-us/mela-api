package com.hcmus.mela.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetUploadUrlResponse {

    private String preSignedUrl;

    private String imageUrl;
}
