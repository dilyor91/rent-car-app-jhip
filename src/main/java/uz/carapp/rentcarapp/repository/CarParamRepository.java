package uz.carapp.rentcarapp.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.carapp.rentcarapp.domain.CarParam;

/**
 * Spring Data JPA repository for the CarParam entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CarParamRepository extends JpaRepository<CarParam, Long> {}
