package uz.carapp.rentcarapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uz.carapp.rentcarapp.domain.ModelAttachmentAsserts.*;
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
import uz.carapp.rentcarapp.domain.ModelAttachment;
import uz.carapp.rentcarapp.repository.ModelAttachmentRepository;
import uz.carapp.rentcarapp.repository.search.ModelAttachmentSearchRepository;
import uz.carapp.rentcarapp.service.dto.ModelAttachmentDTO;
import uz.carapp.rentcarapp.service.mapper.ModelAttachmentMapper;

/**
 * Integration tests for the {@link ModelAttachmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ModelAttachmentResourceIT {

    private static final Boolean DEFAULT_IS_MAIN = false;
    private static final Boolean UPDATED_IS_MAIN = true;

    private static final String ENTITY_API_URL = "/api/model-attachments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/model-attachments/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ModelAttachmentRepository modelAttachmentRepository;

    @Autowired
    private ModelAttachmentMapper modelAttachmentMapper;

    @Autowired
    private ModelAttachmentSearchRepository modelAttachmentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restModelAttachmentMockMvc;

    private ModelAttachment modelAttachment;

    private ModelAttachment insertedModelAttachment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ModelAttachment createEntity() {
        return new ModelAttachment().isMain(DEFAULT_IS_MAIN);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ModelAttachment createUpdatedEntity() {
        return new ModelAttachment().isMain(UPDATED_IS_MAIN);
    }

    @BeforeEach
    public void initTest() {
        modelAttachment = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedModelAttachment != null) {
            modelAttachmentRepository.delete(insertedModelAttachment);
            modelAttachmentSearchRepository.delete(insertedModelAttachment);
            insertedModelAttachment = null;
        }
    }

    @Test
    @Transactional
    void createModelAttachment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(modelAttachmentSearchRepository.findAll());
        // Create the ModelAttachment
        ModelAttachmentDTO modelAttachmentDTO = modelAttachmentMapper.toDto(modelAttachment);
        var returnedModelAttachmentDTO = om.readValue(
            restModelAttachmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(modelAttachmentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ModelAttachmentDTO.class
        );

        // Validate the ModelAttachment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedModelAttachment = modelAttachmentMapper.toEntity(returnedModelAttachmentDTO);
        assertModelAttachmentUpdatableFieldsEquals(returnedModelAttachment, getPersistedModelAttachment(returnedModelAttachment));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(modelAttachmentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedModelAttachment = returnedModelAttachment;
    }

    @Test
    @Transactional
    void createModelAttachmentWithExistingId() throws Exception {
        // Create the ModelAttachment with an existing ID
        modelAttachment.setId(1L);
        ModelAttachmentDTO modelAttachmentDTO = modelAttachmentMapper.toDto(modelAttachment);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(modelAttachmentSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restModelAttachmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(modelAttachmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ModelAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(modelAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllModelAttachments() throws Exception {
        // Initialize the database
        insertedModelAttachment = modelAttachmentRepository.saveAndFlush(modelAttachment);

        // Get all the modelAttachmentList
        restModelAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(modelAttachment.getId().intValue())))
            .andExpect(jsonPath("$.[*].isMain").value(hasItem(DEFAULT_IS_MAIN)));
    }

    @Test
    @Transactional
    void getModelAttachment() throws Exception {
        // Initialize the database
        insertedModelAttachment = modelAttachmentRepository.saveAndFlush(modelAttachment);

        // Get the modelAttachment
        restModelAttachmentMockMvc
            .perform(get(ENTITY_API_URL_ID, modelAttachment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(modelAttachment.getId().intValue()))
            .andExpect(jsonPath("$.isMain").value(DEFAULT_IS_MAIN));
    }

    @Test
    @Transactional
    void getNonExistingModelAttachment() throws Exception {
        // Get the modelAttachment
        restModelAttachmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingModelAttachment() throws Exception {
        // Initialize the database
        insertedModelAttachment = modelAttachmentRepository.saveAndFlush(modelAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        modelAttachmentSearchRepository.save(modelAttachment);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(modelAttachmentSearchRepository.findAll());

        // Update the modelAttachment
        ModelAttachment updatedModelAttachment = modelAttachmentRepository.findById(modelAttachment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedModelAttachment are not directly saved in db
        em.detach(updatedModelAttachment);
        updatedModelAttachment.isMain(UPDATED_IS_MAIN);
        ModelAttachmentDTO modelAttachmentDTO = modelAttachmentMapper.toDto(updatedModelAttachment);

        restModelAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, modelAttachmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(modelAttachmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the ModelAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedModelAttachmentToMatchAllProperties(updatedModelAttachment);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(modelAttachmentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<ModelAttachment> modelAttachmentSearchList = Streamable.of(modelAttachmentSearchRepository.findAll()).toList();
                ModelAttachment testModelAttachmentSearch = modelAttachmentSearchList.get(searchDatabaseSizeAfter - 1);

                assertModelAttachmentAllPropertiesEquals(testModelAttachmentSearch, updatedModelAttachment);
            });
    }

    @Test
    @Transactional
    void putNonExistingModelAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(modelAttachmentSearchRepository.findAll());
        modelAttachment.setId(longCount.incrementAndGet());

        // Create the ModelAttachment
        ModelAttachmentDTO modelAttachmentDTO = modelAttachmentMapper.toDto(modelAttachment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restModelAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, modelAttachmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(modelAttachmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModelAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(modelAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchModelAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(modelAttachmentSearchRepository.findAll());
        modelAttachment.setId(longCount.incrementAndGet());

        // Create the ModelAttachment
        ModelAttachmentDTO modelAttachmentDTO = modelAttachmentMapper.toDto(modelAttachment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModelAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(modelAttachmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModelAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(modelAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamModelAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(modelAttachmentSearchRepository.findAll());
        modelAttachment.setId(longCount.incrementAndGet());

        // Create the ModelAttachment
        ModelAttachmentDTO modelAttachmentDTO = modelAttachmentMapper.toDto(modelAttachment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModelAttachmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(modelAttachmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ModelAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(modelAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateModelAttachmentWithPatch() throws Exception {
        // Initialize the database
        insertedModelAttachment = modelAttachmentRepository.saveAndFlush(modelAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the modelAttachment using partial update
        ModelAttachment partialUpdatedModelAttachment = new ModelAttachment();
        partialUpdatedModelAttachment.setId(modelAttachment.getId());

        restModelAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedModelAttachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedModelAttachment))
            )
            .andExpect(status().isOk());

        // Validate the ModelAttachment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertModelAttachmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedModelAttachment, modelAttachment),
            getPersistedModelAttachment(modelAttachment)
        );
    }

    @Test
    @Transactional
    void fullUpdateModelAttachmentWithPatch() throws Exception {
        // Initialize the database
        insertedModelAttachment = modelAttachmentRepository.saveAndFlush(modelAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the modelAttachment using partial update
        ModelAttachment partialUpdatedModelAttachment = new ModelAttachment();
        partialUpdatedModelAttachment.setId(modelAttachment.getId());

        partialUpdatedModelAttachment.isMain(UPDATED_IS_MAIN);

        restModelAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedModelAttachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedModelAttachment))
            )
            .andExpect(status().isOk());

        // Validate the ModelAttachment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertModelAttachmentUpdatableFieldsEquals(
            partialUpdatedModelAttachment,
            getPersistedModelAttachment(partialUpdatedModelAttachment)
        );
    }

    @Test
    @Transactional
    void patchNonExistingModelAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(modelAttachmentSearchRepository.findAll());
        modelAttachment.setId(longCount.incrementAndGet());

        // Create the ModelAttachment
        ModelAttachmentDTO modelAttachmentDTO = modelAttachmentMapper.toDto(modelAttachment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restModelAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, modelAttachmentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(modelAttachmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModelAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(modelAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchModelAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(modelAttachmentSearchRepository.findAll());
        modelAttachment.setId(longCount.incrementAndGet());

        // Create the ModelAttachment
        ModelAttachmentDTO modelAttachmentDTO = modelAttachmentMapper.toDto(modelAttachment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModelAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(modelAttachmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ModelAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(modelAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamModelAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(modelAttachmentSearchRepository.findAll());
        modelAttachment.setId(longCount.incrementAndGet());

        // Create the ModelAttachment
        ModelAttachmentDTO modelAttachmentDTO = modelAttachmentMapper.toDto(modelAttachment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restModelAttachmentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(modelAttachmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ModelAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(modelAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteModelAttachment() throws Exception {
        // Initialize the database
        insertedModelAttachment = modelAttachmentRepository.saveAndFlush(modelAttachment);
        modelAttachmentRepository.save(modelAttachment);
        modelAttachmentSearchRepository.save(modelAttachment);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(modelAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the modelAttachment
        restModelAttachmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, modelAttachment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(modelAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchModelAttachment() throws Exception {
        // Initialize the database
        insertedModelAttachment = modelAttachmentRepository.saveAndFlush(modelAttachment);
        modelAttachmentSearchRepository.save(modelAttachment);

        // Search the modelAttachment
        restModelAttachmentMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + modelAttachment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(modelAttachment.getId().intValue())))
            .andExpect(jsonPath("$.[*].isMain").value(hasItem(DEFAULT_IS_MAIN)));
    }

    protected long getRepositoryCount() {
        return modelAttachmentRepository.count();
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

    protected ModelAttachment getPersistedModelAttachment(ModelAttachment modelAttachment) {
        return modelAttachmentRepository.findById(modelAttachment.getId()).orElseThrow();
    }

    protected void assertPersistedModelAttachmentToMatchAllProperties(ModelAttachment expectedModelAttachment) {
        assertModelAttachmentAllPropertiesEquals(expectedModelAttachment, getPersistedModelAttachment(expectedModelAttachment));
    }

    protected void assertPersistedModelAttachmentToMatchUpdatableProperties(ModelAttachment expectedModelAttachment) {
        assertModelAttachmentAllUpdatablePropertiesEquals(expectedModelAttachment, getPersistedModelAttachment(expectedModelAttachment));
    }
}
