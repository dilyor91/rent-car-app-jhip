package uz.carapp.rentcarapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uz.carapp.rentcarapp.domain.CarClassAsserts.*;
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
import uz.carapp.rentcarapp.domain.CarClass;
import uz.carapp.rentcarapp.repository.CarClassRepository;
import uz.carapp.rentcarapp.repository.search.CarClassSearchRepository;
import uz.carapp.rentcarapp.service.dto.CarClassDTO;
import uz.carapp.rentcarapp.service.mapper.CarClassMapper;

/**
 * Integration tests for the {@link CarClassResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CarClassResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final String ENTITY_API_URL = "/api/car-classes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/car-classes/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CarClassRepository carClassRepository;

    @Autowired
    private CarClassMapper carClassMapper;

    @Autowired
    private CarClassSearchRepository carClassSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCarClassMockMvc;

    private CarClass carClass;

    private CarClass insertedCarClass;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CarClass createEntity() {
        return new CarClass().name(DEFAULT_NAME).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CarClass createUpdatedEntity() {
        return new CarClass().name(UPDATED_NAME).status(UPDATED_STATUS);
    }

    @BeforeEach
    public void initTest() {
        carClass = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedCarClass != null) {
            carClassRepository.delete(insertedCarClass);
            carClassSearchRepository.delete(insertedCarClass);
            insertedCarClass = null;
        }
    }

    @Test
    @Transactional
    void createCarClass() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carClassSearchRepository.findAll());
        // Create the CarClass
        CarClassDTO carClassDTO = carClassMapper.toDto(carClass);
        var returnedCarClassDTO = om.readValue(
            restCarClassMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carClassDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CarClassDTO.class
        );

        // Validate the CarClass in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCarClass = carClassMapper.toEntity(returnedCarClassDTO);
        assertCarClassUpdatableFieldsEquals(returnedCarClass, getPersistedCarClass(returnedCarClass));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(carClassSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedCarClass = returnedCarClass;
    }

    @Test
    @Transactional
    void createCarClassWithExistingId() throws Exception {
        // Create the CarClass with an existing ID
        carClass.setId(1L);
        CarClassDTO carClassDTO = carClassMapper.toDto(carClass);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carClassSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCarClassMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carClassDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CarClass in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carClassSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carClassSearchRepository.findAll());
        // set the field null
        carClass.setName(null);

        // Create the CarClass, which fails.
        CarClassDTO carClassDTO = carClassMapper.toDto(carClass);

        restCarClassMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carClassDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carClassSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCarClasses() throws Exception {
        // Initialize the database
        insertedCarClass = carClassRepository.saveAndFlush(carClass);

        // Get all the carClassList
        restCarClassMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carClass.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @Test
    @Transactional
    void getCarClass() throws Exception {
        // Initialize the database
        insertedCarClass = carClassRepository.saveAndFlush(carClass);

        // Get the carClass
        restCarClassMockMvc
            .perform(get(ENTITY_API_URL_ID, carClass.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(carClass.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    void getNonExistingCarClass() throws Exception {
        // Get the carClass
        restCarClassMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCarClass() throws Exception {
        // Initialize the database
        insertedCarClass = carClassRepository.saveAndFlush(carClass);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        carClassSearchRepository.save(carClass);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carClassSearchRepository.findAll());

        // Update the carClass
        CarClass updatedCarClass = carClassRepository.findById(carClass.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCarClass are not directly saved in db
        em.detach(updatedCarClass);
        updatedCarClass.name(UPDATED_NAME).status(UPDATED_STATUS);
        CarClassDTO carClassDTO = carClassMapper.toDto(updatedCarClass);

        restCarClassMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carClassDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carClassDTO))
            )
            .andExpect(status().isOk());

        // Validate the CarClass in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCarClassToMatchAllProperties(updatedCarClass);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(carClassSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<CarClass> carClassSearchList = Streamable.of(carClassSearchRepository.findAll()).toList();
                CarClass testCarClassSearch = carClassSearchList.get(searchDatabaseSizeAfter - 1);

                assertCarClassAllPropertiesEquals(testCarClassSearch, updatedCarClass);
            });
    }

    @Test
    @Transactional
    void putNonExistingCarClass() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carClassSearchRepository.findAll());
        carClass.setId(longCount.incrementAndGet());

        // Create the CarClass
        CarClassDTO carClassDTO = carClassMapper.toDto(carClass);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarClassMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carClassDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carClassDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarClass in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carClassSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCarClass() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carClassSearchRepository.findAll());
        carClass.setId(longCount.incrementAndGet());

        // Create the CarClass
        CarClassDTO carClassDTO = carClassMapper.toDto(carClass);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarClassMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carClassDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarClass in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carClassSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCarClass() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carClassSearchRepository.findAll());
        carClass.setId(longCount.incrementAndGet());

        // Create the CarClass
        CarClassDTO carClassDTO = carClassMapper.toDto(carClass);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarClassMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carClassDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CarClass in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carClassSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCarClassWithPatch() throws Exception {
        // Initialize the database
        insertedCarClass = carClassRepository.saveAndFlush(carClass);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carClass using partial update
        CarClass partialUpdatedCarClass = new CarClass();
        partialUpdatedCarClass.setId(carClass.getId());

        partialUpdatedCarClass.name(UPDATED_NAME);

        restCarClassMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarClass.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCarClass))
            )
            .andExpect(status().isOk());

        // Validate the CarClass in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCarClassUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCarClass, carClass), getPersistedCarClass(carClass));
    }

    @Test
    @Transactional
    void fullUpdateCarClassWithPatch() throws Exception {
        // Initialize the database
        insertedCarClass = carClassRepository.saveAndFlush(carClass);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carClass using partial update
        CarClass partialUpdatedCarClass = new CarClass();
        partialUpdatedCarClass.setId(carClass.getId());

        partialUpdatedCarClass.name(UPDATED_NAME).status(UPDATED_STATUS);

        restCarClassMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarClass.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCarClass))
            )
            .andExpect(status().isOk());

        // Validate the CarClass in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCarClassUpdatableFieldsEquals(partialUpdatedCarClass, getPersistedCarClass(partialUpdatedCarClass));
    }

    @Test
    @Transactional
    void patchNonExistingCarClass() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carClassSearchRepository.findAll());
        carClass.setId(longCount.incrementAndGet());

        // Create the CarClass
        CarClassDTO carClassDTO = carClassMapper.toDto(carClass);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarClassMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, carClassDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(carClassDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarClass in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carClassSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCarClass() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carClassSearchRepository.findAll());
        carClass.setId(longCount.incrementAndGet());

        // Create the CarClass
        CarClassDTO carClassDTO = carClassMapper.toDto(carClass);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarClassMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(carClassDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarClass in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carClassSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCarClass() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carClassSearchRepository.findAll());
        carClass.setId(longCount.incrementAndGet());

        // Create the CarClass
        CarClassDTO carClassDTO = carClassMapper.toDto(carClass);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarClassMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(carClassDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CarClass in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carClassSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCarClass() throws Exception {
        // Initialize the database
        insertedCarClass = carClassRepository.saveAndFlush(carClass);
        carClassRepository.save(carClass);
        carClassSearchRepository.save(carClass);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carClassSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the carClass
        restCarClassMockMvc
            .perform(delete(ENTITY_API_URL_ID, carClass.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carClassSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCarClass() throws Exception {
        // Initialize the database
        insertedCarClass = carClassRepository.saveAndFlush(carClass);
        carClassSearchRepository.save(carClass);

        // Search the carClass
        restCarClassMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + carClass.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carClass.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    protected long getRepositoryCount() {
        return carClassRepository.count();
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

    protected CarClass getPersistedCarClass(CarClass carClass) {
        return carClassRepository.findById(carClass.getId()).orElseThrow();
    }

    protected void assertPersistedCarClassToMatchAllProperties(CarClass expectedCarClass) {
        assertCarClassAllPropertiesEquals(expectedCarClass, getPersistedCarClass(expectedCarClass));
    }

    protected void assertPersistedCarClassToMatchUpdatableProperties(CarClass expectedCarClass) {
        assertCarClassAllUpdatablePropertiesEquals(expectedCarClass, getPersistedCarClass(expectedCarClass));
    }
}
