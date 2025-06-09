package com.minhhn.service;

import com.minhhn.dto.CreatePatientRequest;
import com.minhhn.dto.PatientDTO;
import com.minhhn.dto.UpdatePatientRequest;
import com.minhhn.exception.ResourceAlreadyExistsException;
import com.minhhn.exception.ResourceNotFoundException;
import com.minhhn.mapper.PatientMapper;
import com.minhhn.model.Patient;
import com.minhhn.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {
    
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    
    @Transactional(readOnly = true)
    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(patientMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public PatientDTO getPatientById(Long id) {
        return patientRepository.findById(id)
                .map(patientMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }
    
    @Transactional(readOnly = true)
    public PatientDTO getPatientByCode(String patientCode) {
        return patientRepository.findByPatientCode(patientCode)
                .map(patientMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with code: " + patientCode));
    }
    
    @Transactional
    public PatientDTO createPatient(CreatePatientRequest createPatientRequest) {
        if (patientRepository.existsByPatientCode(createPatientRequest.getPatientCode())) {
            throw new ResourceAlreadyExistsException("Patient already exists with code: " + createPatientRequest.getPatientCode());
        }
        
        Patient patient = patientMapper.toEntity(createPatientRequest);
        Patient savedPatient = patientRepository.save(patient);
        return patientMapper.toDto(savedPatient);
    }
    
    @Transactional
    public PatientDTO updatePatient(Long id, UpdatePatientRequest updatePatientRequest) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
        
        patientMapper.updatePatientFromDto(updatePatientRequest, patient);
        Patient updatedPatient = patientRepository.save(patient);
        return patientMapper.toDto(updatedPatient);
    }
    
    @Transactional
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);
    }
}
