package com.lb.brandingApp.task.data.entities;

import com.lb.brandingApp.auth.data.entities.User;
import com.lb.brandingApp.category.data.entities.District;
import com.lb.brandingApp.common.data.entities.Amount;
import com.lb.brandingApp.common.data.entities.Area;
import com.lb.brandingApp.common.data.entities.ImageData;
import com.lb.brandingApp.common.data.entities.Quantity;
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

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime approvedAt;

    @OneToOne
    private Amount rent;

    @OneToMany
    @JoinColumn(name = "task_id")
    private Set<ImageData> finalImages;

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

    private boolean isRenewed;

    @OneToOne(fetch = FetchType.LAZY)
    private Task renewedFrom;

}
