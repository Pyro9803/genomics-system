package com.minhhn.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents a GATK command to be executed.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatkCommand {
    private String toolName;
    private List<GatkCommandParameter> parameters;
    
    @Builder.Default
    private List<String> inputFiles = new ArrayList<>();
    
    @Builder.Default
    private String outputFile = "";
    
    @Builder.Default
    private String referenceGenome = "";
    
    /**
     * Builds the complete GATK command as a string.
     * 
     * @return The GATK command string ready to be executed
     */
    public String buildCommandString() {
        StringBuilder command = new StringBuilder();
        command.append(toolName);
        
        // Add reference genome if specified
        if (!referenceGenome.isEmpty()) {
            command.append(" -R ").append(referenceGenome);
        }
        
        // Add input files
        for (String inputFile : inputFiles) {
            command.append(" -I ").append(inputFile);
        }
        
        // Add output file if specified
        if (!outputFile.isEmpty()) {
            command.append(" -O ").append(outputFile);
        }
        
        // Add all other parameters
        if (parameters != null) {
            for (GatkCommandParameter param : parameters) {
                command.append(" ").append(param.toCommandString());
            }
        }
        
        return command.toString();
    }
    
    /**
     * Creates a map of environment variables needed for the command.
     * 
     * @return Map of environment variables
     */
    public Map<String, String> getEnvironmentVariables() {
        if (parameters == null) {
            return Map.of();
        }
        
        return parameters.stream()
                .filter(param -> param.isEnvironmentVariable())
                .collect(Collectors.toMap(
                        GatkCommandParameter::getName,
                        GatkCommandParameter::getValue
                ));
    }
}
