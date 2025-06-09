package com.minhhn.model;

import com.minhhn.enums.AnalysisStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "analyses")
public class Analysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String pipelineVersion;
    
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnalysisStatus status;
    
    private String vcfPath;
    private String bamPath;
    private String logPath;
    private String resultSummary;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sample_id", nullable = false)
    private com.minhhn.model.Sample sample;
}
