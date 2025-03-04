package uz.carapp.rentcarapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uz.carapp.rentcarapp.domain.DocAttachmentAsserts.*;
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
import uz.carapp.rentcarapp.domain.DocAttachment;
import uz.carapp.rentcarapp.repository.DocAttachmentRepository;
import uz.carapp.rentcarapp.repository.search.DocAttachmentSearchRepository;
import uz.carapp.rentcarapp.service.dto.DocAttachmentDTO;
import uz.carapp.rentcarapp.service.mapper.DocAttachmentMapper;

/**
 * Integration tests for the {@link DocAttachmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DocAttachmentResourceIT {

    private static final String ENTITY_API_URL = "/api/doc-attachments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/doc-attachments/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private DocAttachmentRepository docAttachmentRepository;

    @Autowired
    private DocAttachmentMapper docAttachmentMapper;

    @Autowired
    private DocAttachmentSearchRepository docAttachmentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDocAttachmentMockMvc;

    private DocAttachment docAttachment;

    private DocAttachment insertedDocAttachment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DocAttachment createEntity() {
        return new DocAttachment();
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DocAttachment createUpdatedEntity() {
        return new DocAttachment();
    }

    @BeforeEach
    public void initTest() {
        docAttachment = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedDocAttachment != null) {
            docAttachmentRepository.delete(insertedDocAttachment);
            docAttachmentSearchRepository.delete(insertedDocAttachment);
            insertedDocAttachment = null;
        }
    }

    @Test
    @Transactional
    void createDocAttachment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(docAttachmentSearchRepository.findAll());
        // Create the DocAttachment
        DocAttachmentDTO docAttachmentDTO = docAttachmentMapper.toDto(docAttachment);
        var returnedDocAttachmentDTO = om.readValue(
            restDocAttachmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(docAttachmentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            DocAttachmentDTO.class
        );

        // Validate the DocAttachment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedDocAttachment = docAttachmentMapper.toEntity(returnedDocAttachmentDTO);
        assertDocAttachmentUpdatableFieldsEquals(returnedDocAttachment, getPersistedDocAttachment(returnedDocAttachment));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(docAttachmentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedDocAttachment = returnedDocAttachment;
    }

    @Test
    @Transactional
    void createDocAttachmentWithExistingId() throws Exception {
        // Create the DocAttachment with an existing ID
        docAttachment.setId(1L);
        DocAttachmentDTO docAttachmentDTO = docAttachmentMapper.toDto(docAttachment);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(docAttachmentSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restDocAttachmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(docAttachmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the DocAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(docAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllDocAttachments() throws Exception {
        // Initialize the database
        insertedDocAttachment = docAttachmentRepository.saveAndFlush(docAttachment);

        // Get all the docAttachmentList
        restDocAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(docAttachment.getId().intValue())));
    }

    @Test
    @Transactional
    void getDocAttachment() throws Exception {
        // Initialize the database
        insertedDocAttachment = docAttachmentRepository.saveAndFlush(docAttachment);

        // Get the docAttachment
        restDocAttachmentMockMvc
            .perform(get(ENTITY_API_URL_ID, docAttachment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(docAttachment.getId().intValue()));
    }

    @Test
    @Transactional
    void getNonExistingDocAttachment() throws Exception {
        // Get the docAttachment
        restDocAttachmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingDocAttachment() throws Exception {
        // Initialize the database
        insertedDocAttachment = docAttachmentRepository.saveAndFlush(docAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        docAttachmentSearchRepository.save(docAttachment);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(docAttachmentSearchRepository.findAll());

        // Update the docAttachment
        DocAttachment updatedDocAttachment = docAttachmentRepository.findById(docAttachment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedDocAttachment are not directly saved in db
        em.detach(updatedDocAttachment);
        DocAttachmentDTO docAttachmentDTO = docAttachmentMapper.toDto(updatedDocAttachment);

        restDocAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, docAttachmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(docAttachmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the DocAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedDocAttachmentToMatchAllProperties(updatedDocAttachment);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(docAttachmentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<DocAttachment> docAttachmentSearchList = Streamable.of(docAttachmentSearchRepository.findAll()).toList();
                DocAttachment testDocAttachmentSearch = docAttachmentSearchList.get(searchDatabaseSizeAfter - 1);

                assertDocAttachmentAllPropertiesEquals(testDocAttachmentSearch, updatedDocAttachment);
            });
    }

    @Test
    @Transactional
    void putNonExistingDocAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(docAttachmentSearchRepository.findAll());
        docAttachment.setId(longCount.incrementAndGet());

        // Create the DocAttachment
        DocAttachmentDTO docAttachmentDTO = docAttachmentMapper.toDto(docAttachment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDocAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, docAttachmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(docAttachmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DocAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(docAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchDocAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(docAttachmentSearchRepository.findAll());
        docAttachment.setId(longCount.incrementAndGet());

        // Create the DocAttachment
        DocAttachmentDTO docAttachmentDTO = docAttachmentMapper.toDto(docAttachment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(docAttachmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DocAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(docAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDocAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(docAttachmentSearchRepository.findAll());
        docAttachment.setId(longCount.incrementAndGet());

        // Create the DocAttachment
        DocAttachmentDTO docAttachmentDTO = docAttachmentMapper.toDto(docAttachment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocAttachmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(docAttachmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DocAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(docAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateDocAttachmentWithPatch() throws Exception {
        // Initialize the database
        insertedDocAttachment = docAttachmentRepository.saveAndFlush(docAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the docAttachment using partial update
        DocAttachment partialUpdatedDocAttachment = new DocAttachment();
        partialUpdatedDocAttachment.setId(docAttachment.getId());

        restDocAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDocAttachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDocAttachment))
            )
            .andExpect(status().isOk());

        // Validate the DocAttachment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDocAttachmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedDocAttachment, docAttachment),
            getPersistedDocAttachment(docAttachment)
        );
    }

    @Test
    @Transactional
    void fullUpdateDocAttachmentWithPatch() throws Exception {
        // Initialize the database
        insertedDocAttachment = docAttachmentRepository.saveAndFlush(docAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the docAttachment using partial update
        DocAttachment partialUpdatedDocAttachment = new DocAttachment();
        partialUpdatedDocAttachment.setId(docAttachment.getId());

        restDocAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDocAttachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedDocAttachment))
            )
            .andExpect(status().isOk());

        // Validate the DocAttachment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertDocAttachmentUpdatableFieldsEquals(partialUpdatedDocAttachment, getPersistedDocAttachment(partialUpdatedDocAttachment));
    }

    @Test
    @Transactional
    void patchNonExistingDocAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(docAttachmentSearchRepository.findAll());
        docAttachment.setId(longCount.incrementAndGet());

        // Create the DocAttachment
        DocAttachmentDTO docAttachmentDTO = docAttachmentMapper.toDto(docAttachment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDocAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, docAttachmentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(docAttachmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DocAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(docAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDocAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(docAttachmentSearchRepository.findAll());
        docAttachment.setId(longCount.incrementAndGet());

        // Create the DocAttachment
        DocAttachmentDTO docAttachmentDTO = docAttachmentMapper.toDto(docAttachment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(docAttachmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DocAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(docAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDocAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(docAttachmentSearchRepository.findAll());
        docAttachment.setId(longCount.incrementAndGet());

        // Create the DocAttachment
        DocAttachmentDTO docAttachmentDTO = docAttachmentMapper.toDto(docAttachment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDocAttachmentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(docAttachmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the DocAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(docAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteDocAttachment() throws Exception {
        // Initialize the database
        insertedDocAttachment = docAttachmentRepository.saveAndFlush(docAttachment);
        docAttachmentRepository.save(docAttachment);
        docAttachmentSearchRepository.save(docAttachment);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(docAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the docAttachment
        restDocAttachmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, docAttachment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(docAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchDocAttachment() throws Exception {
        // Initialize the database
        insertedDocAttachment = docAttachmentRepository.saveAndFlush(docAttachment);
        docAttachmentSearchRepository.save(docAttachment);

        // Search the docAttachment
        restDocAttachmentMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + docAttachment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(docAttachment.getId().intValue())));
    }

    protected long getRepositoryCount() {
        return docAttachmentRepository.count();
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

    protected DocAttachment getPersistedDocAttachment(DocAttachment docAttachment) {
        return docAttachmentRepository.findById(docAttachment.getId()).orElseThrow();
    }

    protected void assertPersistedDocAttachmentToMatchAllProperties(DocAttachment expectedDocAttachment) {
        assertDocAttachmentAllPropertiesEquals(expectedDocAttachment, getPersistedDocAttachment(expectedDocAttachment));
    }

    protected void assertPersistedDocAttachmentToMatchUpdatableProperties(DocAttachment expectedDocAttachment) {
        assertDocAttachmentAllUpdatablePropertiesEquals(expectedDocAttachment, getPersistedDocAttachment(expectedDocAttachment));
    }
}
