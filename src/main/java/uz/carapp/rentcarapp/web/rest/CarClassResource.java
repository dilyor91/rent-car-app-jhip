package uz.carapp.rentcarapp.web.rest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
import uz.carapp.rentcarapp.repository.CarClassRepository;
import uz.carapp.rentcarapp.service.CarClassService;
import uz.carapp.rentcarapp.service.dto.CarClassDTO;
import uz.carapp.rentcarapp.web.rest.errors.BadRequestAlertException;
import uz.carapp.rentcarapp.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link uz.carapp.rentcarapp.domain.CarClass}.
 */
@RestController
@RequestMapping("/api/car-classes")
public class CarClassResource {

    private static final Logger LOG = LoggerFactory.getLogger(CarClassResource.class);

    private static final String ENTITY_NAME = "carClass";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CarClassService carClassService;

    private final CarClassRepository carClassRepository;

    public CarClassResource(CarClassService carClassService, CarClassRepository carClassRepository) {
        this.carClassService = carClassService;
        this.carClassRepository = carClassRepository;
    }

    /**
     * {@code POST  /car-classes} : Create a new carClass.
     *
     * @param carClassDTO the carClassDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new carClassDTO, or with status {@code 400 (Bad Request)} if the carClass has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CarClassDTO> createCarClass(@Valid @RequestBody CarClassDTO carClassDTO) throws URISyntaxException {
        LOG.debug("REST request to save CarClass : {}", carClassDTO);
        if (carClassDTO.getId() != null) {
            throw new BadRequestAlertException("A new carClass cannot already have an ID", ENTITY_NAME, "idexists");
        }
        carClassDTO = carClassService.save(carClassDTO);
        return ResponseEntity.created(new URI("/api/car-classes/" + carClassDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, carClassDTO.getId().toString()))
            .body(carClassDTO);
    }

    /**
     * {@code PUT  /car-classes/:id} : Updates an existing carClass.
     *
     * @param id the id of the carClassDTO to save.
     * @param carClassDTO the carClassDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carClassDTO,
     * or with status {@code 400 (Bad Request)} if the carClassDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the carClassDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CarClassDTO> updateCarClass(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CarClassDTO carClassDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CarClass : {}, {}", id, carClassDTO);
        if (carClassDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carClassDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carClassRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        carClassDTO = carClassService.update(carClassDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carClassDTO.getId().toString()))
            .body(carClassDTO);
    }

    /**
     * {@code PATCH  /car-classes/:id} : Partial updates given fields of an existing carClass, field will ignore if it is null
     *
     * @param id the id of the carClassDTO to save.
     * @param carClassDTO the carClassDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carClassDTO,
     * or with status {@code 400 (Bad Request)} if the carClassDTO is not valid,
     * or with status {@code 404 (Not Found)} if the carClassDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the carClassDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CarClassDTO> partialUpdateCarClass(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CarClassDTO carClassDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CarClass partially : {}, {}", id, carClassDTO);
        if (carClassDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carClassDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carClassRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CarClassDTO> result = carClassService.partialUpdate(carClassDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carClassDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /car-classes} : get all the carClasses.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of carClasses in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CarClassDTO>> getAllCarClasses(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of CarClasses");
        Page<CarClassDTO> page = carClassService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /car-classes/:id} : get the "id" carClass.
     *
     * @param id the id of the carClassDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the carClassDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CarClassDTO> getCarClass(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CarClass : {}", id);
        Optional<CarClassDTO> carClassDTO = carClassService.findOne(id);
        return ResponseUtil.wrapOrNotFound(carClassDTO);
    }

    /**
     * {@code DELETE  /car-classes/:id} : delete the "id" carClass.
     *
     * @param id the id of the carClassDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarClass(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CarClass : {}", id);
        carClassService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /car-classes/_search?query=:query} : search for the carClass corresponding
     * to the query.
     *
     * @param query the query of the carClass search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<CarClassDTO>> searchCarClasses(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of CarClasses for query {}", query);
        try {
            Page<CarClassDTO> page = carClassService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
