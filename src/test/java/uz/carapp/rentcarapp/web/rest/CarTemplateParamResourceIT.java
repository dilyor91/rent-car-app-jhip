package uz.carapp.rentcarapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uz.carapp.rentcarapp.domain.CarTemplateParamAsserts.*;
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
import uz.carapp.rentcarapp.domain.CarTemplateParam;
import uz.carapp.rentcarapp.repository.CarTemplateParamRepository;
import uz.carapp.rentcarapp.repository.search.CarTemplateParamSearchRepository;
import uz.carapp.rentcarapp.service.dto.CarTemplateParamDTO;
import uz.carapp.rentcarapp.service.mapper.CarTemplateParamMapper;

/**
 * Integration tests for the {@link CarTemplateParamResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CarTemplateParamResourceIT {

    private static final String DEFAULT_PARAM_VAL = "AAAAAAAAAA";
    private static final String UPDATED_PARAM_VAL = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/car-template-params";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/car-template-params/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CarTemplateParamRepository carTemplateParamRepository;

    @Autowired
    private CarTemplateParamMapper carTemplateParamMapper;

    @Autowired
    private CarTemplateParamSearchRepository carTemplateParamSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCarTemplateParamMockMvc;

    private CarTemplateParam carTemplateParam;

    private CarTemplateParam insertedCarTemplateParam;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CarTemplateParam createEntity() {
        return new CarTemplateParam().paramVal(DEFAULT_PARAM_VAL);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CarTemplateParam createUpdatedEntity() {
        return new CarTemplateParam().paramVal(UPDATED_PARAM_VAL);
    }

    @BeforeEach
    public void initTest() {
        carTemplateParam = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedCarTemplateParam != null) {
            carTemplateParamRepository.delete(insertedCarTemplateParam);
            carTemplateParamSearchRepository.delete(insertedCarTemplateParam);
            insertedCarTemplateParam = null;
        }
    }

    @Test
    @Transactional
    void createCarTemplateParam() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carTemplateParamSearchRepository.findAll());
        // Create the CarTemplateParam
        CarTemplateParamDTO carTemplateParamDTO = carTemplateParamMapper.toDto(carTemplateParam);
        var returnedCarTemplateParamDTO = om.readValue(
            restCarTemplateParamMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carTemplateParamDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CarTemplateParamDTO.class
        );

        // Validate the CarTemplateParam in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCarTemplateParam = carTemplateParamMapper.toEntity(returnedCarTemplateParamDTO);
        assertCarTemplateParamUpdatableFieldsEquals(returnedCarTemplateParam, getPersistedCarTemplateParam(returnedCarTemplateParam));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(carTemplateParamSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedCarTemplateParam = returnedCarTemplateParam;
    }

    @Test
    @Transactional
    void createCarTemplateParamWithExistingId() throws Exception {
        // Create the CarTemplateParam with an existing ID
        carTemplateParam.setId(1L);
        CarTemplateParamDTO carTemplateParamDTO = carTemplateParamMapper.toDto(carTemplateParam);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carTemplateParamSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCarTemplateParamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carTemplateParamDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CarTemplateParam in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carTemplateParamSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCarTemplateParams() throws Exception {
        // Initialize the database
        insertedCarTemplateParam = carTemplateParamRepository.saveAndFlush(carTemplateParam);

        // Get all the carTemplateParamList
        restCarTemplateParamMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carTemplateParam.getId().intValue())))
            .andExpect(jsonPath("$.[*].paramVal").value(hasItem(DEFAULT_PARAM_VAL)));
    }

    @Test
    @Transactional
    void getCarTemplateParam() throws Exception {
        // Initialize the database
        insertedCarTemplateParam = carTemplateParamRepository.saveAndFlush(carTemplateParam);

        // Get the carTemplateParam
        restCarTemplateParamMockMvc
            .perform(get(ENTITY_API_URL_ID, carTemplateParam.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(carTemplateParam.getId().intValue()))
            .andExpect(jsonPath("$.paramVal").value(DEFAULT_PARAM_VAL));
    }

    @Test
    @Transactional
    void getNonExistingCarTemplateParam() throws Exception {
        // Get the carTemplateParam
        restCarTemplateParamMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCarTemplateParam() throws Exception {
        // Initialize the database
        insertedCarTemplateParam = carTemplateParamRepository.saveAndFlush(carTemplateParam);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        carTemplateParamSearchRepository.save(carTemplateParam);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carTemplateParamSearchRepository.findAll());

        // Update the carTemplateParam
        CarTemplateParam updatedCarTemplateParam = carTemplateParamRepository.findById(carTemplateParam.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCarTemplateParam are not directly saved in db
        em.detach(updatedCarTemplateParam);
        updatedCarTemplateParam.paramVal(UPDATED_PARAM_VAL);
        CarTemplateParamDTO carTemplateParamDTO = carTemplateParamMapper.toDto(updatedCarTemplateParam);

        restCarTemplateParamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carTemplateParamDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carTemplateParamDTO))
            )
            .andExpect(status().isOk());

        // Validate the CarTemplateParam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCarTemplateParamToMatchAllProperties(updatedCarTemplateParam);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(carTemplateParamSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<CarTemplateParam> carTemplateParamSearchList = Streamable.of(carTemplateParamSearchRepository.findAll()).toList();
                CarTemplateParam testCarTemplateParamSearch = carTemplateParamSearchList.get(searchDatabaseSizeAfter - 1);

                assertCarTemplateParamAllPropertiesEquals(testCarTemplateParamSearch, updatedCarTemplateParam);
            });
    }

    @Test
    @Transactional
    void putNonExistingCarTemplateParam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carTemplateParamSearchRepository.findAll());
        carTemplateParam.setId(longCount.incrementAndGet());

        // Create the CarTemplateParam
        CarTemplateParamDTO carTemplateParamDTO = carTemplateParamMapper.toDto(carTemplateParam);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarTemplateParamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carTemplateParamDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carTemplateParamDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarTemplateParam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carTemplateParamSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCarTemplateParam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carTemplateParamSearchRepository.findAll());
        carTemplateParam.setId(longCount.incrementAndGet());

        // Create the CarTemplateParam
        CarTemplateParamDTO carTemplateParamDTO = carTemplateParamMapper.toDto(carTemplateParam);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarTemplateParamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carTemplateParamDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarTemplateParam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carTemplateParamSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCarTemplateParam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carTemplateParamSearchRepository.findAll());
        carTemplateParam.setId(longCount.incrementAndGet());

        // Create the CarTemplateParam
        CarTemplateParamDTO carTemplateParamDTO = carTemplateParamMapper.toDto(carTemplateParam);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarTemplateParamMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carTemplateParamDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CarTemplateParam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carTemplateParamSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCarTemplateParamWithPatch() throws Exception {
        // Initialize the database
        insertedCarTemplateParam = carTemplateParamRepository.saveAndFlush(carTemplateParam);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carTemplateParam using partial update
        CarTemplateParam partialUpdatedCarTemplateParam = new CarTemplateParam();
        partialUpdatedCarTemplateParam.setId(carTemplateParam.getId());

        restCarTemplateParamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarTemplateParam.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCarTemplateParam))
            )
            .andExpect(status().isOk());

        // Validate the CarTemplateParam in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCarTemplateParamUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCarTemplateParam, carTemplateParam),
            getPersistedCarTemplateParam(carTemplateParam)
        );
    }

    @Test
    @Transactional
    void fullUpdateCarTemplateParamWithPatch() throws Exception {
        // Initialize the database
        insertedCarTemplateParam = carTemplateParamRepository.saveAndFlush(carTemplateParam);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carTemplateParam using partial update
        CarTemplateParam partialUpdatedCarTemplateParam = new CarTemplateParam();
        partialUpdatedCarTemplateParam.setId(carTemplateParam.getId());

        partialUpdatedCarTemplateParam.paramVal(UPDATED_PARAM_VAL);

        restCarTemplateParamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarTemplateParam.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCarTemplateParam))
            )
            .andExpect(status().isOk());

        // Validate the CarTemplateParam in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCarTemplateParamUpdatableFieldsEquals(
            partialUpdatedCarTemplateParam,
            getPersistedCarTemplateParam(partialUpdatedCarTemplateParam)
        );
    }

    @Test
    @Transactional
    void patchNonExistingCarTemplateParam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carTemplateParamSearchRepository.findAll());
        carTemplateParam.setId(longCount.incrementAndGet());

        // Create the CarTemplateParam
        CarTemplateParamDTO carTemplateParamDTO = carTemplateParamMapper.toDto(carTemplateParam);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarTemplateParamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, carTemplateParamDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(carTemplateParamDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarTemplateParam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carTemplateParamSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCarTemplateParam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carTemplateParamSearchRepository.findAll());
        carTemplateParam.setId(longCount.incrementAndGet());

        // Create the CarTemplateParam
        CarTemplateParamDTO carTemplateParamDTO = carTemplateParamMapper.toDto(carTemplateParam);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarTemplateParamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(carTemplateParamDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarTemplateParam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carTemplateParamSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCarTemplateParam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carTemplateParamSearchRepository.findAll());
        carTemplateParam.setId(longCount.incrementAndGet());

        // Create the CarTemplateParam
        CarTemplateParamDTO carTemplateParamDTO = carTemplateParamMapper.toDto(carTemplateParam);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarTemplateParamMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(carTemplateParamDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CarTemplateParam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carTemplateParamSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCarTemplateParam() throws Exception {
        // Initialize the database
        insertedCarTemplateParam = carTemplateParamRepository.saveAndFlush(carTemplateParam);
        carTemplateParamRepository.save(carTemplateParam);
        carTemplateParamSearchRepository.save(carTemplateParam);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carTemplateParamSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the carTemplateParam
        restCarTemplateParamMockMvc
            .perform(delete(ENTITY_API_URL_ID, carTemplateParam.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carTemplateParamSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCarTemplateParam() throws Exception {
        // Initialize the database
        insertedCarTemplateParam = carTemplateParamRepository.saveAndFlush(carTemplateParam);
        carTemplateParamSearchRepository.save(carTemplateParam);

        // Search the carTemplateParam
        restCarTemplateParamMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + carTemplateParam.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carTemplateParam.getId().intValue())))
            .andExpect(jsonPath("$.[*].paramVal").value(hasItem(DEFAULT_PARAM_VAL)));
    }

    protected long getRepositoryCount() {
        return carTemplateParamRepository.count();
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

    protected CarTemplateParam getPersistedCarTemplateParam(CarTemplateParam carTemplateParam) {
        return carTemplateParamRepository.findById(carTemplateParam.getId()).orElseThrow();
    }

    protected void assertPersistedCarTemplateParamToMatchAllProperties(CarTemplateParam expectedCarTemplateParam) {
        assertCarTemplateParamAllPropertiesEquals(expectedCarTemplateParam, getPersistedCarTemplateParam(expectedCarTemplateParam));
    }

    protected void assertPersistedCarTemplateParamToMatchUpdatableProperties(CarTemplateParam expectedCarTemplateParam) {
        assertCarTemplateParamAllUpdatablePropertiesEquals(
            expectedCarTemplateParam,
            getPersistedCarTemplateParam(expectedCarTemplateParam)
        );
    }
}
