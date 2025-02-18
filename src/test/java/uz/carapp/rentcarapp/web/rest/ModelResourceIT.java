package uz.carapp.rentcarapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uz.carapp.rentcarapp.domain.ModelAsserts.*;
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
import uz.carapp.rentcarapp.domain.Model;
import uz.carapp.rentcarapp.repository.ModelRepository;
import uz.carapp.rentcarapp.repository.search.ModelSearchRepository;
import uz.carapp.rentcarapp.service.dto.ModelDTO;
import uz.carapp.rentcarapp.service.mapper.ModelMapper;

/**
 * Integration tests for the {@link ModelResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ModelResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final String ENTITY_API_URL = "/api/models";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/models/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ModelSearchRepository modelSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restModelMockMvc;

    private Model model;

    private Model insertedModel;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Model createEntity() {
        return new Model().name(DEFAULT_NAME).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Model createUpdatedEntity() {
        return new Model().name(UPDATED_NAME).status(UPDATED_STATUS);
    }

    @BeforeEach
    public void initTest() {
        model = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedModel != null) {
            modelRepository.delete(insertedModel);
            modelSearchRepository.delete(insertedModel);
            insertedModel = null;
        }
    }

    @Test
    @Transactional
    void createModel() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(modelSearchRepository.findAll());
        // Create the Model
        ModelDTO modelDTO = modelMapper.toDto(model);
        var returnedModelDTO = om.readValue(
            restModelMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(modelDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ModelDTO.class
        );

        // Validate the Model in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedModel = modelMapper.toEntity(returnedModelDTO);
        assertModelUpdatableFieldsEquals(returnedModel, getPersistedModel(returnedModel));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(modelSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedModel = returnedModel;
    }

    @Test
    @Transactional
    void createModelWithExistingId() throws Exception {
        // Create the Model with an existing ID
        model.setId(1L);
        ModelDTO modelDTO = modelMapper.toDto(model);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(modelSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restModelMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(modelDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Model in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(modelSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllModels() throws Exception {
        // Initialize the database
        insertedModel = modelRepository.saveAndFlush(model);

        // Get all the modelList
        restModelMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(model.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @Test
    @Transactional
    void getModel() throws Exception {
        // Initialize the database
        insertedModel = modelRepository.saveAndFlush(model);

        // Get the model
        restModelMockMvc
            .perform(get(ENTITY_API_URL_ID, model.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(model.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    void getNonExistingModel() throws Exception {
        // Get the model
        restModelMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingModel() throws Exception {
        // Initialize the database
        insertedModel = modelRepository.saveAndFlush(model);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        modelSearchRepository.save(model);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(modelSearchRepository.findAll());

        // Update the model
        Model updatedModel = modelRepository.findById(model.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedModel are not directly saved in db
        em.detach(updatedModel);
        updatedModel.name(UPDATED_NAME).status(UPDATED_STATUS);
        ModelDTO modelDTO = modelMapper.toDto(updatedModel);

        restModelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, modelDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(modelDTO))
            )
            .andExpect(status().isOk());

        // Validate the Model in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedModelToMatchAllProperties(updatedModel);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(modelSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Model> modelSearchList = Streamable.of(modelSearchRepository.findAll()).toList();
                Model testModelSearch = modelSearchList.get(searchDatabaseSizeAfter - 1);

                assertModelAllPropertiesEquals(testModelSearch, updatedModel);
            });
    }

    @Test
    @Transactional
    void putNonExistingModel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(modelSearchRepository.findAll());
        model.setId(longCount.incrementAndGet());

        // Create the Model
        ModelDTO modelDTO = modelMapper.toDto(model);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restModelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, modelDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(modelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Model in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(modelSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchModel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(modelSearchRepository.findAll());
        model.setId(longCount.incrementAndGet());

        // Create the Model
        ModelDTO modelDTO = modelMapper.toDto(model);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModelMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(modelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Model in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(modelSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamModel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(modelSearchRepository.findAll());
        model.setId(longCount.incrementAndGet());

        // Create the Model
        ModelDTO modelDTO = modelMapper.toDto(model);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModelMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(modelDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Model in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(modelSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateModelWithPatch() throws Exception {
        // Initialize the database
        insertedModel = modelRepository.saveAndFlush(model);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the model using partial update
        Model partialUpdatedModel = new Model();
        partialUpdatedModel.setId(model.getId());

        partialUpdatedModel.status(UPDATED_STATUS);

        restModelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedModel.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedModel))
            )
            .andExpect(status().isOk());

        // Validate the Model in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertModelUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedModel, model), getPersistedModel(model));
    }

    @Test
    @Transactional
    void fullUpdateModelWithPatch() throws Exception {
        // Initialize the database
        insertedModel = modelRepository.saveAndFlush(model);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the model using partial update
        Model partialUpdatedModel = new Model();
        partialUpdatedModel.setId(model.getId());

        partialUpdatedModel.name(UPDATED_NAME).status(UPDATED_STATUS);

        restModelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedModel.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedModel))
            )
            .andExpect(status().isOk());

        // Validate the Model in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertModelUpdatableFieldsEquals(partialUpdatedModel, getPersistedModel(partialUpdatedModel));
    }

    @Test
    @Transactional
    void patchNonExistingModel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(modelSearchRepository.findAll());
        model.setId(longCount.incrementAndGet());

        // Create the Model
        ModelDTO modelDTO = modelMapper.toDto(model);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restModelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, modelDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(modelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Model in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(modelSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchModel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(modelSearchRepository.findAll());
        model.setId(longCount.incrementAndGet());

        // Create the Model
        ModelDTO modelDTO = modelMapper.toDto(model);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModelMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(modelDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Model in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(modelSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamModel() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(modelSearchRepository.findAll());
        model.setId(longCount.incrementAndGet());

        // Create the Model
        ModelDTO modelDTO = modelMapper.toDto(model);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModelMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(modelDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Model in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(modelSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteModel() throws Exception {
        // Initialize the database
        insertedModel = modelRepository.saveAndFlush(model);
        modelRepository.save(model);
        modelSearchRepository.save(model);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(modelSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the model
        restModelMockMvc
            .perform(delete(ENTITY_API_URL_ID, model.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(modelSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchModel() throws Exception {
        // Initialize the database
        insertedModel = modelRepository.saveAndFlush(model);
        modelSearchRepository.save(model);

        // Search the model
        restModelMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + model.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(model.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    protected long getRepositoryCount() {
        return modelRepository.count();
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

    protected Model getPersistedModel(Model model) {
        return modelRepository.findById(model.getId()).orElseThrow();
    }

    protected void assertPersistedModelToMatchAllProperties(Model expectedModel) {
        assertModelAllPropertiesEquals(expectedModel, getPersistedModel(expectedModel));
    }

    protected void assertPersistedModelToMatchUpdatableProperties(Model expectedModel) {
        assertModelAllUpdatablePropertiesEquals(expectedModel, getPersistedModel(expectedModel));
    }
}
