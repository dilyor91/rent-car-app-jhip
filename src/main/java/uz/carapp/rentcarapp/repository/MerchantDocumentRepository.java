package uz.carapp.rentcarapp.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.carapp.rentcarapp.domain.MerchantDocument;

/**
 * Spring Data JPA repository for the MerchantDocument entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MerchantDocumentRepository extends JpaRepository<MerchantDocument, Long> {}
