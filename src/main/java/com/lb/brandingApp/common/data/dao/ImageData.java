package com.lb.brandingApp.common.data.dao;

import com.lb.brandingApp.task.data.dao.Allotment;
import com.lb.brandingApp.task.data.dao.Task;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ImageReference reference;

    @Lob
    @Column(name = "data", columnDefinition = "LONGBLOB")
    private byte[] imageData;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    @ManyToOne
    @JoinColumn(name = "allotment_id")
    private Allotment allotment;

}
