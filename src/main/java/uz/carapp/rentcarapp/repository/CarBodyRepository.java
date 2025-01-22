package uz.carapp.rentcarapp.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.carapp.rentcarapp.domain.CarBody;

/**
 * Spring Data JPA repository for the CarBody entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CarBodyRepository extends JpaRepository<CarBody, Long> {}
