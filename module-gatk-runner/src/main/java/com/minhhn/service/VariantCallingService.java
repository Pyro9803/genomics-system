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
 * Service for performing variant calling using GATK HaplotypeCaller.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class VariantCallingService {

    private final GatkRunnerService gatkRunnerService;
    
    @Value("${gatk.reference.genome:/data/reference/Homo_sapiens_assembly38.fasta}")
    private String referenceGenome;
    
    /**
     * Performs variant calling on a BAM file using GATK HaplotypeCaller.
     * 
     * @param bamFile Path to the BAM file
     * @param outputVcf Path to the output VCF file
     * @param intervalList Optional interval list to restrict analysis
     * @return The result of the variant calling operation
     */
    public GatkCommandResult callVariants(String bamFile, String outputVcf, String intervalList) {
        log.info("Starting variant calling for BAM file: {}", bamFile);
        
        List<GatkCommandParameter> parameters = new ArrayList<>();
        
        // Add interval list if provided
        if (intervalList != null && !intervalList.isEmpty()) {
            parameters.add(GatkCommandParameter.builder()
                    .name("-L")
                    .value(intervalList)
                    .flag(false)
                    .build());
        }
        
        // Configure HaplotypeCaller parameters
        parameters.add(GatkCommandParameter.builder()
                .name("--emit-ref-confidence")
                .value("GVCF")
                .flag(false)
                .build());
                
        parameters.add(GatkCommandParameter.builder()
                .name("--pcr-indel-model")
                .value("NONE")
                .flag(false)
                .build());
                
        parameters.add(GatkCommandParameter.builder()
                .name("--standard-min-confidence-threshold-for-calling")
                .value("30")
                .flag(false)
                .build());
        
        // Build the GATK command
        GatkCommand command = GatkCommand.builder()
                .toolName("HaplotypeCaller")
                .referenceGenome(referenceGenome)
                .inputFiles(List.of(bamFile))
                .outputFile(outputVcf)
                .parameters(parameters)
                .build();
        
        // Execute the command
        GatkCommandResult result = gatkRunnerService.executeCommand(command);
        
        if (result.isSuccess()) {
            log.info("Variant calling completed successfully. Output VCF: {}", outputVcf);
        } else {
            log.error("Variant calling failed: {}", result.getErrorMessage());
        }
        
        return result;
    }
    
    /**
     * Performs joint genotyping on multiple GVCF files.
     * 
     * @param gvcfFiles List of GVCF files
     * @param outputVcf Path to the output VCF file
     * @return The result of the joint genotyping operation
     */
    public GatkCommandResult jointGenotyping(List<String> gvcfFiles, String outputVcf) {
        log.info("Starting joint genotyping for {} GVCF files", gvcfFiles.size());
        
        List<GatkCommandParameter> parameters = new ArrayList<>();
        
        // Add GVCF variant files
        for (String gvcfFile : gvcfFiles) {
            parameters.add(GatkCommandParameter.builder()
                    .name("-V")
                    .value(gvcfFile)
                    .flag(false)
                    .build());
        }
        
        // Build the GATK command
        GatkCommand command = GatkCommand.builder()
                .toolName("GenotypeGVCFs")
                .referenceGenome(referenceGenome)
                .outputFile(outputVcf)
                .parameters(parameters)
                .build();
        
        // Execute the command
        GatkCommandResult result = gatkRunnerService.executeCommand(command);
        
        if (result.isSuccess()) {
            log.info("Joint genotyping completed successfully. Output VCF: {}", outputVcf);
        } else {
            log.error("Joint genotyping failed: {}", result.getErrorMessage());
        }
        
        return result;
    }
}
