package com.hcmus.mela.review.dto.response;

import com.hcmus.mela.review.dto.dto.ReviewDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetReviewsResponse {

    private String message;

    private List<ReviewDto> reviews;
}
