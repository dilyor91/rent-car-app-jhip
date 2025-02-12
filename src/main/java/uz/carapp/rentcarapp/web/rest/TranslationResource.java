package uz.carapp.rentcarapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;
import uz.carapp.rentcarapp.repository.TranslationRepository;
import uz.carapp.rentcarapp.service.TranslationService;
import uz.carapp.rentcarapp.service.dto.TranslationDTO;
import uz.carapp.rentcarapp.web.rest.errors.BadRequestAlertException;
import uz.carapp.rentcarapp.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link uz.carapp.rentcarapp.domain.Translation}.
 */
@RestController
@RequestMapping("/api/translations")
public class TranslationResource {

    private static final Logger LOG = LoggerFactory.getLogger(TranslationResource.class);

    private static final String ENTITY_NAME = "translation";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TranslationService translationService;

    private final TranslationRepository translationRepository;

    public TranslationResource(TranslationService translationService, TranslationRepository translationRepository) {
        this.translationService = translationService;
        this.translationRepository = translationRepository;
    }

    /**
     * {@code POST  /translations} : Create a new translation.
     *
     * @param translationDTO the translationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new translationDTO, or with status {@code 400 (Bad Request)} if the translation has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TranslationDTO> createTranslation(@RequestBody TranslationDTO translationDTO) throws URISyntaxException {
        LOG.debug("REST request to save Translation : {}", translationDTO);
        if (translationDTO.getId() != null) {
            throw new BadRequestAlertException("A new translation cannot already have an ID", ENTITY_NAME, "idexists");
        }
        translationDTO = translationService.save(translationDTO);
        return ResponseEntity.created(new URI("/api/translations/" + translationDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, translationDTO.getId().toString()))
            .body(translationDTO);
    }

    /**
     * {@code PUT  /translations/:id} : Updates an existing translation.
     *
     * @param id the id of the translationDTO to save.
     * @param translationDTO the translationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated translationDTO,
     * or with status {@code 400 (Bad Request)} if the translationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the translationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TranslationDTO> updateTranslation(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TranslationDTO translationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Translation : {}, {}", id, translationDTO);
        if (translationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, translationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!translationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        translationDTO = translationService.update(translationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, translationDTO.getId().toString()))
            .body(translationDTO);
    }

    /**
     * {@code PATCH  /translations/:id} : Partial updates given fields of an existing translation, field will ignore if it is null
     *
     * @param id the id of the translationDTO to save.
     * @param translationDTO the translationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated translationDTO,
     * or with status {@code 400 (Bad Request)} if the translationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the translationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the translationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TranslationDTO> partialUpdateTranslation(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TranslationDTO translationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Translation partially : {}, {}", id, translationDTO);
        if (translationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, translationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!translationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TranslationDTO> result = translationService.partialUpdate(translationDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, translationDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /translations} : get all the translations.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of translations in body.
     */
    @GetMapping("")
    public ResponseEntity<List<TranslationDTO>> getAllTranslations(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Translations");
        Page<TranslationDTO> page = translationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /translations/:id} : get the "id" translation.
     *
     * @param id the id of the translationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the translationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TranslationDTO> getTranslation(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Translation : {}", id);
        Optional<TranslationDTO> translationDTO = translationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(translationDTO);
    }

    /**
     * {@code DELETE  /translations/:id} : delete the "id" translation.
     *
     * @param id the id of the translationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTranslation(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Translation : {}", id);
        translationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /translations/_search?query=:query} : search for the translation corresponding
     * to the query.
     *
     * @param query the query of the translation search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<TranslationDTO>> searchTranslations(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of Translations for query {}", query);
        try {
            Page<TranslationDTO> page = translationService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
