package org.ktb.modie.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "meet")
public class Meet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meet_id", nullable = false)
    private Long meetId;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User owner;

    @Column(name = "meet_intro", nullable = false)
    private String meetIntro;

    @Column(name = "meet_type", nullable = false)
    private String meetType;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "address_description", nullable = false)
    private String addressDescription;

    @Column(name = "meet_at", nullable = false)
    private LocalDateTime meetAt;

    @Column(name = "total_cost", nullable = false)
    private Integer totalCost;

    @Column(name = "member_limit", nullable = false)
    private Integer memberLimit;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Builder
    public Meet(String meetIntro, String meetType, String address,
        String addressDescription, LocalDateTime meetAt,
        Integer totalCost, Integer memberLimit, User owner) {
        this.meetIntro = meetIntro;
        this.meetType = meetType;
        this.address = address;
        this.addressDescription = addressDescription;
        this.meetAt = meetAt;
        this.totalCost = totalCost;
        this.memberLimit = memberLimit;
        this.owner = owner;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
