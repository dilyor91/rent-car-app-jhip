package uz.carapp.rentcarapp.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.carapp.rentcarapp.domain.Parametr;

/**
 * Spring Data JPA repository for the Parametr entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParametrRepository extends JpaRepository<Parametr, Long> {}
