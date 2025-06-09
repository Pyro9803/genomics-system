package com.minhhn.dto;

import com.minhhn.enums.SampleStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSampleRequest {
    private LocalDateTime collectedDate;
    private String sampleType;
    private SampleStatus status;
}
