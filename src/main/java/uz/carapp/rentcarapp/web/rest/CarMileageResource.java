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
import uz.carapp.rentcarapp.repository.CarMileageRepository;
import uz.carapp.rentcarapp.service.CarMileageService;
import uz.carapp.rentcarapp.service.dto.CarMileageDTO;
import uz.carapp.rentcarapp.web.rest.errors.BadRequestAlertException;
import uz.carapp.rentcarapp.web.rest.errors.ElasticsearchExceptionMapper;

/**
 * REST controller for managing {@link uz.carapp.rentcarapp.domain.CarMileage}.
 */
@RestController
@RequestMapping("/api/car-mileages")
public class CarMileageResource {

    private static final Logger LOG = LoggerFactory.getLogger(CarMileageResource.class);

    private static final String ENTITY_NAME = "carMileage";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CarMileageService carMileageService;

    private final CarMileageRepository carMileageRepository;

    public CarMileageResource(CarMileageService carMileageService, CarMileageRepository carMileageRepository) {
        this.carMileageService = carMileageService;
        this.carMileageRepository = carMileageRepository;
    }

    /**
     * {@code POST  /car-mileages} : Create a new carMileage.
     *
     * @param carMileageDTO the carMileageDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new carMileageDTO, or with status {@code 400 (Bad Request)} if the carMileage has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CarMileageDTO> createCarMileage(@RequestBody CarMileageDTO carMileageDTO) throws URISyntaxException {
        LOG.debug("REST request to save CarMileage : {}", carMileageDTO);
        if (carMileageDTO.getId() != null) {
            throw new BadRequestAlertException("A new carMileage cannot already have an ID", ENTITY_NAME, "idexists");
        }
        carMileageDTO = carMileageService.save(carMileageDTO);
        return ResponseEntity.created(new URI("/api/car-mileages/" + carMileageDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, carMileageDTO.getId().toString()))
            .body(carMileageDTO);
    }

    /**
     * {@code PUT  /car-mileages/:id} : Updates an existing carMileage.
     *
     * @param id the id of the carMileageDTO to save.
     * @param carMileageDTO the carMileageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carMileageDTO,
     * or with status {@code 400 (Bad Request)} if the carMileageDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the carMileageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CarMileageDTO> updateCarMileage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CarMileageDTO carMileageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CarMileage : {}, {}", id, carMileageDTO);
        if (carMileageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carMileageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carMileageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        carMileageDTO = carMileageService.update(carMileageDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carMileageDTO.getId().toString()))
            .body(carMileageDTO);
    }

    /**
     * {@code PATCH  /car-mileages/:id} : Partial updates given fields of an existing carMileage, field will ignore if it is null
     *
     * @param id the id of the carMileageDTO to save.
     * @param carMileageDTO the carMileageDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated carMileageDTO,
     * or with status {@code 400 (Bad Request)} if the carMileageDTO is not valid,
     * or with status {@code 404 (Not Found)} if the carMileageDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the carMileageDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CarMileageDTO> partialUpdateCarMileage(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CarMileageDTO carMileageDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CarMileage partially : {}, {}", id, carMileageDTO);
        if (carMileageDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, carMileageDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!carMileageRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CarMileageDTO> result = carMileageService.partialUpdate(carMileageDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, carMileageDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /car-mileages} : get all the carMileages.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of carMileages in body.
     */
    @GetMapping("")
    public List<CarMileageDTO> getAllCarMileages() {
        LOG.debug("REST request to get all CarMileages");
        return carMileageService.findAll();
    }

    /**
     * {@code GET  /car-mileages/:id} : get the "id" carMileage.
     *
     * @param id the id of the carMileageDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the carMileageDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CarMileageDTO> getCarMileage(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CarMileage : {}", id);
        Optional<CarMileageDTO> carMileageDTO = carMileageService.findOne(id);
        return ResponseUtil.wrapOrNotFound(carMileageDTO);
    }

    /**
     * {@code DELETE  /car-mileages/:id} : delete the "id" carMileage.
     *
     * @param id the id of the carMileageDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCarMileage(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CarMileage : {}", id);
        carMileageService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /car-mileages/_search?query=:query} : search for the carMileage corresponding
     * to the query.
     *
     * @param query the query of the carMileage search.
     * @return the result of the search.
     */
    @GetMapping("/_search")
    public List<CarMileageDTO> searchCarMileages(@RequestParam("query") String query) {
        LOG.debug("REST request to search CarMileages for query {}", query);
        try {
            return carMileageService.search(query);
        } catch (RuntimeException e) {
            throw ElasticsearchExceptionMapper.mapException(e);
        }
    }
}
