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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Integer messageId;

    @ManyToOne
    @JoinColumn(name = "meet_id")  // meetId로 외래 키 연결
    private Meet meet;

    @ManyToOne
    @JoinColumn(name = "user_id")  // userId로 외래 키 연결
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "message_content", nullable = false, length = 200)
    private String messageContent;

    // Getters and Setters
}
