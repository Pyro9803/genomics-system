package com.minhhn.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the result of executing a GATK command.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatkCommandResult {
    private boolean success;
    private String outputFile;
    private String errorMessage;
    private String standardOutput;
    private int exitCode;
    private long executionTimeMs;
    private GatkCommand command;
}
