package org.ktb.modie.domain;

import java.time.LocalDateTime;

import org.ktb.modie.core.exception.BusinessException;
import org.ktb.modie.core.exception.CustomErrorCode;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class User {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_name", nullable = false, length = 20)
    private String userName;

    @Setter
    @Column(name = "bank_name", length = 10)
    private String bankName;

    @Setter
    @Column(name = "account_number", length = 30)
    private String accountNumber;

    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateAccountInfo(String bankName, String accountNumber) {
        if (!accountNumber.matches("^[0-9]+$")) {
            throw new BusinessException(CustomErrorCode.INVALID_INPUT_IN_USER);
        }
        this.bankName = bankName;
        this.accountNumber = accountNumber;
    }
}
