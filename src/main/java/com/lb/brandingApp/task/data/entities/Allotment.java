package com.lb.brandingApp.task.data.entities;

import com.lb.brandingApp.auth.data.entities.User;
import com.lb.brandingApp.common.data.entities.*;
import com.lb.brandingApp.product.data.entities.ProductConfig;
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
    @GeneratedValue
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

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime expiry;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private Assignee currentAssignee;

    @ManyToMany
    @JoinTable(
            name = "earlier_assignments",
            joinColumns = @JoinColumn(name = "allotment_id"),
            inverseJoinColumns = @JoinColumn(name = "assignee_id"))
    private Set<Assignee> earlierAssignees;

    @ManyToMany
    @JoinTable(
            name = "future_assignments",
            joinColumns = @JoinColumn(name = "allotment_id"),
            inverseJoinColumns = @JoinColumn(name = "assignee_id"))
    private Set<Assignee> futureAssignees;

    @OneToMany
    @JoinColumn(name = "allotment_id")
    private Set<ImageData> referenceImages;

    @ManyToOne
    @JoinColumn(name = "allotment_id")
    private Task task;

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
