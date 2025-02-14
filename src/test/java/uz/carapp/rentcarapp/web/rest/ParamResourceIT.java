package uz.carapp.rentcarapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uz.carapp.rentcarapp.domain.ParamAsserts.*;
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
import uz.carapp.rentcarapp.domain.Param;
import uz.carapp.rentcarapp.domain.enumeration.FieldTypeEnum;
import uz.carapp.rentcarapp.repository.ParamRepository;
import uz.carapp.rentcarapp.repository.search.ParamSearchRepository;
import uz.carapp.rentcarapp.service.dto.ParamDTO;
import uz.carapp.rentcarapp.service.mapper.ParamMapper;

/**
 * Integration tests for the {@link ParamResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ParamResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final FieldTypeEnum DEFAULT_FIELD_TYPE = FieldTypeEnum.INPUT_FIELD;
    private static final FieldTypeEnum UPDATED_FIELD_TYPE = FieldTypeEnum.TEXT_FIELD;

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final Boolean DEFAULT_IS_DEFAULT = false;
    private static final Boolean UPDATED_IS_DEFAULT = true;

    private static final String ENTITY_API_URL = "/api/params";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/params/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ParamRepository paramRepository;

    @Autowired
    private ParamMapper paramMapper;

    @Autowired
    private ParamSearchRepository paramSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restParamMockMvc;

    private Param param;

    private Param insertedParam;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Param createEntity() {
        return new Param()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .fieldType(DEFAULT_FIELD_TYPE)
            .status(DEFAULT_STATUS)
            .isDefault(DEFAULT_IS_DEFAULT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Param createUpdatedEntity() {
        return new Param()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .fieldType(UPDATED_FIELD_TYPE)
            .status(UPDATED_STATUS)
            .isDefault(UPDATED_IS_DEFAULT);
    }

    @BeforeEach
    public void initTest() {
        param = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedParam != null) {
            paramRepository.delete(insertedParam);
            paramSearchRepository.delete(insertedParam);
            insertedParam = null;
        }
    }

    @Test
    @Transactional
    void createParam() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paramSearchRepository.findAll());
        // Create the Param
        ParamDTO paramDTO = paramMapper.toDto(param);
        var returnedParamDTO = om.readValue(
            restParamMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paramDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ParamDTO.class
        );

        // Validate the Param in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedParam = paramMapper.toEntity(returnedParamDTO);
        assertParamUpdatableFieldsEquals(returnedParam, getPersistedParam(returnedParam));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(paramSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedParam = returnedParam;
    }

    @Test
    @Transactional
    void createParamWithExistingId() throws Exception {
        // Create the Param with an existing ID
        param.setId(1L);
        ParamDTO paramDTO = paramMapper.toDto(param);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paramSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restParamMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paramDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Param in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paramSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllParams() throws Exception {
        // Initialize the database
        insertedParam = paramRepository.saveAndFlush(param);

        // Get all the paramList
        restParamMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(param.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].fieldType").value(hasItem(DEFAULT_FIELD_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].isDefault").value(hasItem(DEFAULT_IS_DEFAULT)));
    }

    @Test
    @Transactional
    void getParam() throws Exception {
        // Initialize the database
        insertedParam = paramRepository.saveAndFlush(param);

        // Get the param
        restParamMockMvc
            .perform(get(ENTITY_API_URL_ID, param.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(param.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.fieldType").value(DEFAULT_FIELD_TYPE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS))
            .andExpect(jsonPath("$.isDefault").value(DEFAULT_IS_DEFAULT));
    }

    @Test
    @Transactional
    void getNonExistingParam() throws Exception {
        // Get the param
        restParamMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingParam() throws Exception {
        // Initialize the database
        insertedParam = paramRepository.saveAndFlush(param);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        paramSearchRepository.save(param);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paramSearchRepository.findAll());

        // Update the param
        Param updatedParam = paramRepository.findById(param.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedParam are not directly saved in db
        em.detach(updatedParam);
        updatedParam
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .fieldType(UPDATED_FIELD_TYPE)
            .status(UPDATED_STATUS)
            .isDefault(UPDATED_IS_DEFAULT);
        ParamDTO paramDTO = paramMapper.toDto(updatedParam);

        restParamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paramDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paramDTO))
            )
            .andExpect(status().isOk());

        // Validate the Param in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedParamToMatchAllProperties(updatedParam);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(paramSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Param> paramSearchList = Streamable.of(paramSearchRepository.findAll()).toList();
                Param testParamSearch = paramSearchList.get(searchDatabaseSizeAfter - 1);

                assertParamAllPropertiesEquals(testParamSearch, updatedParam);
            });
    }

    @Test
    @Transactional
    void putNonExistingParam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paramSearchRepository.findAll());
        param.setId(longCount.incrementAndGet());

        // Create the Param
        ParamDTO paramDTO = paramMapper.toDto(param);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paramDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paramDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Param in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paramSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchParam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paramSearchRepository.findAll());
        param.setId(longCount.incrementAndGet());

        // Create the Param
        ParamDTO paramDTO = paramMapper.toDto(param);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParamMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(paramDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Param in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paramSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamParam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paramSearchRepository.findAll());
        param.setId(longCount.incrementAndGet());

        // Create the Param
        ParamDTO paramDTO = paramMapper.toDto(param);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParamMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(paramDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Param in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paramSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateParamWithPatch() throws Exception {
        // Initialize the database
        insertedParam = paramRepository.saveAndFlush(param);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the param using partial update
        Param partialUpdatedParam = new Param();
        partialUpdatedParam.setId(param.getId());

        partialUpdatedParam.name(UPDATED_NAME).status(UPDATED_STATUS).isDefault(UPDATED_IS_DEFAULT);

        restParamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParam.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParam))
            )
            .andExpect(status().isOk());

        // Validate the Param in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParamUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedParam, param), getPersistedParam(param));
    }

    @Test
    @Transactional
    void fullUpdateParamWithPatch() throws Exception {
        // Initialize the database
        insertedParam = paramRepository.saveAndFlush(param);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the param using partial update
        Param partialUpdatedParam = new Param();
        partialUpdatedParam.setId(param.getId());

        partialUpdatedParam
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .fieldType(UPDATED_FIELD_TYPE)
            .status(UPDATED_STATUS)
            .isDefault(UPDATED_IS_DEFAULT);

        restParamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParam.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParam))
            )
            .andExpect(status().isOk());

        // Validate the Param in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParamUpdatableFieldsEquals(partialUpdatedParam, getPersistedParam(partialUpdatedParam));
    }

    @Test
    @Transactional
    void patchNonExistingParam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paramSearchRepository.findAll());
        param.setId(longCount.incrementAndGet());

        // Create the Param
        ParamDTO paramDTO = paramMapper.toDto(param);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, paramDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paramDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Param in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paramSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchParam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paramSearchRepository.findAll());
        param.setId(longCount.incrementAndGet());

        // Create the Param
        ParamDTO paramDTO = paramMapper.toDto(param);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParamMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(paramDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Param in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paramSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamParam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paramSearchRepository.findAll());
        param.setId(longCount.incrementAndGet());

        // Create the Param
        ParamDTO paramDTO = paramMapper.toDto(param);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParamMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(paramDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Param in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paramSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteParam() throws Exception {
        // Initialize the database
        insertedParam = paramRepository.saveAndFlush(param);
        paramRepository.save(param);
        paramSearchRepository.save(param);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(paramSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the param
        restParamMockMvc
            .perform(delete(ENTITY_API_URL_ID, param.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(paramSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchParam() throws Exception {
        // Initialize the database
        insertedParam = paramRepository.saveAndFlush(param);
        paramSearchRepository.save(param);

        // Search the param
        restParamMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + param.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(param.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].fieldType").value(hasItem(DEFAULT_FIELD_TYPE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)))
            .andExpect(jsonPath("$.[*].isDefault").value(hasItem(DEFAULT_IS_DEFAULT)));
    }

    protected long getRepositoryCount() {
        return paramRepository.count();
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

    protected Param getPersistedParam(Param param) {
        return paramRepository.findById(param.getId()).orElseThrow();
    }

    protected void assertPersistedParamToMatchAllProperties(Param expectedParam) {
        assertParamAllPropertiesEquals(expectedParam, getPersistedParam(expectedParam));
    }

    protected void assertPersistedParamToMatchUpdatableProperties(Param expectedParam) {
        assertParamAllUpdatablePropertiesEquals(expectedParam, getPersistedParam(expectedParam));
    }
}
