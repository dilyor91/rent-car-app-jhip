package uz.carapp.rentcarapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uz.carapp.rentcarapp.domain.MerchantBranchAsserts.*;
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
import uz.carapp.rentcarapp.domain.MerchantBranch;
import uz.carapp.rentcarapp.repository.MerchantBranchRepository;
import uz.carapp.rentcarapp.repository.search.MerchantBranchSearchRepository;
import uz.carapp.rentcarapp.service.dto.MerchantBranchDTO;
import uz.carapp.rentcarapp.service.mapper.MerchantBranchMapper;

/**
 * Integration tests for the {@link MerchantBranchResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MerchantBranchResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String DEFAULT_LATITUDE = "AAAAAAAAAA";
    private static final String UPDATED_LATITUDE = "BBBBBBBBBB";

    private static final String DEFAULT_LONGITUDE = "AAAAAAAAAA";
    private static final String UPDATED_LONGITUDE = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/merchant-branches";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/merchant-branches/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MerchantBranchRepository merchantBranchRepository;

    @Autowired
    private MerchantBranchMapper merchantBranchMapper;

    @Autowired
    private MerchantBranchSearchRepository merchantBranchSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMerchantBranchMockMvc;

    private MerchantBranch merchantBranch;

    private MerchantBranch insertedMerchantBranch;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MerchantBranch createEntity() {
        return new MerchantBranch()
            .name(DEFAULT_NAME)
            .address(DEFAULT_ADDRESS)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .phone(DEFAULT_PHONE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MerchantBranch createUpdatedEntity() {
        return new MerchantBranch()
            .name(UPDATED_NAME)
            .address(UPDATED_ADDRESS)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .phone(UPDATED_PHONE);
    }

    @BeforeEach
    public void initTest() {
        merchantBranch = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedMerchantBranch != null) {
            merchantBranchRepository.delete(insertedMerchantBranch);
            merchantBranchSearchRepository.delete(insertedMerchantBranch);
            insertedMerchantBranch = null;
        }
    }

    @Test
    @Transactional
    void createMerchantBranch() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantBranchSearchRepository.findAll());
        // Create the MerchantBranch
        MerchantBranchDTO merchantBranchDTO = merchantBranchMapper.toDto(merchantBranch);
        var returnedMerchantBranchDTO = om.readValue(
            restMerchantBranchMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(merchantBranchDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MerchantBranchDTO.class
        );

        // Validate the MerchantBranch in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMerchantBranch = merchantBranchMapper.toEntity(returnedMerchantBranchDTO);
        assertMerchantBranchUpdatableFieldsEquals(returnedMerchantBranch, getPersistedMerchantBranch(returnedMerchantBranch));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantBranchSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMerchantBranch = returnedMerchantBranch;
    }

    @Test
    @Transactional
    void createMerchantBranchWithExistingId() throws Exception {
        // Create the MerchantBranch with an existing ID
        merchantBranch.setId(1L);
        MerchantBranchDTO merchantBranchDTO = merchantBranchMapper.toDto(merchantBranch);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantBranchSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restMerchantBranchMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(merchantBranchDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MerchantBranch in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantBranchSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllMerchantBranches() throws Exception {
        // Initialize the database
        insertedMerchantBranch = merchantBranchRepository.saveAndFlush(merchantBranch);

        // Get all the merchantBranchList
        restMerchantBranchMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(merchantBranch.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)));
    }

    @Test
    @Transactional
    void getMerchantBranch() throws Exception {
        // Initialize the database
        insertedMerchantBranch = merchantBranchRepository.saveAndFlush(merchantBranch);

        // Get the merchantBranch
        restMerchantBranchMockMvc
            .perform(get(ENTITY_API_URL_ID, merchantBranch.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(merchantBranch.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE));
    }

    @Test
    @Transactional
    void getNonExistingMerchantBranch() throws Exception {
        // Get the merchantBranch
        restMerchantBranchMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMerchantBranch() throws Exception {
        // Initialize the database
        insertedMerchantBranch = merchantBranchRepository.saveAndFlush(merchantBranch);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        merchantBranchSearchRepository.save(merchantBranch);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantBranchSearchRepository.findAll());

        // Update the merchantBranch
        MerchantBranch updatedMerchantBranch = merchantBranchRepository.findById(merchantBranch.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMerchantBranch are not directly saved in db
        em.detach(updatedMerchantBranch);
        updatedMerchantBranch
            .name(UPDATED_NAME)
            .address(UPDATED_ADDRESS)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .phone(UPDATED_PHONE);
        MerchantBranchDTO merchantBranchDTO = merchantBranchMapper.toDto(updatedMerchantBranch);

        restMerchantBranchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, merchantBranchDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(merchantBranchDTO))
            )
            .andExpect(status().isOk());

        // Validate the MerchantBranch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMerchantBranchToMatchAllProperties(updatedMerchantBranch);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantBranchSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MerchantBranch> merchantBranchSearchList = Streamable.of(merchantBranchSearchRepository.findAll()).toList();
                MerchantBranch testMerchantBranchSearch = merchantBranchSearchList.get(searchDatabaseSizeAfter - 1);

                assertMerchantBranchAllPropertiesEquals(testMerchantBranchSearch, updatedMerchantBranch);
            });
    }

    @Test
    @Transactional
    void putNonExistingMerchantBranch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantBranchSearchRepository.findAll());
        merchantBranch.setId(longCount.incrementAndGet());

        // Create the MerchantBranch
        MerchantBranchDTO merchantBranchDTO = merchantBranchMapper.toDto(merchantBranch);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMerchantBranchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, merchantBranchDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(merchantBranchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MerchantBranch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantBranchSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchMerchantBranch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantBranchSearchRepository.findAll());
        merchantBranch.setId(longCount.incrementAndGet());

        // Create the MerchantBranch
        MerchantBranchDTO merchantBranchDTO = merchantBranchMapper.toDto(merchantBranch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMerchantBranchMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(merchantBranchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MerchantBranch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantBranchSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMerchantBranch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantBranchSearchRepository.findAll());
        merchantBranch.setId(longCount.incrementAndGet());

        // Create the MerchantBranch
        MerchantBranchDTO merchantBranchDTO = merchantBranchMapper.toDto(merchantBranch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMerchantBranchMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(merchantBranchDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MerchantBranch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantBranchSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateMerchantBranchWithPatch() throws Exception {
        // Initialize the database
        insertedMerchantBranch = merchantBranchRepository.saveAndFlush(merchantBranch);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the merchantBranch using partial update
        MerchantBranch partialUpdatedMerchantBranch = new MerchantBranch();
        partialUpdatedMerchantBranch.setId(merchantBranch.getId());

        partialUpdatedMerchantBranch.longitude(UPDATED_LONGITUDE);

        restMerchantBranchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMerchantBranch.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMerchantBranch))
            )
            .andExpect(status().isOk());

        // Validate the MerchantBranch in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMerchantBranchUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMerchantBranch, merchantBranch),
            getPersistedMerchantBranch(merchantBranch)
        );
    }

    @Test
    @Transactional
    void fullUpdateMerchantBranchWithPatch() throws Exception {
        // Initialize the database
        insertedMerchantBranch = merchantBranchRepository.saveAndFlush(merchantBranch);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the merchantBranch using partial update
        MerchantBranch partialUpdatedMerchantBranch = new MerchantBranch();
        partialUpdatedMerchantBranch.setId(merchantBranch.getId());

        partialUpdatedMerchantBranch
            .name(UPDATED_NAME)
            .address(UPDATED_ADDRESS)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .phone(UPDATED_PHONE);

        restMerchantBranchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMerchantBranch.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMerchantBranch))
            )
            .andExpect(status().isOk());

        // Validate the MerchantBranch in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMerchantBranchUpdatableFieldsEquals(partialUpdatedMerchantBranch, getPersistedMerchantBranch(partialUpdatedMerchantBranch));
    }

    @Test
    @Transactional
    void patchNonExistingMerchantBranch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantBranchSearchRepository.findAll());
        merchantBranch.setId(longCount.incrementAndGet());

        // Create the MerchantBranch
        MerchantBranchDTO merchantBranchDTO = merchantBranchMapper.toDto(merchantBranch);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMerchantBranchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, merchantBranchDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(merchantBranchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MerchantBranch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantBranchSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMerchantBranch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantBranchSearchRepository.findAll());
        merchantBranch.setId(longCount.incrementAndGet());

        // Create the MerchantBranch
        MerchantBranchDTO merchantBranchDTO = merchantBranchMapper.toDto(merchantBranch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMerchantBranchMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(merchantBranchDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MerchantBranch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantBranchSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMerchantBranch() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantBranchSearchRepository.findAll());
        merchantBranch.setId(longCount.incrementAndGet());

        // Create the MerchantBranch
        MerchantBranchDTO merchantBranchDTO = merchantBranchMapper.toDto(merchantBranch);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMerchantBranchMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(merchantBranchDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MerchantBranch in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantBranchSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteMerchantBranch() throws Exception {
        // Initialize the database
        insertedMerchantBranch = merchantBranchRepository.saveAndFlush(merchantBranch);
        merchantBranchRepository.save(merchantBranch);
        merchantBranchSearchRepository.save(merchantBranch);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantBranchSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the merchantBranch
        restMerchantBranchMockMvc
            .perform(delete(ENTITY_API_URL_ID, merchantBranch.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantBranchSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchMerchantBranch() throws Exception {
        // Initialize the database
        insertedMerchantBranch = merchantBranchRepository.saveAndFlush(merchantBranch);
        merchantBranchSearchRepository.save(merchantBranch);

        // Search the merchantBranch
        restMerchantBranchMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + merchantBranch.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(merchantBranch.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)));
    }

    protected long getRepositoryCount() {
        return merchantBranchRepository.count();
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

    protected MerchantBranch getPersistedMerchantBranch(MerchantBranch merchantBranch) {
        return merchantBranchRepository.findById(merchantBranch.getId()).orElseThrow();
    }

    protected void assertPersistedMerchantBranchToMatchAllProperties(MerchantBranch expectedMerchantBranch) {
        assertMerchantBranchAllPropertiesEquals(expectedMerchantBranch, getPersistedMerchantBranch(expectedMerchantBranch));
    }

    protected void assertPersistedMerchantBranchToMatchUpdatableProperties(MerchantBranch expectedMerchantBranch) {
        assertMerchantBranchAllUpdatablePropertiesEquals(expectedMerchantBranch, getPersistedMerchantBranch(expectedMerchantBranch));
    }
}
