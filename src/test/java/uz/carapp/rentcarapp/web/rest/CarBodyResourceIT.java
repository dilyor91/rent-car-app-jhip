package uz.carapp.rentcarapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uz.carapp.rentcarapp.domain.CarBodyAsserts.*;
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
import uz.carapp.rentcarapp.domain.CarBody;
import uz.carapp.rentcarapp.repository.CarBodyRepository;
import uz.carapp.rentcarapp.repository.search.CarBodySearchRepository;
import uz.carapp.rentcarapp.service.dto.CarBodyDTO;
import uz.carapp.rentcarapp.service.mapper.CarBodyMapper;

/**
 * Integration tests for the {@link CarBodyResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CarBodyResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final String ENTITY_API_URL = "/api/car-bodies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/car-bodies/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CarBodyRepository carBodyRepository;

    @Autowired
    private CarBodyMapper carBodyMapper;

    @Autowired
    private CarBodySearchRepository carBodySearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCarBodyMockMvc;

    private CarBody carBody;

    private CarBody insertedCarBody;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CarBody createEntity() {
        return new CarBody().name(DEFAULT_NAME).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CarBody createUpdatedEntity() {
        return new CarBody().name(UPDATED_NAME).status(UPDATED_STATUS);
    }

    @BeforeEach
    public void initTest() {
        carBody = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedCarBody != null) {
            carBodyRepository.delete(insertedCarBody);
            carBodySearchRepository.delete(insertedCarBody);
            insertedCarBody = null;
        }
    }

    @Test
    @Transactional
    void createCarBody() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carBodySearchRepository.findAll());
        // Create the CarBody
        CarBodyDTO carBodyDTO = carBodyMapper.toDto(carBody);
        var returnedCarBodyDTO = om.readValue(
            restCarBodyMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carBodyDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CarBodyDTO.class
        );

        // Validate the CarBody in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCarBody = carBodyMapper.toEntity(returnedCarBodyDTO);
        assertCarBodyUpdatableFieldsEquals(returnedCarBody, getPersistedCarBody(returnedCarBody));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(carBodySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedCarBody = returnedCarBody;
    }

    @Test
    @Transactional
    void createCarBodyWithExistingId() throws Exception {
        // Create the CarBody with an existing ID
        carBody.setId(1L);
        CarBodyDTO carBodyDTO = carBodyMapper.toDto(carBody);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carBodySearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCarBodyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carBodyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CarBody in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carBodySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carBodySearchRepository.findAll());
        // set the field null
        carBody.setName(null);

        // Create the CarBody, which fails.
        CarBodyDTO carBodyDTO = carBodyMapper.toDto(carBody);

        restCarBodyMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carBodyDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carBodySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCarBodies() throws Exception {
        // Initialize the database
        insertedCarBody = carBodyRepository.saveAndFlush(carBody);

        // Get all the carBodyList
        restCarBodyMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carBody.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @Test
    @Transactional
    void getCarBody() throws Exception {
        // Initialize the database
        insertedCarBody = carBodyRepository.saveAndFlush(carBody);

        // Get the carBody
        restCarBodyMockMvc
            .perform(get(ENTITY_API_URL_ID, carBody.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(carBody.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    void getNonExistingCarBody() throws Exception {
        // Get the carBody
        restCarBodyMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCarBody() throws Exception {
        // Initialize the database
        insertedCarBody = carBodyRepository.saveAndFlush(carBody);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        carBodySearchRepository.save(carBody);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carBodySearchRepository.findAll());

        // Update the carBody
        CarBody updatedCarBody = carBodyRepository.findById(carBody.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCarBody are not directly saved in db
        em.detach(updatedCarBody);
        updatedCarBody.name(UPDATED_NAME).status(UPDATED_STATUS);
        CarBodyDTO carBodyDTO = carBodyMapper.toDto(updatedCarBody);

        restCarBodyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carBodyDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carBodyDTO))
            )
            .andExpect(status().isOk());

        // Validate the CarBody in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCarBodyToMatchAllProperties(updatedCarBody);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(carBodySearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<CarBody> carBodySearchList = Streamable.of(carBodySearchRepository.findAll()).toList();
                CarBody testCarBodySearch = carBodySearchList.get(searchDatabaseSizeAfter - 1);

                assertCarBodyAllPropertiesEquals(testCarBodySearch, updatedCarBody);
            });
    }

    @Test
    @Transactional
    void putNonExistingCarBody() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carBodySearchRepository.findAll());
        carBody.setId(longCount.incrementAndGet());

        // Create the CarBody
        CarBodyDTO carBodyDTO = carBodyMapper.toDto(carBody);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarBodyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carBodyDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carBodyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarBody in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carBodySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCarBody() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carBodySearchRepository.findAll());
        carBody.setId(longCount.incrementAndGet());

        // Create the CarBody
        CarBodyDTO carBodyDTO = carBodyMapper.toDto(carBody);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarBodyMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carBodyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarBody in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carBodySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCarBody() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carBodySearchRepository.findAll());
        carBody.setId(longCount.incrementAndGet());

        // Create the CarBody
        CarBodyDTO carBodyDTO = carBodyMapper.toDto(carBody);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarBodyMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carBodyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CarBody in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carBodySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCarBodyWithPatch() throws Exception {
        // Initialize the database
        insertedCarBody = carBodyRepository.saveAndFlush(carBody);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carBody using partial update
        CarBody partialUpdatedCarBody = new CarBody();
        partialUpdatedCarBody.setId(carBody.getId());

        partialUpdatedCarBody.status(UPDATED_STATUS);

        restCarBodyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarBody.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCarBody))
            )
            .andExpect(status().isOk());

        // Validate the CarBody in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCarBodyUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCarBody, carBody), getPersistedCarBody(carBody));
    }

    @Test
    @Transactional
    void fullUpdateCarBodyWithPatch() throws Exception {
        // Initialize the database
        insertedCarBody = carBodyRepository.saveAndFlush(carBody);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carBody using partial update
        CarBody partialUpdatedCarBody = new CarBody();
        partialUpdatedCarBody.setId(carBody.getId());

        partialUpdatedCarBody.name(UPDATED_NAME).status(UPDATED_STATUS);

        restCarBodyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarBody.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCarBody))
            )
            .andExpect(status().isOk());

        // Validate the CarBody in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCarBodyUpdatableFieldsEquals(partialUpdatedCarBody, getPersistedCarBody(partialUpdatedCarBody));
    }

    @Test
    @Transactional
    void patchNonExistingCarBody() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carBodySearchRepository.findAll());
        carBody.setId(longCount.incrementAndGet());

        // Create the CarBody
        CarBodyDTO carBodyDTO = carBodyMapper.toDto(carBody);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarBodyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, carBodyDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(carBodyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarBody in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carBodySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCarBody() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carBodySearchRepository.findAll());
        carBody.setId(longCount.incrementAndGet());

        // Create the CarBody
        CarBodyDTO carBodyDTO = carBodyMapper.toDto(carBody);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarBodyMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(carBodyDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarBody in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carBodySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCarBody() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carBodySearchRepository.findAll());
        carBody.setId(longCount.incrementAndGet());

        // Create the CarBody
        CarBodyDTO carBodyDTO = carBodyMapper.toDto(carBody);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarBodyMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(carBodyDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CarBody in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carBodySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCarBody() throws Exception {
        // Initialize the database
        insertedCarBody = carBodyRepository.saveAndFlush(carBody);
        carBodyRepository.save(carBody);
        carBodySearchRepository.save(carBody);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carBodySearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the carBody
        restCarBodyMockMvc
            .perform(delete(ENTITY_API_URL_ID, carBody.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carBodySearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCarBody() throws Exception {
        // Initialize the database
        insertedCarBody = carBodyRepository.saveAndFlush(carBody);
        carBodySearchRepository.save(carBody);

        // Search the carBody
        restCarBodyMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + carBody.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carBody.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    protected long getRepositoryCount() {
        return carBodyRepository.count();
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

    protected CarBody getPersistedCarBody(CarBody carBody) {
        return carBodyRepository.findById(carBody.getId()).orElseThrow();
    }

    protected void assertPersistedCarBodyToMatchAllProperties(CarBody expectedCarBody) {
        assertCarBodyAllPropertiesEquals(expectedCarBody, getPersistedCarBody(expectedCarBody));
    }

    protected void assertPersistedCarBodyToMatchUpdatableProperties(CarBody expectedCarBody) {
        assertCarBodyAllUpdatablePropertiesEquals(expectedCarBody, getPersistedCarBody(expectedCarBody));
    }
}
