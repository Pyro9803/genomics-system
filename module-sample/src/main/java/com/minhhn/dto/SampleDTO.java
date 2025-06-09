package com.minhhn.dto;

import com.minhhn.enums.SampleStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SampleDTO {
    private Long id;
    private String sampleCode;
    private LocalDateTime collectedDate;
    private String sampleType;
    private SampleStatus status;
    private Long patientId;
    private String patientCode;
    private String patientName;
}
