package com.minhhn.repository;

import com.minhhn.enums.AnalysisStatus;
import com.minhhn.model.Analysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Analysis entity.
 */
@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    
    /**
     * Find analyses by sample ID.
     * 
     * @param sampleId The ID of the sample
     * @return List of analyses for the sample
     */
    List<Analysis> findBySampleId(Long sampleId);
    
    /**
     * Find analyses by status.
     * 
     * @param status The analysis status
     * @return List of analyses with the specified status
     */
    List<Analysis> findByStatus(AnalysisStatus status);
    
    /**
     * Find analyses by sample ID and status.
     * 
     * @param sampleId The ID of the sample
     * @param status The analysis status
     * @return List of analyses for the sample with the specified status
     */
    List<Analysis> findBySampleIdAndStatus(Long sampleId, AnalysisStatus status);
}
