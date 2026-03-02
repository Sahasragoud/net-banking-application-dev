package com.optimaNet.v2.repository;

import com.optimaNet.v2.entity.KycRegistryEntryV2;
import com.optimaNet.v2.enums.KycDocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KycRegistryEntryV2Repository extends JpaRepository<KycRegistryEntryV2, Long> {

    Optional<KycRegistryEntryV2> findByDocumentTypeAndDocumentNumberAndActiveTrue(
            KycDocumentType documentType,
            String documentNumber
    );
}
