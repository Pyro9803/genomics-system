package com.minhhn.service;

import com.minhhn.dto.CreateSampleRequest;
import com.minhhn.dto.SampleDTO;
import com.minhhn.dto.UpdateSampleRequest;
import com.minhhn.exception.ResourceAlreadyExistsException;
import com.minhhn.exception.ResourceNotFoundException;
import com.minhhn.mapper.SampleMapper;
import com.minhhn.model.Patient;
import com.minhhn.model.Sample;
import com.minhhn.repository.SampleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SampleService {
    
    private final SampleRepository sampleRepository;
    private final SampleMapper sampleMapper;
    private final com.minhhn.repository.PatientRepository patientRepository;
    
    @Transactional(readOnly = true)
    public List<SampleDTO> getAllSamples() {
        return sampleRepository.findAll().stream()
                .map(sampleMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public SampleDTO getSampleById(Long id) {
        Sample sample = sampleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sample not found with id: " + id));
        return sampleMapper.toDto(sample);
    }
    
    @Transactional(readOnly = true)
    public SampleDTO getSampleByCode(String sampleCode) {
        Sample sample = sampleRepository.findBySampleCode(sampleCode)
                .orElseThrow(() -> new ResourceNotFoundException("Sample not found with code: " + sampleCode));
        return sampleMapper.toDto(sample);
    }
    
    @Transactional(readOnly = true)
    public List<SampleDTO> getSamplesByPatientId(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }
        
        return sampleRepository.findByPatientId(patientId).stream()
                .map(sampleMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public SampleDTO createSample(CreateSampleRequest request) {
        log.info("Creating sample with code: {}", request.getSampleCode());
        
        if (sampleRepository.existsBySampleCode(request.getSampleCode())) {
            throw new ResourceAlreadyExistsException("Sample already exists with code: " + request.getSampleCode());
        }
        
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + request.getPatientId()));
        
        Sample sample = sampleMapper.toEntity(request);
        sample.setPatient(patient);
        
        Sample savedSample = sampleRepository.save(sample);
        log.info("Sample created with id: {}", savedSample.getId());
        
        return sampleMapper.toDto(savedSample);
    }
    
    @Transactional
    public SampleDTO updateSample(Long id, UpdateSampleRequest request) {
        log.info("Updating sample with id: {}", id);
        
        Sample existingSample = sampleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sample not found with id: " + id));
        
        sampleMapper.updateEntityFromDto(request, existingSample);
        Sample updatedSample = sampleRepository.save(existingSample);
        
        log.info("Sample updated with id: {}", updatedSample.getId());
        return sampleMapper.toDto(updatedSample);
    }
    
    @Transactional
    public void deleteSample(Long id) {
        log.info("Deleting sample with id: {}", id);
        
        if (!sampleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sample not found with id: " + id);
        }
        
        sampleRepository.deleteById(id);
        log.info("Sample deleted with id: {}", id);
    }
}
