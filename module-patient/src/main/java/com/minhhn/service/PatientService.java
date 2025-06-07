package com.minhhn.service;

import com.minhhn.dto.PatientRequest;
import com.minhhn.dto.PatientResponse;

import java.util.List;

public interface PatientService {
    PatientResponse createPatient(PatientRequest request);
    PatientResponse getPatientById(Long id);
    List<PatientResponse> getAllPatient();
    PatientResponse update(Long id, PatientRequest request);
    void delete(Long id);
}
