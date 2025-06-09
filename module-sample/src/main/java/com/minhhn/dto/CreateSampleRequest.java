package com.minhhn.dto;

import com.minhhn.enums.SampleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateSampleRequest {
    @NotBlank(message = "Sample code is required")
    private String sampleCode;
    
    private LocalDateTime collectedDate;
    
    @NotBlank(message = "Sample type is required")
    private String sampleType;
    
    private SampleStatus status = SampleStatus.NEW;
    
    @NotNull(message = "Patient ID is required")
    private Long patientId;
}
