package uz.carapp.rentcarapp.service;

import java.util.List;
import java.util.Optional;
import uz.carapp.rentcarapp.service.dto.CarMileageDTO;

/**
 * Service Interface for managing {@link uz.carapp.rentcarapp.domain.CarMileage}.
 */
public interface CarMileageService {
    /**
     * Save a carMileage.
     *
     * @param carMileageDTO the entity to save.
     * @return the persisted entity.
     */
    CarMileageDTO save(CarMileageDTO carMileageDTO);

    /**
     * Updates a carMileage.
     *
     * @param carMileageDTO the entity to update.
     * @return the persisted entity.
     */
    CarMileageDTO update(CarMileageDTO carMileageDTO);

    /**
     * Partially updates a carMileage.
     *
     * @param carMileageDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<CarMileageDTO> partialUpdate(CarMileageDTO carMileageDTO);

    /**
     * Get all the carMileages.
     *
     * @return the list of entities.
     */
    List<CarMileageDTO> findAll();

    /**
     * Get the "id" carMileage.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<CarMileageDTO> findOne(Long id);

    /**
     * Delete the "id" carMileage.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the carMileage corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<CarMileageDTO> search(String query);
}
