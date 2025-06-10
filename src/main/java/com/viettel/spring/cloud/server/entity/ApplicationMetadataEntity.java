package com.viettel.spring.cloud.server.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "application_metadatas", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"applicaion_id"})
})
public class ApplicationMetadataEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "application_id", nullable = false)
    private ApplicationEntity application;

    @Column(name = "owner", nullable = false)
    private String owner;

    @Column(name = "team", nullable = false)
    private String team;

    @Column(name = "environment", nullable = false)
    private String environment;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "criticality", nullable = false)
    private String criticality;

    @Column(name = "documentation")
    private String documentation;

    @Column(name = "repository")
    private String repository;

    @Column(name = "contact", nullable = false)
    private String contact;

    @Column(name = "business_unit")
    private String businessUnit;

    @Column(name = "cost_center")
    private String costCenter;

    @Column(name = "maintenance_window")
    private String maintenanceWindow;

    @ElementCollection
    @CollectionTable(name = "application_metadata_tags", joinColumns = @JoinColumn(name = "application_metadata_id"))
    @Column(name = "tag")
    private List<String> tags;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
