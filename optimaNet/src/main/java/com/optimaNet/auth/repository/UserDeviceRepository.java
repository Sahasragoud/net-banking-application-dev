package com.optimaNet.auth.repository;

import com.optimaNet.auth.entity.UserDevice;
import com.optimaNet.exception.UserNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice,Long> {
    UserDevice findByUserIdAndDeviceId(Long userId, String deviceId);

    Optional<UserDevice> findById(Long deviceId);

    Page<UserDevice> findAll(Pageable pageable);

    Page<UserDevice> findAllByUserId(Long userId, Pageable pageable) throws UserNotFoundException;

}
