package com.lb.brandingApp.task.data.entities;

import com.lb.brandingApp.auth.data.entities.User;
import com.lb.brandingApp.common.data.entities.*;
import com.lb.brandingApp.config.data.entities.ProductConfig;
import com.lb.brandingApp.common.data.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "allotment")
@Getter
@Setter
@NoArgsConstructor
public class Allotment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductConfig productConfig;

    @OneToOne
    private Dimension dimension;

    @OneToOne
    private Area area;

    @OneToOne
    private Quantity quantity;

    @OneToOne
    private Amount amount;

    @OneToMany(mappedBy = "allotment")
    private Set<Note> notes;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @ManyToOne
    @JoinColumn(name = "allotment_id")
    private Task task;

    @OneToMany
    @JoinColumn(name = "allotment_id")
    private Set<ImageData> referenceImages;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "last_modified_by")
    private User modifiedBy;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime lastModifiedAt;

}
