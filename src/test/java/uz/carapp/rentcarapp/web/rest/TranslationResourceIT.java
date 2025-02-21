package uz.carapp.rentcarapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uz.carapp.rentcarapp.domain.TranslationAsserts.*;
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
import uz.carapp.rentcarapp.domain.Translation;
import uz.carapp.rentcarapp.domain.enumeration.LanguageEnum;
import uz.carapp.rentcarapp.repository.TranslationRepository;
import uz.carapp.rentcarapp.repository.search.TranslationSearchRepository;
import uz.carapp.rentcarapp.service.dto.TranslationDTO;
import uz.carapp.rentcarapp.service.mapper.TranslationMapper;

/**
 * Integration tests for the {@link TranslationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TranslationResourceIT {

    private static final String DEFAULT_ENTITY_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_ENTITY_TYPE = "BBBBBBBBBB";

    private static final Long DEFAULT_ENTITY_ID = 1L;
    private static final Long UPDATED_ENTITY_ID = 2L;

    private static final LanguageEnum DEFAULT_LANG = LanguageEnum.UZ;
    private static final LanguageEnum UPDATED_LANG = LanguageEnum.RU;

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/translations";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/translations/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TranslationRepository translationRepository;

    @Autowired
    private TranslationMapper translationMapper;

    @Autowired
    private TranslationSearchRepository translationSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTranslationMockMvc;

    private Translation translation;

    private Translation insertedTranslation;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Translation createEntity() {
        return new Translation()
            .entityType(DEFAULT_ENTITY_TYPE)
            .entityId(DEFAULT_ENTITY_ID)
            .lang(DEFAULT_LANG)
            .value(DEFAULT_VALUE)
            .description(DEFAULT_DESCRIPTION);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Translation createUpdatedEntity() {
        return new Translation()
            .entityType(UPDATED_ENTITY_TYPE)
            .entityId(UPDATED_ENTITY_ID)
            .lang(UPDATED_LANG)
            .value(UPDATED_VALUE)
            .description(UPDATED_DESCRIPTION);
    }

    @BeforeEach
    public void initTest() {
        translation = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedTranslation != null) {
            translationRepository.delete(insertedTranslation);
            translationSearchRepository.delete(insertedTranslation);
            insertedTranslation = null;
        }
    }

    @Test
    @Transactional
    void createTranslation() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(translationSearchRepository.findAll());
        // Create the Translation
        TranslationDTO translationDTO = translationMapper.toDto(translation);
        var returnedTranslationDTO = om.readValue(
            restTranslationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(translationDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TranslationDTO.class
        );

        // Validate the Translation in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTranslation = translationMapper.toEntity(returnedTranslationDTO);
        assertTranslationUpdatableFieldsEquals(returnedTranslation, getPersistedTranslation(returnedTranslation));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(translationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedTranslation = returnedTranslation;
    }

    @Test
    @Transactional
    void createTranslationWithExistingId() throws Exception {
        // Create the Translation with an existing ID
        translation.setId(1L);
        TranslationDTO translationDTO = translationMapper.toDto(translation);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(translationSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTranslationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(translationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Translation in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(translationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllTranslations() throws Exception {
        // Initialize the database
        insertedTranslation = translationRepository.saveAndFlush(translation);

        // Get all the translationList
        restTranslationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(translation.getId().intValue())))
            .andExpect(jsonPath("$.[*].entityType").value(hasItem(DEFAULT_ENTITY_TYPE)))
            .andExpect(jsonPath("$.[*].entityId").value(hasItem(DEFAULT_ENTITY_ID.intValue())))
            .andExpect(jsonPath("$.[*].lang").value(hasItem(DEFAULT_LANG.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getTranslation() throws Exception {
        // Initialize the database
        insertedTranslation = translationRepository.saveAndFlush(translation);

        // Get the translation
        restTranslationMockMvc
            .perform(get(ENTITY_API_URL_ID, translation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(translation.getId().intValue()))
            .andExpect(jsonPath("$.entityType").value(DEFAULT_ENTITY_TYPE))
            .andExpect(jsonPath("$.entityId").value(DEFAULT_ENTITY_ID.intValue()))
            .andExpect(jsonPath("$.lang").value(DEFAULT_LANG.toString()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingTranslation() throws Exception {
        // Get the translation
        restTranslationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTranslation() throws Exception {
        // Initialize the database
        insertedTranslation = translationRepository.saveAndFlush(translation);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        translationSearchRepository.save(translation);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(translationSearchRepository.findAll());

        // Update the translation
        Translation updatedTranslation = translationRepository.findById(translation.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTranslation are not directly saved in db
        em.detach(updatedTranslation);
        updatedTranslation
            .entityType(UPDATED_ENTITY_TYPE)
            .entityId(UPDATED_ENTITY_ID)
            .lang(UPDATED_LANG)
            .value(UPDATED_VALUE)
            .description(UPDATED_DESCRIPTION);
        TranslationDTO translationDTO = translationMapper.toDto(updatedTranslation);

        restTranslationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, translationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(translationDTO))
            )
            .andExpect(status().isOk());

        // Validate the Translation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTranslationToMatchAllProperties(updatedTranslation);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(translationSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Translation> translationSearchList = Streamable.of(translationSearchRepository.findAll()).toList();
                Translation testTranslationSearch = translationSearchList.get(searchDatabaseSizeAfter - 1);

                assertTranslationAllPropertiesEquals(testTranslationSearch, updatedTranslation);
            });
    }

    @Test
    @Transactional
    void putNonExistingTranslation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(translationSearchRepository.findAll());
        translation.setId(longCount.incrementAndGet());

        // Create the Translation
        TranslationDTO translationDTO = translationMapper.toDto(translation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTranslationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, translationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(translationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Translation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(translationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchTranslation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(translationSearchRepository.findAll());
        translation.setId(longCount.incrementAndGet());

        // Create the Translation
        TranslationDTO translationDTO = translationMapper.toDto(translation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTranslationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(translationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Translation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(translationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTranslation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(translationSearchRepository.findAll());
        translation.setId(longCount.incrementAndGet());

        // Create the Translation
        TranslationDTO translationDTO = translationMapper.toDto(translation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTranslationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(translationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Translation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(translationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateTranslationWithPatch() throws Exception {
        // Initialize the database
        insertedTranslation = translationRepository.saveAndFlush(translation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the translation using partial update
        Translation partialUpdatedTranslation = new Translation();
        partialUpdatedTranslation.setId(translation.getId());

        partialUpdatedTranslation.entityType(UPDATED_ENTITY_TYPE).lang(UPDATED_LANG).value(UPDATED_VALUE);

        restTranslationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTranslation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTranslation))
            )
            .andExpect(status().isOk());

        // Validate the Translation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTranslationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTranslation, translation),
            getPersistedTranslation(translation)
        );
    }

    @Test
    @Transactional
    void fullUpdateTranslationWithPatch() throws Exception {
        // Initialize the database
        insertedTranslation = translationRepository.saveAndFlush(translation);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the translation using partial update
        Translation partialUpdatedTranslation = new Translation();
        partialUpdatedTranslation.setId(translation.getId());

        partialUpdatedTranslation
            .entityType(UPDATED_ENTITY_TYPE)
            .entityId(UPDATED_ENTITY_ID)
            .lang(UPDATED_LANG)
            .value(UPDATED_VALUE)
            .description(UPDATED_DESCRIPTION);

        restTranslationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTranslation.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTranslation))
            )
            .andExpect(status().isOk());

        // Validate the Translation in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTranslationUpdatableFieldsEquals(partialUpdatedTranslation, getPersistedTranslation(partialUpdatedTranslation));
    }

    @Test
    @Transactional
    void patchNonExistingTranslation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(translationSearchRepository.findAll());
        translation.setId(longCount.incrementAndGet());

        // Create the Translation
        TranslationDTO translationDTO = translationMapper.toDto(translation);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTranslationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, translationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(translationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Translation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(translationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTranslation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(translationSearchRepository.findAll());
        translation.setId(longCount.incrementAndGet());

        // Create the Translation
        TranslationDTO translationDTO = translationMapper.toDto(translation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTranslationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(translationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Translation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(translationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTranslation() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(translationSearchRepository.findAll());
        translation.setId(longCount.incrementAndGet());

        // Create the Translation
        TranslationDTO translationDTO = translationMapper.toDto(translation);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTranslationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(translationDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Translation in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(translationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteTranslation() throws Exception {
        // Initialize the database
        insertedTranslation = translationRepository.saveAndFlush(translation);
        translationRepository.save(translation);
        translationSearchRepository.save(translation);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(translationSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the translation
        restTranslationMockMvc
            .perform(delete(ENTITY_API_URL_ID, translation.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(translationSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchTranslation() throws Exception {
        // Initialize the database
        insertedTranslation = translationRepository.saveAndFlush(translation);
        translationSearchRepository.save(translation);

        // Search the translation
        restTranslationMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + translation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(translation.getId().intValue())))
            .andExpect(jsonPath("$.[*].entityType").value(hasItem(DEFAULT_ENTITY_TYPE)))
            .andExpect(jsonPath("$.[*].entityId").value(hasItem(DEFAULT_ENTITY_ID.intValue())))
            .andExpect(jsonPath("$.[*].lang").value(hasItem(DEFAULT_LANG.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    protected long getRepositoryCount() {
        return translationRepository.count();
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

    protected Translation getPersistedTranslation(Translation translation) {
        return translationRepository.findById(translation.getId()).orElseThrow();
    }

    protected void assertPersistedTranslationToMatchAllProperties(Translation expectedTranslation) {
        assertTranslationAllPropertiesEquals(expectedTranslation, getPersistedTranslation(expectedTranslation));
    }

    protected void assertPersistedTranslationToMatchUpdatableProperties(Translation expectedTranslation) {
        assertTranslationAllUpdatablePropertiesEquals(expectedTranslation, getPersistedTranslation(expectedTranslation));
    }
}
