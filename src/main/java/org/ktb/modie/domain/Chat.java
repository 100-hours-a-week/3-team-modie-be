package org.ktb.modie.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Integer messageId;

    // private Long meetId;
    @ManyToOne
    @JoinColumn(name = "meet_id", nullable = false)
    private Meet meet;  // Meet 엔티티 참조

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // User 엔티티 참조
    
    // private String userId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "message_content", nullable = false, length = 200)
    private String messageContent;

    // Getters and Setters
}
