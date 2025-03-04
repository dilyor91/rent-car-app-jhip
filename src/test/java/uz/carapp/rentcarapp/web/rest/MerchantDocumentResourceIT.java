package uz.carapp.rentcarapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uz.carapp.rentcarapp.domain.MerchantDocumentAsserts.*;
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
import uz.carapp.rentcarapp.domain.MerchantDocument;
import uz.carapp.rentcarapp.repository.MerchantDocumentRepository;
import uz.carapp.rentcarapp.repository.search.MerchantDocumentSearchRepository;
import uz.carapp.rentcarapp.service.dto.MerchantDocumentDTO;
import uz.carapp.rentcarapp.service.mapper.MerchantDocumentMapper;

/**
 * Integration tests for the {@link MerchantDocumentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MerchantDocumentResourceIT {

    private static final String ENTITY_API_URL = "/api/merchant-documents";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/merchant-documents/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MerchantDocumentRepository merchantDocumentRepository;

    @Autowired
    private MerchantDocumentMapper merchantDocumentMapper;

    @Autowired
    private MerchantDocumentSearchRepository merchantDocumentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMerchantDocumentMockMvc;

    private MerchantDocument merchantDocument;

    private MerchantDocument insertedMerchantDocument;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MerchantDocument createEntity() {
        return new MerchantDocument();
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MerchantDocument createUpdatedEntity() {
        return new MerchantDocument();
    }

    @BeforeEach
    public void initTest() {
        merchantDocument = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedMerchantDocument != null) {
            merchantDocumentRepository.delete(insertedMerchantDocument);
            merchantDocumentSearchRepository.delete(insertedMerchantDocument);
            insertedMerchantDocument = null;
        }
    }

    @Test
    @Transactional
    void createMerchantDocument() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantDocumentSearchRepository.findAll());
        // Create the MerchantDocument
        MerchantDocumentDTO merchantDocumentDTO = merchantDocumentMapper.toDto(merchantDocument);
        var returnedMerchantDocumentDTO = om.readValue(
            restMerchantDocumentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(merchantDocumentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MerchantDocumentDTO.class
        );

        // Validate the MerchantDocument in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMerchantDocument = merchantDocumentMapper.toEntity(returnedMerchantDocumentDTO);
        assertMerchantDocumentUpdatableFieldsEquals(returnedMerchantDocument, getPersistedMerchantDocument(returnedMerchantDocument));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantDocumentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMerchantDocument = returnedMerchantDocument;
    }

    @Test
    @Transactional
    void createMerchantDocumentWithExistingId() throws Exception {
        // Create the MerchantDocument with an existing ID
        merchantDocument.setId(1L);
        MerchantDocumentDTO merchantDocumentDTO = merchantDocumentMapper.toDto(merchantDocument);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantDocumentSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restMerchantDocumentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(merchantDocumentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MerchantDocument in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllMerchantDocuments() throws Exception {
        // Initialize the database
        insertedMerchantDocument = merchantDocumentRepository.saveAndFlush(merchantDocument);

        // Get all the merchantDocumentList
        restMerchantDocumentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(merchantDocument.getId().intValue())));
    }

    @Test
    @Transactional
    void getMerchantDocument() throws Exception {
        // Initialize the database
        insertedMerchantDocument = merchantDocumentRepository.saveAndFlush(merchantDocument);

        // Get the merchantDocument
        restMerchantDocumentMockMvc
            .perform(get(ENTITY_API_URL_ID, merchantDocument.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(merchantDocument.getId().intValue()));
    }

    @Test
    @Transactional
    void getNonExistingMerchantDocument() throws Exception {
        // Get the merchantDocument
        restMerchantDocumentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMerchantDocument() throws Exception {
        // Initialize the database
        insertedMerchantDocument = merchantDocumentRepository.saveAndFlush(merchantDocument);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        merchantDocumentSearchRepository.save(merchantDocument);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantDocumentSearchRepository.findAll());

        // Update the merchantDocument
        MerchantDocument updatedMerchantDocument = merchantDocumentRepository.findById(merchantDocument.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMerchantDocument are not directly saved in db
        em.detach(updatedMerchantDocument);
        MerchantDocumentDTO merchantDocumentDTO = merchantDocumentMapper.toDto(updatedMerchantDocument);

        restMerchantDocumentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, merchantDocumentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(merchantDocumentDTO))
            )
            .andExpect(status().isOk());

        // Validate the MerchantDocument in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMerchantDocumentToMatchAllProperties(updatedMerchantDocument);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantDocumentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MerchantDocument> merchantDocumentSearchList = Streamable.of(merchantDocumentSearchRepository.findAll()).toList();
                MerchantDocument testMerchantDocumentSearch = merchantDocumentSearchList.get(searchDatabaseSizeAfter - 1);

                assertMerchantDocumentAllPropertiesEquals(testMerchantDocumentSearch, updatedMerchantDocument);
            });
    }

    @Test
    @Transactional
    void putNonExistingMerchantDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantDocumentSearchRepository.findAll());
        merchantDocument.setId(longCount.incrementAndGet());

        // Create the MerchantDocument
        MerchantDocumentDTO merchantDocumentDTO = merchantDocumentMapper.toDto(merchantDocument);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMerchantDocumentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, merchantDocumentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(merchantDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MerchantDocument in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchMerchantDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantDocumentSearchRepository.findAll());
        merchantDocument.setId(longCount.incrementAndGet());

        // Create the MerchantDocument
        MerchantDocumentDTO merchantDocumentDTO = merchantDocumentMapper.toDto(merchantDocument);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMerchantDocumentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(merchantDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MerchantDocument in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMerchantDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantDocumentSearchRepository.findAll());
        merchantDocument.setId(longCount.incrementAndGet());

        // Create the MerchantDocument
        MerchantDocumentDTO merchantDocumentDTO = merchantDocumentMapper.toDto(merchantDocument);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMerchantDocumentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(merchantDocumentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MerchantDocument in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateMerchantDocumentWithPatch() throws Exception {
        // Initialize the database
        insertedMerchantDocument = merchantDocumentRepository.saveAndFlush(merchantDocument);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the merchantDocument using partial update
        MerchantDocument partialUpdatedMerchantDocument = new MerchantDocument();
        partialUpdatedMerchantDocument.setId(merchantDocument.getId());

        restMerchantDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMerchantDocument.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMerchantDocument))
            )
            .andExpect(status().isOk());

        // Validate the MerchantDocument in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMerchantDocumentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMerchantDocument, merchantDocument),
            getPersistedMerchantDocument(merchantDocument)
        );
    }

    @Test
    @Transactional
    void fullUpdateMerchantDocumentWithPatch() throws Exception {
        // Initialize the database
        insertedMerchantDocument = merchantDocumentRepository.saveAndFlush(merchantDocument);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the merchantDocument using partial update
        MerchantDocument partialUpdatedMerchantDocument = new MerchantDocument();
        partialUpdatedMerchantDocument.setId(merchantDocument.getId());

        restMerchantDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMerchantDocument.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMerchantDocument))
            )
            .andExpect(status().isOk());

        // Validate the MerchantDocument in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMerchantDocumentUpdatableFieldsEquals(
            partialUpdatedMerchantDocument,
            getPersistedMerchantDocument(partialUpdatedMerchantDocument)
        );
    }

    @Test
    @Transactional
    void patchNonExistingMerchantDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantDocumentSearchRepository.findAll());
        merchantDocument.setId(longCount.incrementAndGet());

        // Create the MerchantDocument
        MerchantDocumentDTO merchantDocumentDTO = merchantDocumentMapper.toDto(merchantDocument);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMerchantDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, merchantDocumentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(merchantDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MerchantDocument in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMerchantDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantDocumentSearchRepository.findAll());
        merchantDocument.setId(longCount.incrementAndGet());

        // Create the MerchantDocument
        MerchantDocumentDTO merchantDocumentDTO = merchantDocumentMapper.toDto(merchantDocument);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMerchantDocumentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(merchantDocumentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MerchantDocument in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMerchantDocument() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantDocumentSearchRepository.findAll());
        merchantDocument.setId(longCount.incrementAndGet());

        // Create the MerchantDocument
        MerchantDocumentDTO merchantDocumentDTO = merchantDocumentMapper.toDto(merchantDocument);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMerchantDocumentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(merchantDocumentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MerchantDocument in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteMerchantDocument() throws Exception {
        // Initialize the database
        insertedMerchantDocument = merchantDocumentRepository.saveAndFlush(merchantDocument);
        merchantDocumentRepository.save(merchantDocument);
        merchantDocumentSearchRepository.save(merchantDocument);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the merchantDocument
        restMerchantDocumentMockMvc
            .perform(delete(ENTITY_API_URL_ID, merchantDocument.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantDocumentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchMerchantDocument() throws Exception {
        // Initialize the database
        insertedMerchantDocument = merchantDocumentRepository.saveAndFlush(merchantDocument);
        merchantDocumentSearchRepository.save(merchantDocument);

        // Search the merchantDocument
        restMerchantDocumentMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + merchantDocument.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(merchantDocument.getId().intValue())));
    }

    protected long getRepositoryCount() {
        return merchantDocumentRepository.count();
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

    protected MerchantDocument getPersistedMerchantDocument(MerchantDocument merchantDocument) {
        return merchantDocumentRepository.findById(merchantDocument.getId()).orElseThrow();
    }

    protected void assertPersistedMerchantDocumentToMatchAllProperties(MerchantDocument expectedMerchantDocument) {
        assertMerchantDocumentAllPropertiesEquals(expectedMerchantDocument, getPersistedMerchantDocument(expectedMerchantDocument));
    }

    protected void assertPersistedMerchantDocumentToMatchUpdatableProperties(MerchantDocument expectedMerchantDocument) {
        assertMerchantDocumentAllUpdatablePropertiesEquals(
            expectedMerchantDocument,
            getPersistedMerchantDocument(expectedMerchantDocument)
        );
    }
}
