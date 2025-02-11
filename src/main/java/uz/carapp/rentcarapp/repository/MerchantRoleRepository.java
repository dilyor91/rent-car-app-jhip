package uz.carapp.rentcarapp.repository;

import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.carapp.rentcarapp.domain.MerchantRole;

/**
 * Spring Data JPA repository for the MerchantRole entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MerchantRoleRepository extends JpaRepository<MerchantRole, Long> {
    @Query("select merchantRole from MerchantRole merchantRole where merchantRole.user.login = ?#{authentication.name}")
    List<MerchantRole> findByUserIsCurrentUser();
}
