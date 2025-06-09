package com.minhhn.dto;

import com.minhhn.enums.AnalysisStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Analysis entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisDTO {
    private Long id;
    private String pipelineVersion;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private AnalysisStatus status;
    private String vcfPath;
    private String bamPath;
    private String logPath;
    private String resultSummary;
    
    // Sample information
    private Long sampleId;
    private String sampleCode;
    private String sampleType;
}
