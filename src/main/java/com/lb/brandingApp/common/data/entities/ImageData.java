package com.lb.brandingApp.common.data.entities;

import com.lb.brandingApp.task.data.entities.Allotment;
import com.lb.brandingApp.task.data.entities.Task;
import com.lb.brandingApp.common.data.enums.ImageReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "image_data")
@Getter
@Setter
@NoArgsConstructor
public class ImageData {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ImageReference reference;

    @Lob
    @Column(name = "data", columnDefinition = "LONGBLOB")
    private byte[] imageData;

    @ManyToOne
    @JoinColumn(name = "agreement_task_id")
    private Task agreementTaskDetails;

    @ManyToOne
    @JoinColumn(name = "final_task_id")
    private Task finalTaskDetails;

    @ManyToOne
    @JoinColumn(name = "allotment_id")
    private Allotment allotment;

    @ManyToOne
    @JoinColumn(name = "invoiced_allotment_id")
    private Allotment invoicedAllotment;

}
