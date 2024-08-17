package com.lb.brandingApp.task.data.entities;

import com.lb.brandingApp.auth.data.entities.User;
import com.lb.brandingApp.category.data.entities.District;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "adhoc_task")
@Getter
@Setter
@NoArgsConstructor
public class AdhocTask {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String name;

    @Column(columnDefinition = "MEDIUMTEXT", length = 2000)
    private String description;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String location;

    private Double latitude;

    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    @OneToOne(mappedBy = "linkedAdhocTask")
    private Task convertedTask;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "last_modified_by")
    private User lastModifiedBy;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime lastModifiedAt;
}
