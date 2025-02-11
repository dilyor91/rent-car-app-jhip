package uz.carapp.rentcarapp.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.carapp.rentcarapp.domain.MerchantBranch;

/**
 * Spring Data JPA repository for the MerchantBranch entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MerchantBranchRepository extends JpaRepository<MerchantBranch, Long> {}
