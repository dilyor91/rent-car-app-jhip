package uz.carapp.rentcarapp.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.carapp.rentcarapp.domain.Merchant;

/**
 * Spring Data JPA repository for the Merchant entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MerchantRepository extends JpaRepository<Merchant, Long> {}
