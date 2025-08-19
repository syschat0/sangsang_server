package kr.co.bnksys.sangsang.repository;

import kr.co.bnksys.sangsang.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> { }
