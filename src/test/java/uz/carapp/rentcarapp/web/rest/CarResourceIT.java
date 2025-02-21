package uz.carapp.rentcarapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uz.carapp.rentcarapp.domain.CarAsserts.*;
import static uz.carapp.rentcarapp.web.rest.TestUtil.createUpdateProxyForBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.util.Streamable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import uz.carapp.rentcarapp.IntegrationTest;
import uz.carapp.rentcarapp.domain.Car;
import uz.carapp.rentcarapp.repository.CarRepository;
import uz.carapp.rentcarapp.repository.search.CarSearchRepository;
import uz.carapp.rentcarapp.service.dto.CarDTO;
import uz.carapp.rentcarapp.service.mapper.CarMapper;

/**
 * Integration tests for the {@link CarResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CarResourceIT {

    private static final Integer DEFAULT_STATE_NUMBER_PLATE = 1;
    private static final Integer UPDATED_STATE_NUMBER_PLATE = 2;

    private static final Integer DEFAULT_DEPOSIT = 1;
    private static final Integer UPDATED_DEPOSIT = 2;

    private static final String ENTITY_API_URL = "/api/cars";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/cars/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarMapper carMapper;

    @Autowired
    private CarSearchRepository carSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCarMockMvc;

    private Car car;

    private Car insertedCar;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Car createEntity() {
        return new Car().stateNumberPlate(DEFAULT_STATE_NUMBER_PLATE).deposit(DEFAULT_DEPOSIT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Car createUpdatedEntity() {
        return new Car().stateNumberPlate(UPDATED_STATE_NUMBER_PLATE).deposit(UPDATED_DEPOSIT);
    }

    @BeforeEach
    public void initTest() {
        car = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedCar != null) {
            carRepository.delete(insertedCar);
            carSearchRepository.delete(insertedCar);
            insertedCar = null;
        }
    }

    @Test
    @Transactional
    void createCar() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll());
        // Create the Car
        CarDTO carDTO = carMapper.toDto(car);
        var returnedCarDTO = om.readValue(
            restCarMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CarDTO.class
        );

        // Validate the Car in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCar = carMapper.toEntity(returnedCarDTO);
        assertCarUpdatableFieldsEquals(returnedCar, getPersistedCar(returnedCar));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedCar = returnedCar;
    }

    @Test
    @Transactional
    void createCarWithExistingId() throws Exception {
        // Create the Car with an existing ID
        car.setId(1L);
        CarDTO carDTO = carMapper.toDto(car);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCarMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Car in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCars() throws Exception {
        // Initialize the database
        insertedCar = carRepository.saveAndFlush(car);

        // Get all the carList
        restCarMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(car.getId().intValue())))
            .andExpect(jsonPath("$.[*].stateNumberPlate").value(hasItem(DEFAULT_STATE_NUMBER_PLATE)))
            .andExpect(jsonPath("$.[*].deposit").value(hasItem(DEFAULT_DEPOSIT)));
    }

    @Test
    @Transactional
    void getCar() throws Exception {
        // Initialize the database
        insertedCar = carRepository.saveAndFlush(car);

        // Get the car
        restCarMockMvc
            .perform(get(ENTITY_API_URL_ID, car.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(car.getId().intValue()))
            .andExpect(jsonPath("$.stateNumberPlate").value(DEFAULT_STATE_NUMBER_PLATE))
            .andExpect(jsonPath("$.deposit").value(DEFAULT_DEPOSIT));
    }

    @Test
    @Transactional
    void getNonExistingCar() throws Exception {
        // Get the car
        restCarMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCar() throws Exception {
        // Initialize the database
        insertedCar = carRepository.saveAndFlush(car);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        carSearchRepository.save(car);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll());

        // Update the car
        Car updatedCar = carRepository.findById(car.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCar are not directly saved in db
        em.detach(updatedCar);
        updatedCar.stateNumberPlate(UPDATED_STATE_NUMBER_PLATE).deposit(UPDATED_DEPOSIT);
        CarDTO carDTO = carMapper.toDto(updatedCar);

        restCarMockMvc
            .perform(put(ENTITY_API_URL_ID, carDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carDTO)))
            .andExpect(status().isOk());

        // Validate the Car in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCarToMatchAllProperties(updatedCar);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Car> carSearchList = Streamable.of(carSearchRepository.findAll()).toList();
                Car testCarSearch = carSearchList.get(searchDatabaseSizeAfter - 1);

                assertCarAllPropertiesEquals(testCarSearch, updatedCar);
            });
    }

    @Test
    @Transactional
    void putNonExistingCar() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll());
        car.setId(longCount.incrementAndGet());

        // Create the Car
        CarDTO carDTO = carMapper.toDto(car);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarMockMvc
            .perform(put(ENTITY_API_URL_ID, carDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Car in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCar() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll());
        car.setId(longCount.incrementAndGet());

        // Create the Car
        CarDTO carDTO = carMapper.toDto(car);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Car in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCar() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll());
        car.setId(longCount.incrementAndGet());

        // Create the Car
        CarDTO carDTO = carMapper.toDto(car);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Car in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCarWithPatch() throws Exception {
        // Initialize the database
        insertedCar = carRepository.saveAndFlush(car);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the car using partial update
        Car partialUpdatedCar = new Car();
        partialUpdatedCar.setId(car.getId());

        partialUpdatedCar.stateNumberPlate(UPDATED_STATE_NUMBER_PLATE);

        restCarMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCar.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCar))
            )
            .andExpect(status().isOk());

        // Validate the Car in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCarUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCar, car), getPersistedCar(car));
    }

    @Test
    @Transactional
    void fullUpdateCarWithPatch() throws Exception {
        // Initialize the database
        insertedCar = carRepository.saveAndFlush(car);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the car using partial update
        Car partialUpdatedCar = new Car();
        partialUpdatedCar.setId(car.getId());

        partialUpdatedCar.stateNumberPlate(UPDATED_STATE_NUMBER_PLATE).deposit(UPDATED_DEPOSIT);

        restCarMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCar.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCar))
            )
            .andExpect(status().isOk());

        // Validate the Car in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCarUpdatableFieldsEquals(partialUpdatedCar, getPersistedCar(partialUpdatedCar));
    }

    @Test
    @Transactional
    void patchNonExistingCar() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll());
        car.setId(longCount.incrementAndGet());

        // Create the Car
        CarDTO carDTO = carMapper.toDto(car);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, carDTO.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(carDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Car in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCar() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll());
        car.setId(longCount.incrementAndGet());

        // Create the Car
        CarDTO carDTO = carMapper.toDto(car);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(carDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Car in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCar() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll());
        car.setId(longCount.incrementAndGet());

        // Create the Car
        CarDTO carDTO = carMapper.toDto(car);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(carDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Car in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCar() throws Exception {
        // Initialize the database
        insertedCar = carRepository.saveAndFlush(car);
        carRepository.save(car);
        carSearchRepository.save(car);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the car
        restCarMockMvc.perform(delete(ENTITY_API_URL_ID, car.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCar() throws Exception {
        // Initialize the database
        insertedCar = carRepository.saveAndFlush(car);
        carSearchRepository.save(car);

        // Search the car
        restCarMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + car.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(car.getId().intValue())))
            .andExpect(jsonPath("$.[*].stateNumberPlate").value(hasItem(DEFAULT_STATE_NUMBER_PLATE)))
            .andExpect(jsonPath("$.[*].deposit").value(hasItem(DEFAULT_DEPOSIT)));
    }

    protected long getRepositoryCount() {
        return carRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Car getPersistedCar(Car car) {
        return carRepository.findById(car.getId()).orElseThrow();
    }

    protected void assertPersistedCarToMatchAllProperties(Car expectedCar) {
        assertCarAllPropertiesEquals(expectedCar, getPersistedCar(expectedCar));
    }

    protected void assertPersistedCarToMatchUpdatableProperties(Car expectedCar) {
        assertCarAllUpdatablePropertiesEquals(expectedCar, getPersistedCar(expectedCar));
    }
}
