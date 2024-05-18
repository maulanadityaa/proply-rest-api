package com.enigma.proplybackend.model.entity;

import com.enigma.proplybackend.constant.EGender;
import com.enigma.proplybackend.constant.EMaritalStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "mst_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "fullname", nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private EGender gender;

    @Column(name = "birth_date", nullable = false)
    private Long birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "marital_status", nullable = false)
    private EMaritalStatus maritalStatus;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @ManyToOne
    @Cascade(CascadeType.MERGE)
    @JoinColumn(name = "division_id")
    private Division division;

    @OneToOne(mappedBy = "user")
    @Cascade(CascadeType.ALL)
    private UserCredential userCredential;
}
