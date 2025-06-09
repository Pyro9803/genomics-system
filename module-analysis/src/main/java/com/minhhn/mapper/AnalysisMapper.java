package com.minhhn.mapper;

import com.minhhn.dto.AnalysisDTO;
import com.minhhn.dto.CreateAnalysisRequest;
import com.minhhn.model.Analysis;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper interface for converting between Analysis entities and DTOs.
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {})
public interface AnalysisMapper {
    
    /**
     * Converts a CreateAnalysisRequest to an Analysis entity.
     * 
     * @param request The request DTO
     * @return The Analysis entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "startedAt", ignore = true)
    @Mapping(target = "finishedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "vcfPath", ignore = true)
    @Mapping(target = "logPath", ignore = true)
    @Mapping(target = "resultSummary", ignore = true)
    @Mapping(target = "sample", ignore = true)
    Analysis toEntity(CreateAnalysisRequest request);
    
    /**
     * Converts an Analysis entity to an AnalysisDTO.
     * 
     * @param analysis The Analysis entity
     * @return The AnalysisDTO
     */
    @Mapping(target = "sampleId", source = "sample.id")
    @Mapping(target = "sampleCode", source = "sample.sampleCode")
    @Mapping(target = "sampleType", source = "sample.sampleType")
    AnalysisDTO toDto(Analysis analysis);
}
