package com.minhhn.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new Analysis.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAnalysisRequest {
    
    @NotNull(message = "Sample ID is required")
    private Long sampleId;
    
    @NotBlank(message = "Pipeline version is required")
    private String pipelineVersion;
    
    private String bamPath;
    
    // Optional reference genome path (if different from default)
    private String referenceGenomePath;
    
    // Optional interval list for targeted analysis
    private String intervalListPath;
}
