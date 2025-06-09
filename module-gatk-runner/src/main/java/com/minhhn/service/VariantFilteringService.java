package com.minhhn.service;

import com.minhhn.model.GatkCommand;
import com.minhhn.model.GatkCommandParameter;
import com.minhhn.model.GatkCommandResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for filtering and annotating variants using GATK tools.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VariantFilteringService {

    private final GatkRunnerService gatkRunnerService;
    
    @Value("${gatk.reference.genome:/data/reference/Homo_sapiens_assembly38.fasta}")
    private String referenceGenome;
    
    /**
     * Filters variants based on standard quality filters.
     * 
     * @param inputVcf Path to the input VCF file
     * @param outputVcf Path to the output filtered VCF file
     * @return The result of the variant filtering operation
     */
    public GatkCommandResult filterVariants(String inputVcf, String outputVcf) {
        log.info("Starting variant filtering for VCF file: {}", inputVcf);
        
        List<GatkCommandParameter> parameters = new ArrayList<>();
        
        // Add filter expressions
        parameters.add(GatkCommandParameter.builder()
                .name("--filter-expression")
                .value("\"QD < 2.0\"")
                .flag(false)
                .build());
                
        parameters.add(GatkCommandParameter.builder()
                .name("--filter-name")
                .value("\"QD2\"")
                .flag(false)
                .build());
                
        parameters.add(GatkCommandParameter.builder()
                .name("--filter-expression")
                .value("\"FS > 60.0\"")
                .flag(false)
                .build());
                
        parameters.add(GatkCommandParameter.builder()
                .name("--filter-name")
                .value("\"FS60\"")
                .flag(false)
                .build());
                
        parameters.add(GatkCommandParameter.builder()
                .name("--filter-expression")
                .value("\"MQ < 40.0\"")
                .flag(false)
                .build());
                
        parameters.add(GatkCommandParameter.builder()
                .name("--filter-name")
                .value("\"MQ40\"")
                .flag(false)
                .build());
        
        // Build the GATK command
        GatkCommand command = GatkCommand.builder()
                .toolName("VariantFiltration")
                .referenceGenome(referenceGenome)
                .inputFiles(List.of(inputVcf))
                .outputFile(outputVcf)
                .parameters(parameters)
                .build();
        
        // Execute the command
        GatkCommandResult result = gatkRunnerService.executeCommand(command);
        
        if (result.isSuccess()) {
            log.info("Variant filtering completed successfully. Output VCF: {}", outputVcf);
        } else {
            log.error("Variant filtering failed: {}", result.getErrorMessage());
        }
        
        return result;
    }
    
    /**
     * Annotates variants with functional information using GATK Funcotator.
     * 
     * @param inputVcf Path to the input VCF file
     * @param outputVcf Path to the output annotated VCF file
     * @param dataSourcesPath Path to the Funcotator data sources
     * @return The result of the variant annotation operation
     */
    public GatkCommandResult annotateVariants(String inputVcf, String outputVcf, String dataSourcesPath) {
        log.info("Starting variant annotation for VCF file: {}", inputVcf);
        
        List<GatkCommandParameter> parameters = new ArrayList<>();
        
        // Add Funcotator parameters
        parameters.add(GatkCommandParameter.builder()
                .name("--data-sources-path")
                .value(dataSourcesPath)
                .flag(false)
                .build());
                
        parameters.add(GatkCommandParameter.builder()
                .name("--ref-version")
                .value("hg38")
                .flag(false)
                .build());
                
        parameters.add(GatkCommandParameter.builder()
                .name("--output-file-format")
                .value("VCF")
                .flag(false)
                .build());
        
        // Build the GATK command
        GatkCommand command = GatkCommand.builder()
                .toolName("Funcotator")
                .referenceGenome(referenceGenome)
                .inputFiles(List.of(inputVcf))
                .outputFile(outputVcf)
                .parameters(parameters)
                .build();
        
        // Execute the command
        GatkCommandResult result = gatkRunnerService.executeCommand(command);
        
        if (result.isSuccess()) {
            log.info("Variant annotation completed successfully. Output VCF: {}", outputVcf);
        } else {
            log.error("Variant annotation failed: {}", result.getErrorMessage());
        }
        
        return result;
    }
    
    /**
     * Selects specific variants by type (SNP, INDEL) from a VCF file.
     * 
     * @param inputVcf Path to the input VCF file
     * @param outputVcf Path to the output VCF file
     * @param variantType Type of variants to select (SNP or INDEL)
     * @return The result of the variant selection operation
     */
    public GatkCommandResult selectVariantsByType(String inputVcf, String outputVcf, String variantType) {
        log.info("Selecting {} variants from VCF file: {}", variantType, inputVcf);
        
        List<GatkCommandParameter> parameters = new ArrayList<>();
        
        // Add select type parameter
        parameters.add(GatkCommandParameter.builder()
                .name("--select-type")
                .value(variantType)
                .flag(false)
                .build());
        
        // Build the GATK command
        GatkCommand command = GatkCommand.builder()
                .toolName("SelectVariants")
                .referenceGenome(referenceGenome)
                .inputFiles(List.of(inputVcf))
                .outputFile(outputVcf)
                .parameters(parameters)
                .build();
        
        // Execute the command
        GatkCommandResult result = gatkRunnerService.executeCommand(command);
        
        if (result.isSuccess()) {
            log.info("Variant selection completed successfully. Output VCF: {}", outputVcf);
        } else {
            log.error("Variant selection failed: {}", result.getErrorMessage());
        }
        
        return result;
    }
}
