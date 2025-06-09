package com.minhhn.service;

import com.minhhn.model.GatkCommand;
import com.minhhn.model.GatkCommandResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Service for executing GATK commands using Docker.
 */
@Service
@Slf4j
public class GatkRunnerService {

    @Value("${gatk.docker.image:broadinstitute/gatk:4.5.0.0}")
    private String gatkDockerImage;

    @Value("${gatk.data.volume:/data}")
    private String gatkDataVolume;

    /**
     * Executes a GATK command using Docker.
     *
     * @param command The GATK command to execute
     * @return The result of the command execution
     */
    public GatkCommandResult executeCommand(GatkCommand command) {
        long startTime = System.currentTimeMillis();
        
        List<String> dockerCommand = buildDockerCommand(command);
        ProcessBuilder processBuilder = new ProcessBuilder(dockerCommand);
        
        StringBuilder output = new StringBuilder();
        StringBuilder error = new StringBuilder();
        int exitCode = -1;
        
        try {
            log.info("Executing GATK command: {}", command.buildCommandString());
            Process process = processBuilder.start();
            
            // Read output and error streams
            try (BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                
                String line;
                while ((line = outputReader.readLine()) != null) {
                    output.append(line).append(System.lineSeparator());
                }
                
                while ((line = errorReader.readLine()) != null) {
                    error.append(line).append(System.lineSeparator());
                }
            }
            
            // Wait for the process to complete
            boolean completed = process.waitFor(30, TimeUnit.MINUTES);
            if (!completed) {
                process.destroyForcibly();
                return GatkCommandResult.builder()
                        .success(false)
                        .errorMessage("Command execution timed out after 30 minutes")
                        .command(command)
                        .executionTimeMs(System.currentTimeMillis() - startTime)
                        .build();
            }
            
            exitCode = process.exitValue();
            boolean success = exitCode == 0;
            
            long endTime = System.currentTimeMillis();
            
            return GatkCommandResult.builder()
                    .success(success)
                    .outputFile(command.getOutputFile())
                    .standardOutput(output.toString())
                    .errorMessage(error.toString())
                    .exitCode(exitCode)
                    .executionTimeMs(endTime - startTime)
                    .command(command)
                    .build();
            
        } catch (IOException | InterruptedException e) {
            log.error("Error executing GATK command", e);
            return GatkCommandResult.builder()
                    .success(false)
                    .errorMessage("Error executing command: " + e.getMessage())
                    .command(command)
                    .executionTimeMs(System.currentTimeMillis() - startTime)
                    .build();
        }
    }
    
    /**
     * Builds a Docker command to execute the GATK command.
     *
     * @param command The GATK command to execute
     * @return The Docker command as a list of strings
     */
    private List<String> buildDockerCommand(GatkCommand command) {
        List<String> dockerCommand = new ArrayList<>();
        dockerCommand.add("docker");
        dockerCommand.add("run");
        dockerCommand.add("--rm");
        
        // Mount the data volume
        dockerCommand.add("-v");
        dockerCommand.add("./data:" + gatkDataVolume);
        
        // Set working directory
        dockerCommand.add("-w");
        dockerCommand.add(gatkDataVolume);
        
        // Add environment variables if any
        command.getEnvironmentVariables().forEach((key, value) -> {
            dockerCommand.add("-e");
            dockerCommand.add(key + "=" + value);
        });
        
        // Add the GATK Docker image
        dockerCommand.add(gatkDockerImage);
        
        // Add the GATK command
        dockerCommand.add("gatk");
        dockerCommand.add(command.buildCommandString());
        
        return dockerCommand;
    }
}
