package uz.carapp.rentcarapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uz.carapp.rentcarapp.domain.CarTemplateAsserts.*;
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
import uz.carapp.rentcarapp.domain.CarTemplate;
import uz.carapp.rentcarapp.repository.CarTemplateRepository;
import uz.carapp.rentcarapp.repository.search.CarTemplateSearchRepository;
import uz.carapp.rentcarapp.service.dto.CarTemplateDTO;
import uz.carapp.rentcarapp.service.mapper.CarTemplateMapper;

/**
 * Integration tests for the {@link CarTemplateResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CarTemplateResourceIT {

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final String ENTITY_API_URL = "/api/car-templates";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/car-templates/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CarTemplateRepository carTemplateRepository;

    @Autowired
    private CarTemplateMapper carTemplateMapper;

    @Autowired
    private CarTemplateSearchRepository carTemplateSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCarTemplateMockMvc;

    private CarTemplate carTemplate;

    private CarTemplate insertedCarTemplate;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CarTemplate createEntity() {
        return new CarTemplate().status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CarTemplate createUpdatedEntity() {
        return new CarTemplate().status(UPDATED_STATUS);
    }

    @BeforeEach
    public void initTest() {
        carTemplate = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedCarTemplate != null) {
            carTemplateRepository.delete(insertedCarTemplate);
            carTemplateSearchRepository.delete(insertedCarTemplate);
            insertedCarTemplate = null;
        }
    }

    @Test
    @Transactional
    void createCarTemplate() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carTemplateSearchRepository.findAll());
        // Create the CarTemplate
        CarTemplateDTO carTemplateDTO = carTemplateMapper.toDto(carTemplate);
        var returnedCarTemplateDTO = om.readValue(
            restCarTemplateMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carTemplateDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CarTemplateDTO.class
        );

        // Validate the CarTemplate in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCarTemplate = carTemplateMapper.toEntity(returnedCarTemplateDTO);
        assertCarTemplateUpdatableFieldsEquals(returnedCarTemplate, getPersistedCarTemplate(returnedCarTemplate));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(carTemplateSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedCarTemplate = returnedCarTemplate;
    }

    @Test
    @Transactional
    void createCarTemplateWithExistingId() throws Exception {
        // Create the CarTemplate with an existing ID
        carTemplate.setId(1L);
        CarTemplateDTO carTemplateDTO = carTemplateMapper.toDto(carTemplate);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carTemplateSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCarTemplateMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carTemplateDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CarTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carTemplateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCarTemplates() throws Exception {
        // Initialize the database
        insertedCarTemplate = carTemplateRepository.saveAndFlush(carTemplate);

        // Get all the carTemplateList
        restCarTemplateMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carTemplate.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @Test
    @Transactional
    void getCarTemplate() throws Exception {
        // Initialize the database
        insertedCarTemplate = carTemplateRepository.saveAndFlush(carTemplate);

        // Get the carTemplate
        restCarTemplateMockMvc
            .perform(get(ENTITY_API_URL_ID, carTemplate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(carTemplate.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    void getNonExistingCarTemplate() throws Exception {
        // Get the carTemplate
        restCarTemplateMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCarTemplate() throws Exception {
        // Initialize the database
        insertedCarTemplate = carTemplateRepository.saveAndFlush(carTemplate);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        carTemplateSearchRepository.save(carTemplate);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carTemplateSearchRepository.findAll());

        // Update the carTemplate
        CarTemplate updatedCarTemplate = carTemplateRepository.findById(carTemplate.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCarTemplate are not directly saved in db
        em.detach(updatedCarTemplate);
        updatedCarTemplate.status(UPDATED_STATUS);
        CarTemplateDTO carTemplateDTO = carTemplateMapper.toDto(updatedCarTemplate);

        restCarTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carTemplateDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carTemplateDTO))
            )
            .andExpect(status().isOk());

        // Validate the CarTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCarTemplateToMatchAllProperties(updatedCarTemplate);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(carTemplateSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<CarTemplate> carTemplateSearchList = Streamable.of(carTemplateSearchRepository.findAll()).toList();
                CarTemplate testCarTemplateSearch = carTemplateSearchList.get(searchDatabaseSizeAfter - 1);

                assertCarTemplateAllPropertiesEquals(testCarTemplateSearch, updatedCarTemplate);
            });
    }

    @Test
    @Transactional
    void putNonExistingCarTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carTemplateSearchRepository.findAll());
        carTemplate.setId(longCount.incrementAndGet());

        // Create the CarTemplate
        CarTemplateDTO carTemplateDTO = carTemplateMapper.toDto(carTemplate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carTemplateDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carTemplateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCarTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carTemplateSearchRepository.findAll());
        carTemplate.setId(longCount.incrementAndGet());

        // Create the CarTemplate
        CarTemplateDTO carTemplateDTO = carTemplateMapper.toDto(carTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarTemplateMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carTemplateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCarTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carTemplateSearchRepository.findAll());
        carTemplate.setId(longCount.incrementAndGet());

        // Create the CarTemplate
        CarTemplateDTO carTemplateDTO = carTemplateMapper.toDto(carTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarTemplateMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carTemplateDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CarTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carTemplateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCarTemplateWithPatch() throws Exception {
        // Initialize the database
        insertedCarTemplate = carTemplateRepository.saveAndFlush(carTemplate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carTemplate using partial update
        CarTemplate partialUpdatedCarTemplate = new CarTemplate();
        partialUpdatedCarTemplate.setId(carTemplate.getId());

        restCarTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarTemplate.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCarTemplate))
            )
            .andExpect(status().isOk());

        // Validate the CarTemplate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCarTemplateUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCarTemplate, carTemplate),
            getPersistedCarTemplate(carTemplate)
        );
    }

    @Test
    @Transactional
    void fullUpdateCarTemplateWithPatch() throws Exception {
        // Initialize the database
        insertedCarTemplate = carTemplateRepository.saveAndFlush(carTemplate);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carTemplate using partial update
        CarTemplate partialUpdatedCarTemplate = new CarTemplate();
        partialUpdatedCarTemplate.setId(carTemplate.getId());

        partialUpdatedCarTemplate.status(UPDATED_STATUS);

        restCarTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarTemplate.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCarTemplate))
            )
            .andExpect(status().isOk());

        // Validate the CarTemplate in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCarTemplateUpdatableFieldsEquals(partialUpdatedCarTemplate, getPersistedCarTemplate(partialUpdatedCarTemplate));
    }

    @Test
    @Transactional
    void patchNonExistingCarTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carTemplateSearchRepository.findAll());
        carTemplate.setId(longCount.incrementAndGet());

        // Create the CarTemplate
        CarTemplateDTO carTemplateDTO = carTemplateMapper.toDto(carTemplate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, carTemplateDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(carTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carTemplateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCarTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carTemplateSearchRepository.findAll());
        carTemplate.setId(longCount.incrementAndGet());

        // Create the CarTemplate
        CarTemplateDTO carTemplateDTO = carTemplateMapper.toDto(carTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarTemplateMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(carTemplateDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carTemplateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCarTemplate() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carTemplateSearchRepository.findAll());
        carTemplate.setId(longCount.incrementAndGet());

        // Create the CarTemplate
        CarTemplateDTO carTemplateDTO = carTemplateMapper.toDto(carTemplate);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarTemplateMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(carTemplateDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CarTemplate in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carTemplateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCarTemplate() throws Exception {
        // Initialize the database
        insertedCarTemplate = carTemplateRepository.saveAndFlush(carTemplate);
        carTemplateRepository.save(carTemplate);
        carTemplateSearchRepository.save(carTemplate);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carTemplateSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the carTemplate
        restCarTemplateMockMvc
            .perform(delete(ENTITY_API_URL_ID, carTemplate.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carTemplateSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCarTemplate() throws Exception {
        // Initialize the database
        insertedCarTemplate = carTemplateRepository.saveAndFlush(carTemplate);
        carTemplateSearchRepository.save(carTemplate);

        // Search the carTemplate
        restCarTemplateMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + carTemplate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carTemplate.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    protected long getRepositoryCount() {
        return carTemplateRepository.count();
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

    protected CarTemplate getPersistedCarTemplate(CarTemplate carTemplate) {
        return carTemplateRepository.findById(carTemplate.getId()).orElseThrow();
    }

    protected void assertPersistedCarTemplateToMatchAllProperties(CarTemplate expectedCarTemplate) {
        assertCarTemplateAllPropertiesEquals(expectedCarTemplate, getPersistedCarTemplate(expectedCarTemplate));
    }

    protected void assertPersistedCarTemplateToMatchUpdatableProperties(CarTemplate expectedCarTemplate) {
        assertCarTemplateAllUpdatablePropertiesEquals(expectedCarTemplate, getPersistedCarTemplate(expectedCarTemplate));
    }
}
