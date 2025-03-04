package uz.carapp.rentcarapp.service;

import java.util.List;
import java.util.Optional;
import uz.carapp.rentcarapp.service.dto.MerchantDocumentDTO;

/**
 * Service Interface for managing {@link uz.carapp.rentcarapp.domain.MerchantDocument}.
 */
public interface MerchantDocumentService {
    /**
     * Save a merchantDocument.
     *
     * @param merchantDocumentDTO the entity to save.
     * @return the persisted entity.
     */
    MerchantDocumentDTO save(MerchantDocumentDTO merchantDocumentDTO);

    /**
     * Updates a merchantDocument.
     *
     * @param merchantDocumentDTO the entity to update.
     * @return the persisted entity.
     */
    MerchantDocumentDTO update(MerchantDocumentDTO merchantDocumentDTO);

    /**
     * Partially updates a merchantDocument.
     *
     * @param merchantDocumentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<MerchantDocumentDTO> partialUpdate(MerchantDocumentDTO merchantDocumentDTO);

    /**
     * Get all the merchantDocuments.
     *
     * @return the list of entities.
     */
    List<MerchantDocumentDTO> findAll();

    /**
     * Get the "id" merchantDocument.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<MerchantDocumentDTO> findOne(Long id);

    /**
     * Delete the "id" merchantDocument.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    /**
     * Search for the merchantDocument corresponding to the query.
     *
     * @param query the query of the search.
     * @return the list of entities.
     */
    List<MerchantDocumentDTO> search(String query);
}
