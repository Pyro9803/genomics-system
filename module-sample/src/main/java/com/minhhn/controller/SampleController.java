package com.minhhn.controller;

import com.minhhn.dto.CreateSampleRequest;
import com.minhhn.dto.SampleDTO;
import com.minhhn.dto.UpdateSampleRequest;
import com.minhhn.service.SampleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/samples")
@RequiredArgsConstructor
public class SampleController {

    private final SampleService sampleService;

    @GetMapping
    public ResponseEntity<List<SampleDTO>> getAllSamples() {
        return ResponseEntity.ok(sampleService.getAllSamples());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SampleDTO> getSampleById(@PathVariable Long id) {
        return ResponseEntity.ok(sampleService.getSampleById(id));
    }

    @GetMapping("/code/{sampleCode}")
    public ResponseEntity<SampleDTO> getSampleByCode(@PathVariable String sampleCode) {
        return ResponseEntity.ok(sampleService.getSampleByCode(sampleCode));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<SampleDTO>> getSamplesByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(sampleService.getSamplesByPatientId(patientId));
    }

    @PostMapping
    public ResponseEntity<SampleDTO> createSample(@Valid @RequestBody CreateSampleRequest request) {
        SampleDTO createdSample = sampleService.createSample(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSample);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SampleDTO> updateSample(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSampleRequest request) {
        return ResponseEntity.ok(sampleService.updateSample(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSample(@PathVariable Long id) {
        sampleService.deleteSample(id);
        return ResponseEntity.noContent().build();
    }
}
