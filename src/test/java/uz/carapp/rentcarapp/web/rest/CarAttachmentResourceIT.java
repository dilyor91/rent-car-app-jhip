package uz.carapp.rentcarapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uz.carapp.rentcarapp.domain.CarAttachmentAsserts.*;
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
import uz.carapp.rentcarapp.domain.CarAttachment;
import uz.carapp.rentcarapp.repository.CarAttachmentRepository;
import uz.carapp.rentcarapp.repository.search.CarAttachmentSearchRepository;
import uz.carapp.rentcarapp.service.dto.CarAttachmentDTO;
import uz.carapp.rentcarapp.service.mapper.CarAttachmentMapper;

/**
 * Integration tests for the {@link CarAttachmentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CarAttachmentResourceIT {

    private static final Boolean DEFAULT_IS_MAIN = false;
    private static final Boolean UPDATED_IS_MAIN = true;

    private static final String ENTITY_API_URL = "/api/car-attachments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/car-attachments/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CarAttachmentRepository carAttachmentRepository;

    @Autowired
    private CarAttachmentMapper carAttachmentMapper;

    @Autowired
    private CarAttachmentSearchRepository carAttachmentSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCarAttachmentMockMvc;

    private CarAttachment carAttachment;

    private CarAttachment insertedCarAttachment;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CarAttachment createEntity() {
        return new CarAttachment().isMain(DEFAULT_IS_MAIN);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CarAttachment createUpdatedEntity() {
        return new CarAttachment().isMain(UPDATED_IS_MAIN);
    }

    @BeforeEach
    public void initTest() {
        carAttachment = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedCarAttachment != null) {
            carAttachmentRepository.delete(insertedCarAttachment);
            carAttachmentSearchRepository.delete(insertedCarAttachment);
            insertedCarAttachment = null;
        }
    }

    @Test
    @Transactional
    void createCarAttachment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carAttachmentSearchRepository.findAll());
        // Create the CarAttachment
        CarAttachmentDTO carAttachmentDTO = carAttachmentMapper.toDto(carAttachment);
        var returnedCarAttachmentDTO = om.readValue(
            restCarAttachmentMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carAttachmentDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CarAttachmentDTO.class
        );

        // Validate the CarAttachment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCarAttachment = carAttachmentMapper.toEntity(returnedCarAttachmentDTO);
        assertCarAttachmentUpdatableFieldsEquals(returnedCarAttachment, getPersistedCarAttachment(returnedCarAttachment));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(carAttachmentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedCarAttachment = returnedCarAttachment;
    }

    @Test
    @Transactional
    void createCarAttachmentWithExistingId() throws Exception {
        // Create the CarAttachment with an existing ID
        carAttachment.setId(1L);
        CarAttachmentDTO carAttachmentDTO = carAttachmentMapper.toDto(carAttachment);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carAttachmentSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCarAttachmentMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carAttachmentDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CarAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllCarAttachments() throws Exception {
        // Initialize the database
        insertedCarAttachment = carAttachmentRepository.saveAndFlush(carAttachment);

        // Get all the carAttachmentList
        restCarAttachmentMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carAttachment.getId().intValue())))
            .andExpect(jsonPath("$.[*].isMain").value(hasItem(DEFAULT_IS_MAIN)));
    }

    @Test
    @Transactional
    void getCarAttachment() throws Exception {
        // Initialize the database
        insertedCarAttachment = carAttachmentRepository.saveAndFlush(carAttachment);

        // Get the carAttachment
        restCarAttachmentMockMvc
            .perform(get(ENTITY_API_URL_ID, carAttachment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(carAttachment.getId().intValue()))
            .andExpect(jsonPath("$.isMain").value(DEFAULT_IS_MAIN));
    }

    @Test
    @Transactional
    void getNonExistingCarAttachment() throws Exception {
        // Get the carAttachment
        restCarAttachmentMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCarAttachment() throws Exception {
        // Initialize the database
        insertedCarAttachment = carAttachmentRepository.saveAndFlush(carAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        carAttachmentSearchRepository.save(carAttachment);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carAttachmentSearchRepository.findAll());

        // Update the carAttachment
        CarAttachment updatedCarAttachment = carAttachmentRepository.findById(carAttachment.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCarAttachment are not directly saved in db
        em.detach(updatedCarAttachment);
        updatedCarAttachment.isMain(UPDATED_IS_MAIN);
        CarAttachmentDTO carAttachmentDTO = carAttachmentMapper.toDto(updatedCarAttachment);

        restCarAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carAttachmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carAttachmentDTO))
            )
            .andExpect(status().isOk());

        // Validate the CarAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCarAttachmentToMatchAllProperties(updatedCarAttachment);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(carAttachmentSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<CarAttachment> carAttachmentSearchList = Streamable.of(carAttachmentSearchRepository.findAll()).toList();
                CarAttachment testCarAttachmentSearch = carAttachmentSearchList.get(searchDatabaseSizeAfter - 1);

                assertCarAttachmentAllPropertiesEquals(testCarAttachmentSearch, updatedCarAttachment);
            });
    }

    @Test
    @Transactional
    void putNonExistingCarAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carAttachmentSearchRepository.findAll());
        carAttachment.setId(longCount.incrementAndGet());

        // Create the CarAttachment
        CarAttachmentDTO carAttachmentDTO = carAttachmentMapper.toDto(carAttachment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, carAttachmentDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carAttachmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchCarAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carAttachmentSearchRepository.findAll());
        carAttachment.setId(longCount.incrementAndGet());

        // Create the CarAttachment
        CarAttachmentDTO carAttachmentDTO = carAttachmentMapper.toDto(carAttachment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarAttachmentMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(carAttachmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCarAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carAttachmentSearchRepository.findAll());
        carAttachment.setId(longCount.incrementAndGet());

        // Create the CarAttachment
        CarAttachmentDTO carAttachmentDTO = carAttachmentMapper.toDto(carAttachment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarAttachmentMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(carAttachmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CarAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateCarAttachmentWithPatch() throws Exception {
        // Initialize the database
        insertedCarAttachment = carAttachmentRepository.saveAndFlush(carAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carAttachment using partial update
        CarAttachment partialUpdatedCarAttachment = new CarAttachment();
        partialUpdatedCarAttachment.setId(carAttachment.getId());

        restCarAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarAttachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCarAttachment))
            )
            .andExpect(status().isOk());

        // Validate the CarAttachment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCarAttachmentUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedCarAttachment, carAttachment),
            getPersistedCarAttachment(carAttachment)
        );
    }

    @Test
    @Transactional
    void fullUpdateCarAttachmentWithPatch() throws Exception {
        // Initialize the database
        insertedCarAttachment = carAttachmentRepository.saveAndFlush(carAttachment);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the carAttachment using partial update
        CarAttachment partialUpdatedCarAttachment = new CarAttachment();
        partialUpdatedCarAttachment.setId(carAttachment.getId());

        partialUpdatedCarAttachment.isMain(UPDATED_IS_MAIN);

        restCarAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCarAttachment.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCarAttachment))
            )
            .andExpect(status().isOk());

        // Validate the CarAttachment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCarAttachmentUpdatableFieldsEquals(partialUpdatedCarAttachment, getPersistedCarAttachment(partialUpdatedCarAttachment));
    }

    @Test
    @Transactional
    void patchNonExistingCarAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carAttachmentSearchRepository.findAll());
        carAttachment.setId(longCount.incrementAndGet());

        // Create the CarAttachment
        CarAttachmentDTO carAttachmentDTO = carAttachmentMapper.toDto(carAttachment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCarAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, carAttachmentDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(carAttachmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCarAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carAttachmentSearchRepository.findAll());
        carAttachment.setId(longCount.incrementAndGet());

        // Create the CarAttachment
        CarAttachmentDTO carAttachmentDTO = carAttachmentMapper.toDto(carAttachment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarAttachmentMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(carAttachmentDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CarAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCarAttachment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carAttachmentSearchRepository.findAll());
        carAttachment.setId(longCount.incrementAndGet());

        // Create the CarAttachment
        CarAttachmentDTO carAttachmentDTO = carAttachmentMapper.toDto(carAttachment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCarAttachmentMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(carAttachmentDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CarAttachment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteCarAttachment() throws Exception {
        // Initialize the database
        insertedCarAttachment = carAttachmentRepository.saveAndFlush(carAttachment);
        carAttachmentRepository.save(carAttachment);
        carAttachmentSearchRepository.save(carAttachment);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(carAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the carAttachment
        restCarAttachmentMockMvc
            .perform(delete(ENTITY_API_URL_ID, carAttachment.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(carAttachmentSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchCarAttachment() throws Exception {
        // Initialize the database
        insertedCarAttachment = carAttachmentRepository.saveAndFlush(carAttachment);
        carAttachmentSearchRepository.save(carAttachment);

        // Search the carAttachment
        restCarAttachmentMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + carAttachment.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(carAttachment.getId().intValue())))
            .andExpect(jsonPath("$.[*].isMain").value(hasItem(DEFAULT_IS_MAIN)));
    }

    protected long getRepositoryCount() {
        return carAttachmentRepository.count();
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

    protected CarAttachment getPersistedCarAttachment(CarAttachment carAttachment) {
        return carAttachmentRepository.findById(carAttachment.getId()).orElseThrow();
    }

    protected void assertPersistedCarAttachmentToMatchAllProperties(CarAttachment expectedCarAttachment) {
        assertCarAttachmentAllPropertiesEquals(expectedCarAttachment, getPersistedCarAttachment(expectedCarAttachment));
    }

    protected void assertPersistedCarAttachmentToMatchUpdatableProperties(CarAttachment expectedCarAttachment) {
        assertCarAttachmentAllUpdatablePropertiesEquals(expectedCarAttachment, getPersistedCarAttachment(expectedCarAttachment));
    }
}
