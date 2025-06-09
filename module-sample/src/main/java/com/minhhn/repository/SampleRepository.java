package com.minhhn.repository;

import com.minhhn.model.Sample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SampleRepository extends JpaRepository<Sample, Long> {
    Optional<Sample> findBySampleCode(String sampleCode);
    boolean existsBySampleCode(String sampleCode);
    List<Sample> findByPatientId(Long patientId);
}
