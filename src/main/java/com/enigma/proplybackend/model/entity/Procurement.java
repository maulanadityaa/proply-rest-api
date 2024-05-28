package com.enigma.proplybackend.model.entity;

import com.enigma.proplybackend.constant.DbPath;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = DbPath.PROCUREMENT_SCHEMA)
public class Procurement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Long createdAt;

    @Column(name = "updated_at", nullable = false, updatable = true)
    private Long updatedAt;

    @OneToMany(mappedBy = "procurement", cascade = CascadeType.ALL)
    private List<ProcurementDetail> procurementDetails;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "procurement_category_id", nullable = false)
    private ProcurementCategory procurementCategory;

    @OneToMany(mappedBy = "procurement", cascade = CascadeType.ALL)
    private List<Approval> approvals;
}
