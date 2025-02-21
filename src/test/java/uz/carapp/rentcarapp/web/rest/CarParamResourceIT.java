package uz.carapp.rentcarapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uz.carapp.rentcarapp.domain.CarParamAsserts.*;
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
import uz.carapp.rentcarapp.domain.CarParam;
import uz.carapp.rentcarapp.repository.CarParamRepository;
import uz.carapp.rentcarapp.repository.search.CarParamSearchRepository;
import uz.carapp.rentcarapp.service.dto.CarParamDTO;
import uz.carapp.rentcarapp.service.mapper.CarParamMapper;

/**
 * Integration tests for the {@link CarParamResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CarParamResourceIT {

    private static final String DEFAULT_PARAM_ITEM_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_PARAM_ITEM_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_PARAM_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_PARAM_VALUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/car-params";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/car-params/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CarParamRepository carParamRepository;

    @Autowired
    private CarParamMapper carParamMapper;

    @Autowired
    private CarParamSearchRepository carParamSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCarParamMockMvc;

    private CarParam carParam;

    private CarParam insertedCarParam;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CarParam createEntity() {
        return new CarParam().paramItemValue(DEFAULT_PARAM_ITEM_VALUE).paramValue(DEFAULT_PARAM_VALUE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CarParam createUpdatedEntity() {
        return new CarParam().paramItemValue(UPDATED_PARAM_ITEM_VALUE).paramValue(UPDATED_PARAM_VALUE);
    }

    @BeforeEach
    public void initTest() {
        carParam = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedCarParam != null) {
            carParamRepository.delete(insertedCarParam);
            carParamSearchRepository.delete(insertedCarParam);
            insertedCarParam = null;
        }
    }

    @Test
    @Transactional
    void createCarParam() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carParamSearchRepository.findAll());
        // Create the CarParam
        CarParamDTO carParamDTO = carParamMapper.toDto(carParam);
        var returnedCarParamDTO = om.readValue(
            restCarParamMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carParamDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CarParamDTO.class
        );

        // Validate the CarParam in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCarParam = carParamMapper.toEntity(returnedCarParamDTO);
        assertCarParamUpdatableFieldsEquals(returnedCarParam, getPersistedCarParam(returnedCarParam));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(carParamSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedCarParam = returnedCarParam;
    }

    @Test
    @Transactional
    void createCarParamWithExistingId() throws Exception {
        // Create the CarParam with an existing ID
        carParam.setId(1L);
        CarParamDTO carParamDTO = carParamMapper.toDto(carParam);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carParamSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCarParamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carParamDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CarParam in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carParamSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCarParams() throws Exception {
        // Initialize the database
        insertedCarParam = carParamRepository.saveAndFlush(carParam);

        // Get all the carParamList
        restCarParamMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carParam.getId().intValue())))
            .andExpect(jsonPath("$.[*].paramItemValue").value(hasItem(DEFAULT_PARAM_ITEM_VALUE)))
            .andExpect(jsonPath("$.[*].paramValue").value(hasItem(DEFAULT_PARAM_VALUE)));
    }

    @Test
    @Transactional
    void getCarParam() throws Exception {
        // Initialize the database
        insertedCarParam = carParamRepository.saveAndFlush(carParam);

        // Get the carParam
        restCarParamMockMvc
            .perform(get(ENTITY_API_URL_ID, carParam.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(carParam.getId().intValue()))
            .andExpect(jsonPath("$.paramItemValue").value(DEFAULT_PARAM_ITEM_VALUE))
            .andExpect(jsonPath("$.paramValue").value(DEFAULT_PARAM_VALUE));
    }

    @Test
    @Transactional
    void getNonExistingCarParam() throws Exception {
        // Get the carParam
        restCarParamMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCarParam() throws Exception {
        // Initialize the database
        insertedCarParam = carParamRepository.saveAndFlush(carParam);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        carParamSearchRepository.save(carParam);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carParamSearchRepository.findAll());

        // Update the carParam
        CarParam updatedCarParam = carParamRepository.findById(carParam.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCarParam are not directly saved in db
        em.detach(updatedCarParam);
        updatedCarParam.paramItemValue(UPDATED_PARAM_ITEM_VALUE).paramValue(UPDATED_PARAM_VALUE);
        CarParamDTO carParamDTO = carParamMapper.toDto(updatedCarParam);

        restCarParamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carParamDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carParamDTO))
            )
            .andExpect(status().isOk());

        // Validate the CarParam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCarParamToMatchAllProperties(updatedCarParam);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(carParamSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<CarParam> carParamSearchList = Streamable.of(carParamSearchRepository.findAll()).toList();
                CarParam testCarParamSearch = carParamSearchList.get(searchDatabaseSizeAfter - 1);

                assertCarParamAllPropertiesEquals(testCarParamSearch, updatedCarParam);
            });
    }

    @Test
    @Transactional
    void putNonExistingCarParam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carParamSearchRepository.findAll());
        carParam.setId(longCount.incrementAndGet());

        // Create the CarParam
        CarParamDTO carParamDTO = carParamMapper.toDto(carParam);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarParamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carParamDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carParamDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarParam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carParamSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCarParam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carParamSearchRepository.findAll());
        carParam.setId(longCount.incrementAndGet());

        // Create the CarParam
        CarParamDTO carParamDTO = carParamMapper.toDto(carParam);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarParamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carParamDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarParam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carParamSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCarParam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carParamSearchRepository.findAll());
        carParam.setId(longCount.incrementAndGet());

        // Create the CarParam
        CarParamDTO carParamDTO = carParamMapper.toDto(carParam);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarParamMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carParamDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CarParam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carParamSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCarParamWithPatch() throws Exception {
        // Initialize the database
        insertedCarParam = carParamRepository.saveAndFlush(carParam);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carParam using partial update
        CarParam partialUpdatedCarParam = new CarParam();
        partialUpdatedCarParam.setId(carParam.getId());

        partialUpdatedCarParam.paramItemValue(UPDATED_PARAM_ITEM_VALUE);

        restCarParamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarParam.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCarParam))
            )
            .andExpect(status().isOk());

        // Validate the CarParam in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCarParamUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCarParam, carParam), getPersistedCarParam(carParam));
    }

    @Test
    @Transactional
    void fullUpdateCarParamWithPatch() throws Exception {
        // Initialize the database
        insertedCarParam = carParamRepository.saveAndFlush(carParam);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carParam using partial update
        CarParam partialUpdatedCarParam = new CarParam();
        partialUpdatedCarParam.setId(carParam.getId());

        partialUpdatedCarParam.paramItemValue(UPDATED_PARAM_ITEM_VALUE).paramValue(UPDATED_PARAM_VALUE);

        restCarParamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarParam.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCarParam))
            )
            .andExpect(status().isOk());

        // Validate the CarParam in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCarParamUpdatableFieldsEquals(partialUpdatedCarParam, getPersistedCarParam(partialUpdatedCarParam));
    }

    @Test
    @Transactional
    void patchNonExistingCarParam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carParamSearchRepository.findAll());
        carParam.setId(longCount.incrementAndGet());

        // Create the CarParam
        CarParamDTO carParamDTO = carParamMapper.toDto(carParam);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarParamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, carParamDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(carParamDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarParam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carParamSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCarParam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carParamSearchRepository.findAll());
        carParam.setId(longCount.incrementAndGet());

        // Create the CarParam
        CarParamDTO carParamDTO = carParamMapper.toDto(carParam);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarParamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(carParamDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarParam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carParamSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCarParam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carParamSearchRepository.findAll());
        carParam.setId(longCount.incrementAndGet());

        // Create the CarParam
        CarParamDTO carParamDTO = carParamMapper.toDto(carParam);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarParamMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(carParamDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CarParam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carParamSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCarParam() throws Exception {
        // Initialize the database
        insertedCarParam = carParamRepository.saveAndFlush(carParam);
        carParamRepository.save(carParam);
        carParamSearchRepository.save(carParam);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carParamSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the carParam
        restCarParamMockMvc
            .perform(delete(ENTITY_API_URL_ID, carParam.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carParamSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCarParam() throws Exception {
        // Initialize the database
        insertedCarParam = carParamRepository.saveAndFlush(carParam);
        carParamSearchRepository.save(carParam);

        // Search the carParam
        restCarParamMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + carParam.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carParam.getId().intValue())))
            .andExpect(jsonPath("$.[*].paramItemValue").value(hasItem(DEFAULT_PARAM_ITEM_VALUE)))
            .andExpect(jsonPath("$.[*].paramValue").value(hasItem(DEFAULT_PARAM_VALUE)));
    }

    protected long getRepositoryCount() {
        return carParamRepository.count();
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

    protected CarParam getPersistedCarParam(CarParam carParam) {
        return carParamRepository.findById(carParam.getId()).orElseThrow();
    }

    protected void assertPersistedCarParamToMatchAllProperties(CarParam expectedCarParam) {
        assertCarParamAllPropertiesEquals(expectedCarParam, getPersistedCarParam(expectedCarParam));
    }

    protected void assertPersistedCarParamToMatchUpdatableProperties(CarParam expectedCarParam) {
        assertCarParamAllUpdatablePropertiesEquals(expectedCarParam, getPersistedCarParam(expectedCarParam));
    }
}
