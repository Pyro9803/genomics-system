package com.minhhn.mapper;

import com.minhhn.dto.PatientRequest;
import com.minhhn.dto.PatientResponse;
import com.minhhn.model.Patient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    Patient toEntity(PatientRequest request);
    PatientResponse toDto(Patient entity);
}
