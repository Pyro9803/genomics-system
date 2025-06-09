package com.minhhn.mapper;

import com.minhhn.dto.CreatePatientRequest;
import com.minhhn.dto.PatientDTO;
import com.minhhn.dto.UpdatePatientRequest;
import com.minhhn.model.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PatientMapper {
    
    PatientDTO toDto(Patient patient);
    
    @Mapping(target = "id", ignore = true)
    Patient toEntity(PatientDTO patientDTO);
    
    @Mapping(target = "id", ignore = true)
    Patient toEntity(CreatePatientRequest createPatientRequest);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patientCode", ignore = true)
    void updatePatientFromDto(UpdatePatientRequest updatePatientRequest, @MappingTarget Patient patient);
}
