package com.minhhn.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a parameter for a GATK command.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatkCommandParameter {
    private String name;
    private String value;
    private boolean flag;
    private boolean environmentVariable;
    
    /**
     * Converts the parameter to a command-line string format.
     * 
     * @return The parameter as a command-line string
     */
    public String toCommandString() {
        if (flag) {
            return name;
        } else {
            return name + " " + value;
        }
    }
}
