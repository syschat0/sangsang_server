package kr.co.bnksys.sangsang.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class UserSession {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionId;

    private LocalDateTime startTime = LocalDateTime.now();
    private LocalDateTime endTime;
}
