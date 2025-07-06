package com.hcmus.mela.test.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class TestDto {

    private Integer total;

    private List<QuestionDto> questions;
}
