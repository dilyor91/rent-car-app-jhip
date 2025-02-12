package uz.carapp.rentcarapp.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.carapp.rentcarapp.domain.Param;

/**
 * Spring Data JPA repository for the Param entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParamRepository extends JpaRepository<Param, Long> {}
