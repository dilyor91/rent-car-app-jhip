package uz.carapp.rentcarapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uz.carapp.rentcarapp.domain.ParametrAsserts.*;
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
import uz.carapp.rentcarapp.domain.Parametr;
import uz.carapp.rentcarapp.repository.ParametrRepository;
import uz.carapp.rentcarapp.repository.search.ParametrSearchRepository;
import uz.carapp.rentcarapp.service.dto.ParametrDTO;
import uz.carapp.rentcarapp.service.mapper.ParametrMapper;

/**
 * Integration tests for the {@link ParametrResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ParametrResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final String ENTITY_API_URL = "/api/parametrs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/parametrs/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ParametrRepository parametrRepository;

    @Autowired
    private ParametrMapper parametrMapper;

    @Autowired
    private ParametrSearchRepository parametrSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restParametrMockMvc;

    private Parametr parametr;

    private Parametr insertedParametr;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Parametr createEntity() {
        return new Parametr().name(DEFAULT_NAME).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Parametr createUpdatedEntity() {
        return new Parametr().name(UPDATED_NAME).status(UPDATED_STATUS);
    }

    @BeforeEach
    public void initTest() {
        parametr = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedParametr != null) {
            parametrRepository.delete(insertedParametr);
            parametrSearchRepository.delete(insertedParametr);
            insertedParametr = null;
        }
    }

    @Test
    @Transactional
    void createParametr() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(parametrSearchRepository.findAll());
        // Create the Parametr
        ParametrDTO parametrDTO = parametrMapper.toDto(parametr);
        var returnedParametrDTO = om.readValue(
            restParametrMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parametrDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ParametrDTO.class
        );

        // Validate the Parametr in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedParametr = parametrMapper.toEntity(returnedParametrDTO);
        assertParametrUpdatableFieldsEquals(returnedParametr, getPersistedParametr(returnedParametr));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(parametrSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedParametr = returnedParametr;
    }

    @Test
    @Transactional
    void createParametrWithExistingId() throws Exception {
        // Create the Parametr with an existing ID
        parametr.setId(1L);
        ParametrDTO parametrDTO = parametrMapper.toDto(parametr);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(parametrSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restParametrMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parametrDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Parametr in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(parametrSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(parametrSearchRepository.findAll());
        // set the field null
        parametr.setName(null);

        // Create the Parametr, which fails.
        ParametrDTO parametrDTO = parametrMapper.toDto(parametr);

        restParametrMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parametrDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(parametrSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllParametrs() throws Exception {
        // Initialize the database
        insertedParametr = parametrRepository.saveAndFlush(parametr);

        // Get all the parametrList
        restParametrMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parametr.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @Test
    @Transactional
    void getParametr() throws Exception {
        // Initialize the database
        insertedParametr = parametrRepository.saveAndFlush(parametr);

        // Get the parametr
        restParametrMockMvc
            .perform(get(ENTITY_API_URL_ID, parametr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(parametr.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    void getNonExistingParametr() throws Exception {
        // Get the parametr
        restParametrMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingParametr() throws Exception {
        // Initialize the database
        insertedParametr = parametrRepository.saveAndFlush(parametr);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        parametrSearchRepository.save(parametr);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(parametrSearchRepository.findAll());

        // Update the parametr
        Parametr updatedParametr = parametrRepository.findById(parametr.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedParametr are not directly saved in db
        em.detach(updatedParametr);
        updatedParametr.name(UPDATED_NAME).status(UPDATED_STATUS);
        ParametrDTO parametrDTO = parametrMapper.toDto(updatedParametr);

        restParametrMockMvc
            .perform(
                put(ENTITY_API_URL_ID, parametrDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(parametrDTO))
            )
            .andExpect(status().isOk());

        // Validate the Parametr in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedParametrToMatchAllProperties(updatedParametr);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(parametrSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Parametr> parametrSearchList = Streamable.of(parametrSearchRepository.findAll()).toList();
                Parametr testParametrSearch = parametrSearchList.get(searchDatabaseSizeAfter - 1);

                assertParametrAllPropertiesEquals(testParametrSearch, updatedParametr);
            });
    }

    @Test
    @Transactional
    void putNonExistingParametr() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(parametrSearchRepository.findAll());
        parametr.setId(longCount.incrementAndGet());

        // Create the Parametr
        ParametrDTO parametrDTO = parametrMapper.toDto(parametr);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParametrMockMvc
            .perform(
                put(ENTITY_API_URL_ID, parametrDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(parametrDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Parametr in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(parametrSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchParametr() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(parametrSearchRepository.findAll());
        parametr.setId(longCount.incrementAndGet());

        // Create the Parametr
        ParametrDTO parametrDTO = parametrMapper.toDto(parametr);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParametrMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(parametrDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Parametr in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(parametrSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamParametr() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(parametrSearchRepository.findAll());
        parametr.setId(longCount.incrementAndGet());

        // Create the Parametr
        ParametrDTO parametrDTO = parametrMapper.toDto(parametr);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParametrMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(parametrDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Parametr in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(parametrSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateParametrWithPatch() throws Exception {
        // Initialize the database
        insertedParametr = parametrRepository.saveAndFlush(parametr);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the parametr using partial update
        Parametr partialUpdatedParametr = new Parametr();
        partialUpdatedParametr.setId(parametr.getId());

        partialUpdatedParametr.status(UPDATED_STATUS);

        restParametrMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParametr.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParametr))
            )
            .andExpect(status().isOk());

        // Validate the Parametr in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParametrUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedParametr, parametr), getPersistedParametr(parametr));
    }

    @Test
    @Transactional
    void fullUpdateParametrWithPatch() throws Exception {
        // Initialize the database
        insertedParametr = parametrRepository.saveAndFlush(parametr);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the parametr using partial update
        Parametr partialUpdatedParametr = new Parametr();
        partialUpdatedParametr.setId(parametr.getId());

        partialUpdatedParametr.name(UPDATED_NAME).status(UPDATED_STATUS);

        restParametrMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParametr.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedParametr))
            )
            .andExpect(status().isOk());

        // Validate the Parametr in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertParametrUpdatableFieldsEquals(partialUpdatedParametr, getPersistedParametr(partialUpdatedParametr));
    }

    @Test
    @Transactional
    void patchNonExistingParametr() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(parametrSearchRepository.findAll());
        parametr.setId(longCount.incrementAndGet());

        // Create the Parametr
        ParametrDTO parametrDTO = parametrMapper.toDto(parametr);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParametrMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, parametrDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(parametrDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Parametr in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(parametrSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchParametr() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(parametrSearchRepository.findAll());
        parametr.setId(longCount.incrementAndGet());

        // Create the Parametr
        ParametrDTO parametrDTO = parametrMapper.toDto(parametr);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParametrMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(parametrDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Parametr in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(parametrSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamParametr() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(parametrSearchRepository.findAll());
        parametr.setId(longCount.incrementAndGet());

        // Create the Parametr
        ParametrDTO parametrDTO = parametrMapper.toDto(parametr);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParametrMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(parametrDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Parametr in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(parametrSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteParametr() throws Exception {
        // Initialize the database
        insertedParametr = parametrRepository.saveAndFlush(parametr);
        parametrRepository.save(parametr);
        parametrSearchRepository.save(parametr);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(parametrSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the parametr
        restParametrMockMvc
            .perform(delete(ENTITY_API_URL_ID, parametr.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(parametrSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchParametr() throws Exception {
        // Initialize the database
        insertedParametr = parametrRepository.saveAndFlush(parametr);
        parametrSearchRepository.save(parametr);

        // Search the parametr
        restParametrMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + parametr.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(parametr.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    protected long getRepositoryCount() {
        return parametrRepository.count();
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

    protected Parametr getPersistedParametr(Parametr parametr) {
        return parametrRepository.findById(parametr.getId()).orElseThrow();
    }

    protected void assertPersistedParametrToMatchAllProperties(Parametr expectedParametr) {
        assertParametrAllPropertiesEquals(expectedParametr, getPersistedParametr(expectedParametr));
    }

    protected void assertPersistedParametrToMatchUpdatableProperties(Parametr expectedParametr) {
        assertParametrAllUpdatablePropertiesEquals(expectedParametr, getPersistedParametr(expectedParametr));
    }
}
