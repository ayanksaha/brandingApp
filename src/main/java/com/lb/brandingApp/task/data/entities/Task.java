package com.lb.brandingApp.task.data.entities;

import com.lb.brandingApp.auth.data.entities.User;
import com.lb.brandingApp.category.data.entities.District;
import com.lb.brandingApp.common.data.entities.*;
import com.lb.brandingApp.common.data.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "task")
@Getter
@Setter
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue
    private Long id;

    @Column(length = 50)
    private String name;

    @OneToMany
    @JoinColumn(name = "task_id")
    private Set<WorkflowItem> workflow;

    private String location;

    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    private District district;

    private String mobileNumber;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime startDate;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime endDate;

    @OneToMany
    @JoinColumn(name = "allotment_id")
    private Set<Allotment> allotments;

    @OneToOne
    private Quantity aggregatedQuantity;

    @OneToOne
    private Area aggregatedArea;

    @OneToOne
    private Amount aggregatedAmount;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;

    @OneToOne
    private Amount rent;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime expiry;

    @ManyToOne
    @JoinColumn(name = "assignee_id", nullable = false)
    private Assignee currentAssignee;

    @ManyToMany
    @JoinTable(
            name = "earlier_assignments",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "assignee_id"))
    private Set<Assignee> earlierAssignees;

    @OneToMany(mappedBy = "task")
    private Set<ImageData> referenceImages;

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
