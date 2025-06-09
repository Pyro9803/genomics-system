package com.minhhn.controller;

import com.minhhn.dto.AnalysisDTO;
import com.minhhn.dto.CreateAnalysisRequest;
import com.minhhn.service.AnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing genomic analyses.
 */
@RestController
@RequestMapping("/api/analyses")
@RequiredArgsConstructor
@Slf4j
public class AnalysisController {

    private final AnalysisService analysisService;
    
    /**
     * Creates a new analysis.
     * 
     * @param request The analysis creation request
     * @return The created analysis
     */
    @PostMapping
    public ResponseEntity<AnalysisDTO> createAnalysis(@Valid @RequestBody CreateAnalysisRequest request) {
        log.info("REST request to create a new analysis for sample ID: {}", request.getSampleId());
        AnalysisDTO result = analysisService.createAnalysis(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
    
    /**
     * Gets all analyses.
     * 
     * @return List of all analyses
     */
    @GetMapping
    public ResponseEntity<List<AnalysisDTO>> getAllAnalyses() {
        log.info("REST request to get all analyses");
        List<AnalysisDTO> analyses = analysisService.getAllAnalyses();
        return ResponseEntity.ok(analyses);
    }
    
    /**
     * Gets an analysis by ID.
     * 
     * @param id The analysis ID
     * @return The analysis
     */
    @GetMapping("/{id}")
    public ResponseEntity<AnalysisDTO> getAnalysisById(@PathVariable Long id) {
        log.info("REST request to get analysis with ID: {}", id);
        AnalysisDTO analysis = analysisService.getAnalysisById(id);
        return ResponseEntity.ok(analysis);
    }
    
    /**
     * Gets all analyses for a sample.
     * 
     * @param sampleId The sample ID
     * @return List of analyses for the sample
     */
    @GetMapping("/sample/{sampleId}")
    public ResponseEntity<List<AnalysisDTO>> getAnalysesBySampleId(@PathVariable Long sampleId) {
        log.info("REST request to get all analyses for sample ID: {}", sampleId);
        List<AnalysisDTO> analyses = analysisService.getAnalysesBySampleId(sampleId);
        return ResponseEntity.ok(analyses);
    }
}
