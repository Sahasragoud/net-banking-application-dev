package com.optimaNet.auth.repository;

import com.optimaNet.auth.entity.LoginSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoginSessionRepository extends JpaRepository<LoginSession, Long> {

    Page<LoginSession> findAllByUserIdAndIsActiveTrue(Long userId, Pageable pageable);

    Page<LoginSession> findAll( Pageable pageable);

    Optional<LoginSession> findByIdAndIsActiveTrue(Long sessionId);

}
