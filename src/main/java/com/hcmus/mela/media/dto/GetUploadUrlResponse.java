package com.hcmus.mela.media.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetUploadUrlResponse {

    private String preSignedUrl;

    private String fileUrl;
}
