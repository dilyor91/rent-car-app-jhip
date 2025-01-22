package uz.carapp.rentcarapp.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.carapp.rentcarapp.domain.CarClass;

/**
 * Spring Data JPA repository for the CarClass entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CarClassRepository extends JpaRepository<CarClass, Long> {}
