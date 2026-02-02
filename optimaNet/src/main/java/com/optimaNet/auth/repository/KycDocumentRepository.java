package com.optimaNet.auth.repository;

import com.optimaNet.auth.entity.KycDocument;
import com.optimaNet.auth.enums.DocumentType;
import com.optimaNet.auth.enums.KycDocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface KycDocumentRepository extends JpaRepository<KycDocument, Long> {

    Page<KycDocument> findByApplicationId(Long applicationId, Pageable pageable);

    List<KycDocument> findByApplicationIdAndStatus(
            Long applicationId,
            KycDocumentStatus status
    );

    boolean existsByApplicationIdAndDocumentTypeAndStatusIn(
            Long applicationId,
            DocumentType documentType,
            Collection<KycDocumentStatus> statuses
    );

}
