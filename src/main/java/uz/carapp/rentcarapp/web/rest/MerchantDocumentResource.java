package uz.carapp.rentcarapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;
import uz.carapp.rentcarapp.repository.MerchantDocumentRepository;
import uz.carapp.rentcarapp.service.MerchantDocumentService;
import uz.carapp.rentcarapp.service.dto.MerchantDocumentDTO;
import uz.carapp.rentcarapp.web.rest.errors.BadRequestAlertException;
import uz.carapp.rentcarapp.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link uz.carapp.rentcarapp.domain.MerchantDocument}.
 */
@RestController
@RequestMapping("/api/merchant-documents")
public class MerchantDocumentResource {

    private static final Logger LOG = LoggerFactory.getLogger(MerchantDocumentResource.class);

    private static final String ENTITY_NAME = "merchantDocument";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MerchantDocumentService merchantDocumentService;

    private final MerchantDocumentRepository merchantDocumentRepository;

    public MerchantDocumentResource(
        MerchantDocumentService merchantDocumentService,
        MerchantDocumentRepository merchantDocumentRepository
    ) {
        this.merchantDocumentService = merchantDocumentService;
        this.merchantDocumentRepository = merchantDocumentRepository;
    }

    /**
     * {@code POST  /merchant-documents} : Create a new merchantDocument.
     *
     * @param merchantDocumentDTO the merchantDocumentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new merchantDocumentDTO, or with status {@code 400 (Bad Request)} if the merchantDocument has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MerchantDocumentDTO> createMerchantDocument(@RequestBody MerchantDocumentDTO merchantDocumentDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save MerchantDocument : {}", merchantDocumentDTO);
        if (merchantDocumentDTO.getId() != null) {
            throw new BadRequestAlertException("A new merchantDocument cannot already have an ID", ENTITY_NAME, "idexists");
        }
        merchantDocumentDTO = merchantDocumentService.save(merchantDocumentDTO);
        return ResponseEntity.created(new URI("/api/merchant-documents/" + merchantDocumentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, merchantDocumentDTO.getId().toString()))
            .body(merchantDocumentDTO);
    }

    /**
     * {@code PUT  /merchant-documents/:id} : Updates an existing merchantDocument.
     *
     * @param id the id of the merchantDocumentDTO to save.
     * @param merchantDocumentDTO the merchantDocumentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated merchantDocumentDTO,
     * or with status {@code 400 (Bad Request)} if the merchantDocumentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the merchantDocumentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MerchantDocumentDTO> updateMerchantDocument(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MerchantDocumentDTO merchantDocumentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MerchantDocument : {}, {}", id, merchantDocumentDTO);
        if (merchantDocumentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, merchantDocumentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!merchantDocumentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        merchantDocumentDTO = merchantDocumentService.update(merchantDocumentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, merchantDocumentDTO.getId().toString()))
            .body(merchantDocumentDTO);
    }

    /**
     * {@code PATCH  /merchant-documents/:id} : Partial updates given fields of an existing merchantDocument, field will ignore if it is null
     *
     * @param id the id of the merchantDocumentDTO to save.
     * @param merchantDocumentDTO the merchantDocumentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated merchantDocumentDTO,
     * or with status {@code 400 (Bad Request)} if the merchantDocumentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the merchantDocumentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the merchantDocumentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MerchantDocumentDTO> partialUpdateMerchantDocument(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MerchantDocumentDTO merchantDocumentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MerchantDocument partially : {}, {}", id, merchantDocumentDTO);
        if (merchantDocumentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, merchantDocumentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!merchantDocumentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MerchantDocumentDTO> result = merchantDocumentService.partialUpdate(merchantDocumentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, merchantDocumentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /merchant-documents} : get all the merchantDocuments.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of merchantDocuments in body.
     */
    @GetMapping("")
    public List<MerchantDocumentDTO> getAllMerchantDocuments() {
        LOG.debug("REST request to get all MerchantDocuments");
        return merchantDocumentService.findAll();
    }

    /**
     * {@code GET  /merchant-documents/:id} : get the "id" merchantDocument.
     *
     * @param id the id of the merchantDocumentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the merchantDocumentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MerchantDocumentDTO> getMerchantDocument(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MerchantDocument : {}", id);
        Optional<MerchantDocumentDTO> merchantDocumentDTO = merchantDocumentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(merchantDocumentDTO);
    }

    /**
     * {@code DELETE  /merchant-documents/:id} : delete the "id" merchantDocument.
     *
     * @param id the id of the merchantDocumentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMerchantDocument(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MerchantDocument : {}", id);
        merchantDocumentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /merchant-documents/_search?query=:query} : search for the merchantDocument corresponding
     * to the query.
     *
     * @param query the query of the merchantDocument search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<MerchantDocumentDTO> searchMerchantDocuments(@RequestParam("query") String query) {
        LOG.debug("REST request to search MerchantDocuments for query {}", query);
        try {
            return merchantDocumentService.search(query);
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
