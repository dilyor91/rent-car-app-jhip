package uz.carapp.rentcarapp.service;

import java.util.List;
import java.util.Optional;
import uz.carapp.rentcarapp.service.dto.MerchantBranchDTO;

/**
 * Service Interface for managing {@link uz.carapp.rentcarapp.domain.MerchantBranch}.
 */
public interface MerchantBranchService {
    /**
     * Save a merchantBranch.
     *
     * @param merchantBranchDTO the entity to save.
     * @return the persisted entity.
     */
    MerchantBranchDTO save(MerchantBranchDTO merchantBranchDTO);

    /**
     * Updates a merchantBranch.
     *
     * @param merchantBranchDTO the entity to update.
     * @return the persisted entity.
     */
    MerchantBranchDTO update(MerchantBranchDTO merchantBranchDTO);

    /**
     * Partially updates a merchantBranch.
     *
     * @param merchantBranchDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<MerchantBranchDTO> partialUpdate(MerchantBranchDTO merchantBranchDTO);

    /**
     * Get all the merchantBranches.
     *
     * @return the list of entities.
     */
    List<MerchantBranchDTO> findAll();

    /**
     * Get the "id" merchantBranch.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<MerchantBranchDTO> findOne(Long id);

    /**
     * Delete the "id" merchantBranch.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the merchantBranch corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<MerchantBranchDTO> search(String query);
}
