package com.minhhn.service;

import com.minhhn.dto.AnalysisDTO;
import com.minhhn.dto.CreateAnalysisRequest;
import com.minhhn.enums.AnalysisStatus;
import com.minhhn.mapper.AnalysisMapper;
import com.minhhn.model.Analysis;
import com.minhhn.model.GatkCommand;
import com.minhhn.model.GatkCommandResult;
import com.minhhn.repository.AnalysisRepository;
import com.minhhn.service.VariantCallingService;
import com.minhhn.service.VariantFilteringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for managing genomic analyses.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AnalysisService {

    private final AnalysisRepository analysisRepository;
    private final AnalysisMapper analysisMapper;
    private final com.minhhn.repository.SampleRepository sampleRepository;
    private final VariantCallingService variantCallingService;
    private final VariantFilteringService variantFilteringService;
    
    @Value("${analysis.output.directory:/data/output}")
    private String outputDirectory;
    
    @Value("${analysis.reference.genome:/data/reference/Homo_sapiens_assembly38.fasta}")
    private String defaultReferenceGenome;
    
    @Value("${analysis.funcotator.datasources:/data/funcotator_dataSources}")
    private String funcotatorDataSources;
    
    /**
     * Creates a new analysis and starts it asynchronously.
     * 
     * @param request The analysis creation request
     * @return The created analysis DTO
     */
    @Transactional
    public AnalysisDTO createAnalysis(CreateAnalysisRequest request) {
        log.info("Creating new analysis for sample ID: {}", request.getSampleId());
        
        // Verify sample exists
        var sample = sampleRepository.findById(request.getSampleId())
                .orElseThrow(() -> new RuntimeException("Sample not found with ID: " + request.getSampleId()));
        
        // Create analysis entity
        Analysis analysis = analysisMapper.toEntity(request);
        analysis.setSample(sample);
        analysis.setStatus(AnalysisStatus.PENDING);
        analysis.setStartedAt(LocalDateTime.now());
        
        // Generate unique output paths
        String analysisId = UUID.randomUUID().toString().substring(0, 8);
        String sampleCode = sample.getSampleCode();
        
        Path outputPath = Paths.get(outputDirectory, sampleCode, analysisId);
        String vcfPath = outputPath.resolve(sampleCode + ".vcf").toString();
        String logPath = outputPath.resolve(sampleCode + ".log").toString();
        
        analysis.setVcfPath(vcfPath);
        analysis.setLogPath(logPath);
        
        // Use provided BAM path or default from sample
        if (request.getBamPath() == null || request.getBamPath().isEmpty()) {
            // In a real system, you would have a field in Sample entity for the BAM file path
            // For now, we'll use a placeholder
            analysis.setBamPath("/data/samples/" + sampleCode + "/" + sampleCode + ".bam");
        } else {
            analysis.setBamPath(request.getBamPath());
        }
        
        // Save the analysis
        Analysis savedAnalysis = analysisRepository.save(analysis);
        
        // Start the analysis asynchronously
        startAnalysis(savedAnalysis.getId(), request.getReferenceGenomePath(), request.getIntervalListPath());
        
        return analysisMapper.toDto(savedAnalysis);
    }
    
    /**
     * Starts the analysis process asynchronously.
     * 
     * @param analysisId The ID of the analysis to start
     * @param referenceGenomePath Optional custom reference genome path
     * @param intervalListPath Optional interval list path for targeted analysis
     */
    @Async
    public void startAnalysis(Long analysisId, String referenceGenomePath, String intervalListPath) {
        log.info("Starting analysis with ID: {}", analysisId);
        
        try {
            // Get the analysis
            Analysis analysis = analysisRepository.findById(analysisId)
                    .orElseThrow(() -> new RuntimeException("Analysis not found with ID: " + analysisId));
            
            // Update status to RUNNING
            analysis.setStatus(AnalysisStatus.RUNNING);
            analysisRepository.save(analysis);
            
            // Use provided reference genome or default
            String referenceGenome = (referenceGenomePath != null && !referenceGenomePath.isEmpty()) 
                    ? referenceGenomePath : defaultReferenceGenome;
            
            // Extract paths
            String bamPath = analysis.getBamPath();
            String sampleCode = analysis.getSample().getSampleCode();
            Path outputPath = Paths.get(analysis.getVcfPath()).getParent();
            
            // Generate intermediate file paths
            String rawVcfPath = outputPath.resolve(sampleCode + ".raw.vcf").toString();
            String filteredVcfPath = outputPath.resolve(sampleCode + ".filtered.vcf").toString();
            String finalVcfPath = analysis.getVcfPath();
            
            // Step 1: Call variants
            GatkCommandResult variantResult = variantCallingService.callVariants(
                    bamPath, rawVcfPath, intervalListPath);
            
            if (!variantResult.isSuccess()) {
                failAnalysis(analysis, "Variant calling failed: " + variantResult.getErrorMessage());
                return;
            }
            
            // Step 2: Filter variants
            GatkCommandResult filterResult = variantFilteringService.filterVariants(
                    rawVcfPath, filteredVcfPath);
            
            if (!filterResult.isSuccess()) {
                failAnalysis(analysis, "Variant filtering failed: " + filterResult.getErrorMessage());
                return;
            }
            
            // Step 3: Annotate variants
            GatkCommandResult annotationResult = variantFilteringService.annotateVariants(
                    filteredVcfPath, finalVcfPath, funcotatorDataSources);
            
            if (!annotationResult.isSuccess()) {
                failAnalysis(analysis, "Variant annotation failed: " + annotationResult.getErrorMessage());
                return;
            }
            
            // Complete the analysis
            analysis.setStatus(AnalysisStatus.DONE);
            analysis.setFinishedAt(LocalDateTime.now());
            analysis.setResultSummary("Analysis completed successfully. Found variants in " + finalVcfPath);
            analysisRepository.save(analysis);
            
            log.info("Analysis completed successfully for ID: {}", analysisId);
            
        } catch (Exception e) {
            log.error("Error during analysis execution", e);
            
            // Get the analysis again in case it was modified
            analysisRepository.findById(analysisId).ifPresent(analysis -> {
                failAnalysis(analysis, "Analysis failed with error: " + e.getMessage());
            });
        }
    }
    
    /**
     * Marks an analysis as failed with the given error message.
     * 
     * @param analysis The analysis to mark as failed
     * @param errorMessage The error message
     */
    private void failAnalysis(Analysis analysis, String errorMessage) {
        analysis.setStatus(AnalysisStatus.FAILED);
        analysis.setFinishedAt(LocalDateTime.now());
        analysis.setResultSummary(errorMessage);
        analysisRepository.save(analysis);
        log.error("Analysis failed for ID: {}. Reason: {}", analysis.getId(), errorMessage);
    }
    
    /**
     * Gets all analyses.
     * 
     * @return List of all analyses DTOs
     */
    @Transactional(readOnly = true)
    public List<AnalysisDTO> getAllAnalyses() {
        return analysisRepository.findAll().stream()
                .map(analysisMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Gets an analysis by ID.
     * 
     * @param id The analysis ID
     * @return The analysis DTO
     */
    @Transactional(readOnly = true)
    public AnalysisDTO getAnalysisById(Long id) {
        return analysisRepository.findById(id)
                .map(analysisMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Analysis not found with ID: " + id));
    }
    
    /**
     * Gets all analyses for a sample.
     * 
     * @param sampleId The sample ID
     * @return List of analyses DTOs for the sample
     */
    @Transactional(readOnly = true)
    public List<AnalysisDTO> getAnalysesBySampleId(Long sampleId) {
        return analysisRepository.findBySampleId(sampleId).stream()
                .map(analysisMapper::toDto)
                .collect(Collectors.toList());
    }
}
