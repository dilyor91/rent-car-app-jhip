package uz.carapp.rentcarapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uz.carapp.rentcarapp.domain.CarMileageAsserts.*;
import static uz.carapp.rentcarapp.web.rest.TestUtil.createUpdateProxyForBean;
import static uz.carapp.rentcarapp.web.rest.TestUtil.sameNumber;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import uz.carapp.rentcarapp.domain.CarMileage;
import uz.carapp.rentcarapp.domain.enumeration.MileageEnum;
import uz.carapp.rentcarapp.repository.CarMileageRepository;
import uz.carapp.rentcarapp.repository.search.CarMileageSearchRepository;
import uz.carapp.rentcarapp.service.dto.CarMileageDTO;
import uz.carapp.rentcarapp.service.mapper.CarMileageMapper;

/**
 * Integration tests for the {@link CarMileageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CarMileageResourceIT {

    private static final BigDecimal DEFAULT_VALUE = new BigDecimal(1);
    private static final BigDecimal UPDATED_VALUE = new BigDecimal(2);

    private static final MileageEnum DEFAULT_UNIT = MileageEnum.KILOMETRES;
    private static final MileageEnum UPDATED_UNIT = MileageEnum.MILES;

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/car-mileages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/car-mileages/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CarMileageRepository carMileageRepository;

    @Autowired
    private CarMileageMapper carMileageMapper;

    @Autowired
    private CarMileageSearchRepository carMileageSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCarMileageMockMvc;

    private CarMileage carMileage;

    private CarMileage insertedCarMileage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CarMileage createEntity() {
        return new CarMileage().value(DEFAULT_VALUE).unit(DEFAULT_UNIT).date(DEFAULT_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CarMileage createUpdatedEntity() {
        return new CarMileage().value(UPDATED_VALUE).unit(UPDATED_UNIT).date(UPDATED_DATE);
    }

    @BeforeEach
    public void initTest() {
        carMileage = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedCarMileage != null) {
            carMileageRepository.delete(insertedCarMileage);
            carMileageSearchRepository.delete(insertedCarMileage);
            insertedCarMileage = null;
        }
    }

    @Test
    @Transactional
    void createCarMileage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carMileageSearchRepository.findAll());
        // Create the CarMileage
        CarMileageDTO carMileageDTO = carMileageMapper.toDto(carMileage);
        var returnedCarMileageDTO = om.readValue(
            restCarMileageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carMileageDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CarMileageDTO.class
        );

        // Validate the CarMileage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCarMileage = carMileageMapper.toEntity(returnedCarMileageDTO);
        assertCarMileageUpdatableFieldsEquals(returnedCarMileage, getPersistedCarMileage(returnedCarMileage));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(carMileageSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedCarMileage = returnedCarMileage;
    }

    @Test
    @Transactional
    void createCarMileageWithExistingId() throws Exception {
        // Create the CarMileage with an existing ID
        carMileage.setId(1L);
        CarMileageDTO carMileageDTO = carMileageMapper.toDto(carMileage);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carMileageSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCarMileageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carMileageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CarMileage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carMileageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCarMileages() throws Exception {
        // Initialize the database
        insertedCarMileage = carMileageRepository.saveAndFlush(carMileage);

        // Get all the carMileageList
        restCarMileageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carMileage.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(sameNumber(DEFAULT_VALUE))))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT.toString())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    void getCarMileage() throws Exception {
        // Initialize the database
        insertedCarMileage = carMileageRepository.saveAndFlush(carMileage);

        // Get the carMileage
        restCarMileageMockMvc
            .perform(get(ENTITY_API_URL_ID, carMileage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(carMileage.getId().intValue()))
            .andExpect(jsonPath("$.value").value(sameNumber(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.unit").value(DEFAULT_UNIT.toString()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCarMileage() throws Exception {
        // Get the carMileage
        restCarMileageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCarMileage() throws Exception {
        // Initialize the database
        insertedCarMileage = carMileageRepository.saveAndFlush(carMileage);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        carMileageSearchRepository.save(carMileage);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carMileageSearchRepository.findAll());

        // Update the carMileage
        CarMileage updatedCarMileage = carMileageRepository.findById(carMileage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCarMileage are not directly saved in db
        em.detach(updatedCarMileage);
        updatedCarMileage.value(UPDATED_VALUE).unit(UPDATED_UNIT).date(UPDATED_DATE);
        CarMileageDTO carMileageDTO = carMileageMapper.toDto(updatedCarMileage);

        restCarMileageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carMileageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carMileageDTO))
            )
            .andExpect(status().isOk());

        // Validate the CarMileage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCarMileageToMatchAllProperties(updatedCarMileage);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(carMileageSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<CarMileage> carMileageSearchList = Streamable.of(carMileageSearchRepository.findAll()).toList();
                CarMileage testCarMileageSearch = carMileageSearchList.get(searchDatabaseSizeAfter - 1);

                assertCarMileageAllPropertiesEquals(testCarMileageSearch, updatedCarMileage);
            });
    }

    @Test
    @Transactional
    void putNonExistingCarMileage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carMileageSearchRepository.findAll());
        carMileage.setId(longCount.incrementAndGet());

        // Create the CarMileage
        CarMileageDTO carMileageDTO = carMileageMapper.toDto(carMileage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarMileageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carMileageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carMileageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarMileage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carMileageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCarMileage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carMileageSearchRepository.findAll());
        carMileage.setId(longCount.incrementAndGet());

        // Create the CarMileage
        CarMileageDTO carMileageDTO = carMileageMapper.toDto(carMileage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarMileageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carMileageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarMileage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carMileageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCarMileage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carMileageSearchRepository.findAll());
        carMileage.setId(longCount.incrementAndGet());

        // Create the CarMileage
        CarMileageDTO carMileageDTO = carMileageMapper.toDto(carMileage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarMileageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carMileageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CarMileage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carMileageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCarMileageWithPatch() throws Exception {
        // Initialize the database
        insertedCarMileage = carMileageRepository.saveAndFlush(carMileage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carMileage using partial update
        CarMileage partialUpdatedCarMileage = new CarMileage();
        partialUpdatedCarMileage.setId(carMileage.getId());

        partialUpdatedCarMileage.value(UPDATED_VALUE).unit(UPDATED_UNIT);

        restCarMileageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarMileage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCarMileage))
            )
            .andExpect(status().isOk());

        // Validate the CarMileage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCarMileageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCarMileage, carMileage),
            getPersistedCarMileage(carMileage)
        );
    }

    @Test
    @Transactional
    void fullUpdateCarMileageWithPatch() throws Exception {
        // Initialize the database
        insertedCarMileage = carMileageRepository.saveAndFlush(carMileage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carMileage using partial update
        CarMileage partialUpdatedCarMileage = new CarMileage();
        partialUpdatedCarMileage.setId(carMileage.getId());

        partialUpdatedCarMileage.value(UPDATED_VALUE).unit(UPDATED_UNIT).date(UPDATED_DATE);

        restCarMileageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarMileage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCarMileage))
            )
            .andExpect(status().isOk());

        // Validate the CarMileage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCarMileageUpdatableFieldsEquals(partialUpdatedCarMileage, getPersistedCarMileage(partialUpdatedCarMileage));
    }

    @Test
    @Transactional
    void patchNonExistingCarMileage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carMileageSearchRepository.findAll());
        carMileage.setId(longCount.incrementAndGet());

        // Create the CarMileage
        CarMileageDTO carMileageDTO = carMileageMapper.toDto(carMileage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarMileageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, carMileageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(carMileageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarMileage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carMileageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCarMileage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carMileageSearchRepository.findAll());
        carMileage.setId(longCount.incrementAndGet());

        // Create the CarMileage
        CarMileageDTO carMileageDTO = carMileageMapper.toDto(carMileage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarMileageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(carMileageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarMileage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carMileageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCarMileage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carMileageSearchRepository.findAll());
        carMileage.setId(longCount.incrementAndGet());

        // Create the CarMileage
        CarMileageDTO carMileageDTO = carMileageMapper.toDto(carMileage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarMileageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(carMileageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CarMileage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carMileageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCarMileage() throws Exception {
        // Initialize the database
        insertedCarMileage = carMileageRepository.saveAndFlush(carMileage);
        carMileageRepository.save(carMileage);
        carMileageSearchRepository.save(carMileage);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carMileageSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the carMileage
        restCarMileageMockMvc
            .perform(delete(ENTITY_API_URL_ID, carMileage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carMileageSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCarMileage() throws Exception {
        // Initialize the database
        insertedCarMileage = carMileageRepository.saveAndFlush(carMileage);
        carMileageSearchRepository.save(carMileage);

        // Search the carMileage
        restCarMileageMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + carMileage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carMileage.getId().intValue())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(sameNumber(DEFAULT_VALUE))))
            .andExpect(jsonPath("$.[*].unit").value(hasItem(DEFAULT_UNIT.toString())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }

    protected long getRepositoryCount() {
        return carMileageRepository.count();
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

    protected CarMileage getPersistedCarMileage(CarMileage carMileage) {
        return carMileageRepository.findById(carMileage.getId()).orElseThrow();
    }

    protected void assertPersistedCarMileageToMatchAllProperties(CarMileage expectedCarMileage) {
        assertCarMileageAllPropertiesEquals(expectedCarMileage, getPersistedCarMileage(expectedCarMileage));
    }

    protected void assertPersistedCarMileageToMatchUpdatableProperties(CarMileage expectedCarMileage) {
        assertCarMileageAllUpdatablePropertiesEquals(expectedCarMileage, getPersistedCarMileage(expectedCarMileage));
    }
}
