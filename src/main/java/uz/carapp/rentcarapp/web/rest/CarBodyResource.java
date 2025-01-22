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
import uz.carapp.rentcarapp.repository.CarBodyRepository;
import uz.carapp.rentcarapp.service.CarBodyService;
import uz.carapp.rentcarapp.service.dto.CarBodyDTO;
import uz.carapp.rentcarapp.web.rest.errors.BadRequestAlertException;
import uz.carapp.rentcarapp.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link uz.carapp.rentcarapp.domain.CarBody}.
 */
@RestController
@RequestMapping("/api/car-bodies")
public class CarBodyResource {

    private static final Logger LOG = LoggerFactory.getLogger(CarBodyResource.class);

    private static final String ENTITY_NAME = "carBody";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CarBodyService carBodyService;

    private final CarBodyRepository carBodyRepository;

    public CarBodyResource(CarBodyService carBodyService, CarBodyRepository carBodyRepository) {
        this.carBodyService = carBodyService;
        this.carBodyRepository = carBodyRepository;
    }

    /**
     * {@code POST  /car-bodies} : Create a new carBody.
     *
     * @param carBodyDTO the carBodyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new carBodyDTO, or with status {@code 400 (Bad Request)} if the carBody has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CarBodyDTO> createCarBody(@Valid @RequestBody CarBodyDTO carBodyDTO) throws URISyntaxException {
        LOG.debug("REST request to save CarBody : {}", carBodyDTO);
        if (carBodyDTO.getId() != null) {
            throw new BadRequestAlertException("A new carBody cannot already have an ID", ENTITY_NAME, "idexists");
        }
        carBodyDTO = carBodyService.save(carBodyDTO);
        return ResponseEntity.created(new URI("/api/car-bodies/" + carBodyDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, carBodyDTO.getId().toString()))
            .body(carBodyDTO);
    }

    /**
     * {@code PUT  /car-bodies/:id} : Updates an existing carBody.
     *
     * @param id the id of the carBodyDTO to save.
     * @param carBodyDTO the carBodyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carBodyDTO,
     * or with status {@code 400 (Bad Request)} if the carBodyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the carBodyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CarBodyDTO> updateCarBody(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CarBodyDTO carBodyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CarBody : {}, {}", id, carBodyDTO);
        if (carBodyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carBodyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carBodyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        carBodyDTO = carBodyService.update(carBodyDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carBodyDTO.getId().toString()))
            .body(carBodyDTO);
    }

    /**
     * {@code PATCH  /car-bodies/:id} : Partial updates given fields of an existing carBody, field will ignore if it is null
     *
     * @param id the id of the carBodyDTO to save.
     * @param carBodyDTO the carBodyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carBodyDTO,
     * or with status {@code 400 (Bad Request)} if the carBodyDTO is not valid,
     * or with status {@code 404 (Not Found)} if the carBodyDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the carBodyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CarBodyDTO> partialUpdateCarBody(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CarBodyDTO carBodyDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CarBody partially : {}, {}", id, carBodyDTO);
        if (carBodyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carBodyDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carBodyRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CarBodyDTO> result = carBodyService.partialUpdate(carBodyDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carBodyDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /car-bodies} : get all the carBodies.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of carBodies in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CarBodyDTO>> getAllCarBodies(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of CarBodies");
        Page<CarBodyDTO> page = carBodyService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /car-bodies/:id} : get the "id" carBody.
     *
     * @param id the id of the carBodyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the carBodyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CarBodyDTO> getCarBody(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CarBody : {}", id);
        Optional<CarBodyDTO> carBodyDTO = carBodyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(carBodyDTO);
    }

    /**
     * {@code DELETE  /car-bodies/:id} : delete the "id" carBody.
     *
     * @param id the id of the carBodyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarBody(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CarBody : {}", id);
        carBodyService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /car-bodies/_search?query=:query} : search for the carBody corresponding
     * to the query.
     *
     * @param query the query of the carBody search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public ResponseEntity<List<CarBodyDTO>> searchCarBodies(
        @RequestParam("query") String query,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable
    ) {
        LOG.debug("REST request to search for a page of CarBodies for query {}", query);
        try {
            Page<CarBodyDTO> page = carBodyService.search(query, pageable);
            HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
            return ResponseEntity.ok().headers(headers).body(page.getContent());
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
