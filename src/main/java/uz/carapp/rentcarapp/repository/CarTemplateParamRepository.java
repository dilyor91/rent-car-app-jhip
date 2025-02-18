package uz.carapp.rentcarapp.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import uz.carapp.rentcarapp.domain.CarTemplateParam;

/**
 * Spring Data JPA repository for the CarTemplateParam entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CarTemplateParamRepository extends JpaRepository<CarTemplateParam, Long> {}
