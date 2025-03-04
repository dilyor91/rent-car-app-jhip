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
import uz.carapp.rentcarapp.repository.DocAttachmentRepository;
import uz.carapp.rentcarapp.service.DocAttachmentService;
import uz.carapp.rentcarapp.service.dto.DocAttachmentDTO;
import uz.carapp.rentcarapp.web.rest.errors.BadRequestAlertException;
import uz.carapp.rentcarapp.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link uz.carapp.rentcarapp.domain.DocAttachment}.
 */
@RestController
@RequestMapping("/api/doc-attachments")
public class DocAttachmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(DocAttachmentResource.class);

    private static final String ENTITY_NAME = "docAttachment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DocAttachmentService docAttachmentService;

    private final DocAttachmentRepository docAttachmentRepository;

    public DocAttachmentResource(DocAttachmentService docAttachmentService, DocAttachmentRepository docAttachmentRepository) {
        this.docAttachmentService = docAttachmentService;
        this.docAttachmentRepository = docAttachmentRepository;
    }

    /**
     * {@code POST  /doc-attachments} : Create a new docAttachment.
     *
     * @param docAttachmentDTO the docAttachmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new docAttachmentDTO, or with status {@code 400 (Bad Request)} if the docAttachment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<DocAttachmentDTO> createDocAttachment(@RequestBody DocAttachmentDTO docAttachmentDTO) throws URISyntaxException {
        LOG.debug("REST request to save DocAttachment : {}", docAttachmentDTO);
        if (docAttachmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new docAttachment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        docAttachmentDTO = docAttachmentService.save(docAttachmentDTO);
        return ResponseEntity.created(new URI("/api/doc-attachments/" + docAttachmentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, docAttachmentDTO.getId().toString()))
            .body(docAttachmentDTO);
    }

    /**
     * {@code PUT  /doc-attachments/:id} : Updates an existing docAttachment.
     *
     * @param id the id of the docAttachmentDTO to save.
     * @param docAttachmentDTO the docAttachmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated docAttachmentDTO,
     * or with status {@code 400 (Bad Request)} if the docAttachmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the docAttachmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<DocAttachmentDTO> updateDocAttachment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DocAttachmentDTO docAttachmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update DocAttachment : {}, {}", id, docAttachmentDTO);
        if (docAttachmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, docAttachmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!docAttachmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        docAttachmentDTO = docAttachmentService.update(docAttachmentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, docAttachmentDTO.getId().toString()))
            .body(docAttachmentDTO);
    }

    /**
     * {@code PATCH  /doc-attachments/:id} : Partial updates given fields of an existing docAttachment, field will ignore if it is null
     *
     * @param id the id of the docAttachmentDTO to save.
     * @param docAttachmentDTO the docAttachmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated docAttachmentDTO,
     * or with status {@code 400 (Bad Request)} if the docAttachmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the docAttachmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the docAttachmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<DocAttachmentDTO> partialUpdateDocAttachment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody DocAttachmentDTO docAttachmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update DocAttachment partially : {}, {}", id, docAttachmentDTO);
        if (docAttachmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, docAttachmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!docAttachmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<DocAttachmentDTO> result = docAttachmentService.partialUpdate(docAttachmentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, docAttachmentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /doc-attachments} : get all the docAttachments.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of docAttachments in body.
     */
    @GetMapping("")
    public List<DocAttachmentDTO> getAllDocAttachments() {
        LOG.debug("REST request to get all DocAttachments");
        return docAttachmentService.findAll();
    }

    /**
     * {@code GET  /doc-attachments/:id} : get the "id" docAttachment.
     *
     * @param id the id of the docAttachmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the docAttachmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DocAttachmentDTO> getDocAttachment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get DocAttachment : {}", id);
        Optional<DocAttachmentDTO> docAttachmentDTO = docAttachmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(docAttachmentDTO);
    }

    /**
     * {@code DELETE  /doc-attachments/:id} : delete the "id" docAttachment.
     *
     * @param id the id of the docAttachmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocAttachment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete DocAttachment : {}", id);
        docAttachmentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /doc-attachments/_search?query=:query} : search for the docAttachment corresponding
     * to the query.
     *
     * @param query the query of the docAttachment search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<DocAttachmentDTO> searchDocAttachments(@RequestParam("query") String query) {
        LOG.debug("REST request to search DocAttachments for query {}", query);
        try {
            return docAttachmentService.search(query);
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
