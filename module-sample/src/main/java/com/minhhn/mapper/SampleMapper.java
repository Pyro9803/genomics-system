package com.minhhn.mapper;

import com.minhhn.dto.CreateSampleRequest;
import com.minhhn.dto.SampleDTO;
import com.minhhn.dto.UpdateSampleRequest;
import com.minhhn.model.Sample;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {})
public interface SampleMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    Sample toEntity(CreateSampleRequest request);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sampleCode", ignore = true)
    @Mapping(target = "patient", ignore = true)
    void updateEntityFromDto(UpdateSampleRequest request, @MappingTarget Sample sample);
    
    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "patientCode", source = "patient.patientCode")
    @Mapping(target = "patientName", source = "patient.fullName")
    SampleDTO toDto(Sample sample);
}
