package com.optimaNet.auth.repository;

import com.optimaNet.auth.entity.UserIdentity;
import com.optimaNet.auth.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import com.optimaNet.auth.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findById(Long userId);
    Optional<User> findByIdAndUserStatus(Long userId, UserStatus status);

    @Query(value = "SELECT nextval('user_code_seq')", nativeQuery = true)
    Long getNextCustomerID();

    Optional<User> findByCustomerId(String customerId);
}
