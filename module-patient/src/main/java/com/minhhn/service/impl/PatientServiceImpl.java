package com.minhhn.service.impl;

import com.minhhn.dto.PatientRequest;
import com.minhhn.dto.PatientResponse;
import com.minhhn.exception.PatientNotFoundException;
import com.minhhn.mapper.PatientMapper;
import com.minhhn.model.Patient;
import com.minhhn.repository.PatientRepository;
import com.minhhn.service.PatientService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper mapper;

    public PatientServiceImpl(PatientRepository patientRepository, PatientMapper mapper) {
        this.patientRepository = patientRepository;
        this.mapper = mapper;
    }

    @Override
    public PatientResponse createPatient(PatientRequest request) {
        Patient patient = mapper.toEntity(request);
        patient.setPatientCode(UUID.randomUUID().toString());
        return mapper.toDto(patientRepository.save(patient));
    }

    @Override
    public PatientResponse getPatientById(Long id) {
        return patientRepository.findById(id).map(mapper::toDto).orElseThrow(() -> new PatientNotFoundException(id));
    }

    @Override
    public List<PatientResponse> getAllPatient() {
        return patientRepository.findAll().stream().map(mapper::toDto).toList();
    }

    @Override
    public PatientResponse update(Long id, PatientRequest request) {
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException(id));

        patient.setFullName(request.getFullName());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setGender(request.getGender());
        patient.setPhoneNumber(request.getPhoneNumber());
        patient.setEmail(request.getEmail());
        patient.setAddress(request.getAddress());
        patient.setNationalId(request.getNationalId());

        return mapper.toDto(patientRepository.save(patient));
    }

    @Override
    public void delete(Long id) {
        if(!patientRepository.existsById(id)) {
            throw new PatientNotFoundException(id);
        }

        patientRepository.deleteById(id);
    }
}
