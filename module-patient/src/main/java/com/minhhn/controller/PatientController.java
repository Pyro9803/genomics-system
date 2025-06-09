package com.minhhn.controller;

import com.minhhn.dto.CreatePatientRequest;
import com.minhhn.dto.PatientDTO;
import com.minhhn.dto.UpdatePatientRequest;
import com.minhhn.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    
    private final PatientService patientService;
    
    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }
    
    @GetMapping("/code/{patientCode}")
    public ResponseEntity<PatientDTO> getPatientByCode(@PathVariable String patientCode) {
        return ResponseEntity.ok(patientService.getPatientByCode(patientCode));
    }
    
    @PostMapping
    public ResponseEntity<PatientDTO> createPatient(@Valid @RequestBody CreatePatientRequest createPatientRequest) {
        PatientDTO createdPatient = patientService.createPatient(createPatientRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPatient);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<PatientDTO> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePatientRequest updatePatientRequest) {
        return ResponseEntity.ok(patientService.updatePatient(id, updatePatientRequest));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
