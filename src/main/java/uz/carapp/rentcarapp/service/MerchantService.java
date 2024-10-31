package uz.carapp.rentcarapp.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uz.carapp.rentcarapp.service.dto.MerchantDTO;

/**
 * Service Interface for managing {@link uz.carapp.rentcarapp.domain.Merchant}.
 */
public interface MerchantService {
    /**
     * Save a merchant.
     *
     * @param merchantDTO the entity to save.
     * @return the persisted entity.
     */
    MerchantDTO save(MerchantDTO merchantDTO);

    /**
     * Updates a merchant.
     *
     * @param merchantDTO the entity to update.
     * @return the persisted entity.
     */
    MerchantDTO update(MerchantDTO merchantDTO);

    /**
     * Partially updates a merchant.
     *
     * @param merchantDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<MerchantDTO> partialUpdate(MerchantDTO merchantDTO);

    /**
     * Get all the merchants.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MerchantDTO> findAll(Pageable pageable);

    /**
     * Get the "id" merchant.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<MerchantDTO> findOne(Long id);

    /**
     * Delete the "id" merchant.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the merchant corresponding to the query.
     *
     * @param query the query of the search.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<MerchantDTO> search(String query, Pageable pageable);
}
