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
import uz.carapp.rentcarapp.repository.CarAttachmentRepository;
import uz.carapp.rentcarapp.service.CarAttachmentService;
import uz.carapp.rentcarapp.service.dto.CarAttachmentDTO;
import uz.carapp.rentcarapp.web.rest.errors.BadRequestAlertException;
import uz.carapp.rentcarapp.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link uz.carapp.rentcarapp.domain.CarAttachment}.
 */
@RestController
@RequestMapping("/api/car-attachments")
public class CarAttachmentResource {

    private static final Logger LOG = LoggerFactory.getLogger(CarAttachmentResource.class);

    private static final String ENTITY_NAME = "carAttachment";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CarAttachmentService carAttachmentService;

    private final CarAttachmentRepository carAttachmentRepository;

    public CarAttachmentResource(CarAttachmentService carAttachmentService, CarAttachmentRepository carAttachmentRepository) {
        this.carAttachmentService = carAttachmentService;
        this.carAttachmentRepository = carAttachmentRepository;
    }

    /**
     * {@code POST  /car-attachments} : Create a new carAttachment.
     *
     * @param carAttachmentDTO the carAttachmentDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new carAttachmentDTO, or with status {@code 400 (Bad Request)} if the carAttachment has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CarAttachmentDTO> createCarAttachment(@RequestBody CarAttachmentDTO carAttachmentDTO) throws URISyntaxException {
        LOG.debug("REST request to save CarAttachment : {}", carAttachmentDTO);
        if (carAttachmentDTO.getId() != null) {
            throw new BadRequestAlertException("A new carAttachment cannot already have an ID", ENTITY_NAME, "idexists");
        }
        carAttachmentDTO = carAttachmentService.save(carAttachmentDTO);
        return ResponseEntity.created(new URI("/api/car-attachments/" + carAttachmentDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, carAttachmentDTO.getId().toString()))
            .body(carAttachmentDTO);
    }

    /**
     * {@code PUT  /car-attachments/:id} : Updates an existing carAttachment.
     *
     * @param id the id of the carAttachmentDTO to save.
     * @param carAttachmentDTO the carAttachmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carAttachmentDTO,
     * or with status {@code 400 (Bad Request)} if the carAttachmentDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the carAttachmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CarAttachmentDTO> updateCarAttachment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CarAttachmentDTO carAttachmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CarAttachment : {}, {}", id, carAttachmentDTO);
        if (carAttachmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carAttachmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carAttachmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        carAttachmentDTO = carAttachmentService.update(carAttachmentDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carAttachmentDTO.getId().toString()))
            .body(carAttachmentDTO);
    }

    /**
     * {@code PATCH  /car-attachments/:id} : Partial updates given fields of an existing carAttachment, field will ignore if it is null
     *
     * @param id the id of the carAttachmentDTO to save.
     * @param carAttachmentDTO the carAttachmentDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carAttachmentDTO,
     * or with status {@code 400 (Bad Request)} if the carAttachmentDTO is not valid,
     * or with status {@code 404 (Not Found)} if the carAttachmentDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the carAttachmentDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CarAttachmentDTO> partialUpdateCarAttachment(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CarAttachmentDTO carAttachmentDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CarAttachment partially : {}, {}", id, carAttachmentDTO);
        if (carAttachmentDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carAttachmentDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carAttachmentRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CarAttachmentDTO> result = carAttachmentService.partialUpdate(carAttachmentDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carAttachmentDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /car-attachments} : get all the carAttachments.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of carAttachments in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CarAttachmentDTO>> getAllCarAttachments(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of CarAttachments");
        Page<CarAttachmentDTO> page = carAttachmentService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /car-attachments/:id} : get the "id" carAttachment.
     *
     * @param id the id of the carAttachmentDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the carAttachmentDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CarAttachmentDTO> getCarAttachment(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CarAttachment : {}", id);
        Optional<CarAttachmentDTO> carAttachmentDTO = carAttachmentService.findOne(id);
        return ResponseUtil.wrapOrNotFound(carAttachmentDTO);
    }

    /**
     * {@code DELETE  /car-attachments/:id} : delete the "id" carAttachment.
     *
     * @param id the id of the carAttachmentDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarAttachment(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CarAttachment : {}", id);
        carAttachmentService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /car-attachments/_search?query=:query} : search for the carAttachment corresponding
     * to the query.
     *
     * @param query the query of the carAttachment search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<CarAttachmentDTO>> searchCarAttachments(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of CarAttachments for query {}", query);
        try {
            Page<CarAttachmentDTO> page = carAttachmentService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
