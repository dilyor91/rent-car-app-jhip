package uz.carapp.rentcarapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uz.carapp.rentcarapp.domain.ParamValueAsserts.*;
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
import uz.carapp.rentcarapp.domain.ParamValue;
import uz.carapp.rentcarapp.repository.ParamValueRepository;
import uz.carapp.rentcarapp.repository.search.ParamValueSearchRepository;
import uz.carapp.rentcarapp.service.dto.ParamValueDTO;
import uz.carapp.rentcarapp.service.mapper.ParamValueMapper;

/**
 * Integration tests for the {@link ParamValueResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ParamValueResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final String ENTITY_API_URL = "/api/param-values";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/param-values/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ParamValueRepository paramValueRepository;

    @Autowired
    private ParamValueMapper paramValueMapper;

    @Autowired
    private ParamValueSearchRepository paramValueSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restParamValueMockMvc;

    private ParamValue paramValue;

    private ParamValue insertedParamValue;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParamValue createEntity() {
        return new ParamValue().name(DEFAULT_NAME).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParamValue createUpdatedEntity() {
        return new ParamValue().name(UPDATED_NAME).status(UPDATED_STATUS);
    }

    @BeforeEach
    public void initTest() {
        paramValue = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedParamValue != null) {
            paramValueRepository.delete(insertedParamValue);
            paramValueSearchRepository.delete(insertedParamValue);
            insertedParamValue = null;
        }
    }

    @Test
    @Transactional
    void createParamValue() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paramValueSearchRepository.findAll());
        // Create the ParamValue
        ParamValueDTO paramValueDTO = paramValueMapper.toDto(paramValue);
        var returnedParamValueDTO = om.readValue(
            restParamValueMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paramValueDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ParamValueDTO.class
        );

        // Validate the ParamValue in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedParamValue = paramValueMapper.toEntity(returnedParamValueDTO);
        assertParamValueUpdatableFieldsEquals(returnedParamValue, getPersistedParamValue(returnedParamValue));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(paramValueSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedParamValue = returnedParamValue;
    }

    @Test
    @Transactional
    void createParamValueWithExistingId() throws Exception {
        // Create the ParamValue with an existing ID
        paramValue.setId(1L);
        ParamValueDTO paramValueDTO = paramValueMapper.toDto(paramValue);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paramValueSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restParamValueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paramValueDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ParamValue in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paramValueSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllParamValues() throws Exception {
        // Initialize the database
        insertedParamValue = paramValueRepository.saveAndFlush(paramValue);

        // Get all the paramValueList
        restParamValueMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paramValue.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @Test
    @Transactional
    void getParamValue() throws Exception {
        // Initialize the database
        insertedParamValue = paramValueRepository.saveAndFlush(paramValue);

        // Get the paramValue
        restParamValueMockMvc
            .perform(get(ENTITY_API_URL_ID, paramValue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(paramValue.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    void getNonExistingParamValue() throws Exception {
        // Get the paramValue
        restParamValueMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingParamValue() throws Exception {
        // Initialize the database
        insertedParamValue = paramValueRepository.saveAndFlush(paramValue);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        paramValueSearchRepository.save(paramValue);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paramValueSearchRepository.findAll());

        // Update the paramValue
        ParamValue updatedParamValue = paramValueRepository.findById(paramValue.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedParamValue are not directly saved in db
        em.detach(updatedParamValue);
        updatedParamValue.name(UPDATED_NAME).status(UPDATED_STATUS);
        ParamValueDTO paramValueDTO = paramValueMapper.toDto(updatedParamValue);

        restParamValueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paramValueDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paramValueDTO))
            )
            .andExpect(status().isOk());

        // Validate the ParamValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedParamValueToMatchAllProperties(updatedParamValue);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(paramValueSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ParamValue> paramValueSearchList = Streamable.of(paramValueSearchRepository.findAll()).toList();
                ParamValue testParamValueSearch = paramValueSearchList.get(searchDatabaseSizeAfter - 1);

                assertParamValueAllPropertiesEquals(testParamValueSearch, updatedParamValue);
            });
    }

    @Test
    @Transactional
    void putNonExistingParamValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paramValueSearchRepository.findAll());
        paramValue.setId(longCount.incrementAndGet());

        // Create the ParamValue
        ParamValueDTO paramValueDTO = paramValueMapper.toDto(paramValue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParamValueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paramValueDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paramValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParamValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paramValueSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchParamValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paramValueSearchRepository.findAll());
        paramValue.setId(longCount.incrementAndGet());

        // Create the ParamValue
        ParamValueDTO paramValueDTO = paramValueMapper.toDto(paramValue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParamValueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paramValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParamValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paramValueSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamParamValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paramValueSearchRepository.findAll());
        paramValue.setId(longCount.incrementAndGet());

        // Create the ParamValue
        ParamValueDTO paramValueDTO = paramValueMapper.toDto(paramValue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParamValueMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paramValueDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParamValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paramValueSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateParamValueWithPatch() throws Exception {
        // Initialize the database
        insertedParamValue = paramValueRepository.saveAndFlush(paramValue);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paramValue using partial update
        ParamValue partialUpdatedParamValue = new ParamValue();
        partialUpdatedParamValue.setId(paramValue.getId());

        partialUpdatedParamValue.name(UPDATED_NAME).status(UPDATED_STATUS);

        restParamValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParamValue.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParamValue))
            )
            .andExpect(status().isOk());

        // Validate the ParamValue in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParamValueUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedParamValue, paramValue),
            getPersistedParamValue(paramValue)
        );
    }

    @Test
    @Transactional
    void fullUpdateParamValueWithPatch() throws Exception {
        // Initialize the database
        insertedParamValue = paramValueRepository.saveAndFlush(paramValue);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the paramValue using partial update
        ParamValue partialUpdatedParamValue = new ParamValue();
        partialUpdatedParamValue.setId(paramValue.getId());

        partialUpdatedParamValue.name(UPDATED_NAME).status(UPDATED_STATUS);

        restParamValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParamValue.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParamValue))
            )
            .andExpect(status().isOk());

        // Validate the ParamValue in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParamValueUpdatableFieldsEquals(partialUpdatedParamValue, getPersistedParamValue(partialUpdatedParamValue));
    }

    @Test
    @Transactional
    void patchNonExistingParamValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paramValueSearchRepository.findAll());
        paramValue.setId(longCount.incrementAndGet());

        // Create the ParamValue
        ParamValueDTO paramValueDTO = paramValueMapper.toDto(paramValue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParamValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, paramValueDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paramValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParamValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paramValueSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchParamValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paramValueSearchRepository.findAll());
        paramValue.setId(longCount.incrementAndGet());

        // Create the ParamValue
        ParamValueDTO paramValueDTO = paramValueMapper.toDto(paramValue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParamValueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paramValueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParamValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paramValueSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamParamValue() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paramValueSearchRepository.findAll());
        paramValue.setId(longCount.incrementAndGet());

        // Create the ParamValue
        ParamValueDTO paramValueDTO = paramValueMapper.toDto(paramValue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParamValueMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(paramValueDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParamValue in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paramValueSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteParamValue() throws Exception {
        // Initialize the database
        insertedParamValue = paramValueRepository.saveAndFlush(paramValue);
        paramValueRepository.save(paramValue);
        paramValueSearchRepository.save(paramValue);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paramValueSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the paramValue
        restParamValueMockMvc
            .perform(delete(ENTITY_API_URL_ID, paramValue.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paramValueSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchParamValue() throws Exception {
        // Initialize the database
        insertedParamValue = paramValueRepository.saveAndFlush(paramValue);
        paramValueSearchRepository.save(paramValue);

        // Search the paramValue
        restParamValueMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + paramValue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paramValue.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    protected long getRepositoryCount() {
        return paramValueRepository.count();
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

    protected ParamValue getPersistedParamValue(ParamValue paramValue) {
        return paramValueRepository.findById(paramValue.getId()).orElseThrow();
    }

    protected void assertPersistedParamValueToMatchAllProperties(ParamValue expectedParamValue) {
        assertParamValueAllPropertiesEquals(expectedParamValue, getPersistedParamValue(expectedParamValue));
    }

    protected void assertPersistedParamValueToMatchUpdatableProperties(ParamValue expectedParamValue) {
        assertParamValueAllUpdatablePropertiesEquals(expectedParamValue, getPersistedParamValue(expectedParamValue));
    }
}
