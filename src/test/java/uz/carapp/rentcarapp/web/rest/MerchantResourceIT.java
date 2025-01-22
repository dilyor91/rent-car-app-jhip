package uz.carapp.rentcarapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uz.carapp.rentcarapp.domain.MerchantAsserts.*;
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
import uz.carapp.rentcarapp.domain.Merchant;
import uz.carapp.rentcarapp.repository.MerchantRepository;
import uz.carapp.rentcarapp.repository.search.MerchantSearchRepository;
import uz.carapp.rentcarapp.service.dto.MerchantDTO;
import uz.carapp.rentcarapp.service.mapper.MerchantMapper;

/**
 * Integration tests for the {@link MerchantResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MerchantResourceIT {

    private static final String DEFAULT_COMPANY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_COMPANY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_BRAND_NAME = "AAAAAAAAAA";
    private static final String UPDATED_BRAND_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_INN = "AAAAAAAAAA";
    private static final String UPDATED_INN = "BBBBBBBBBB";

    private static final String DEFAULT_OWNER = "AAAAAAAAAA";
    private static final String UPDATED_OWNER = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/merchants";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/merchants/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private MerchantMapper merchantMapper;

    @Autowired
    private MerchantSearchRepository merchantSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMerchantMockMvc;

    private Merchant merchant;

    private Merchant insertedMerchant;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Merchant createEntity() {
        return new Merchant()
            .companyName(DEFAULT_COMPANY_NAME)
            .brandName(DEFAULT_BRAND_NAME)
            .inn(DEFAULT_INN)
            .owner(DEFAULT_OWNER)
            .phone(DEFAULT_PHONE)
            .address(DEFAULT_ADDRESS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Merchant createUpdatedEntity() {
        return new Merchant()
            .companyName(UPDATED_COMPANY_NAME)
            .brandName(UPDATED_BRAND_NAME)
            .inn(UPDATED_INN)
            .owner(UPDATED_OWNER)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS);
    }

    @BeforeEach
    public void initTest() {
        merchant = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedMerchant != null) {
            merchantRepository.delete(insertedMerchant);
            merchantSearchRepository.delete(insertedMerchant);
            insertedMerchant = null;
        }
    }

    @Test
    @Transactional
    void createMerchant() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantSearchRepository.findAll());
        // Create the Merchant
        MerchantDTO merchantDTO = merchantMapper.toDto(merchant);
        var returnedMerchantDTO = om.readValue(
            restMerchantMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(merchantDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MerchantDTO.class
        );

        // Validate the Merchant in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMerchant = merchantMapper.toEntity(returnedMerchantDTO);
        assertMerchantUpdatableFieldsEquals(returnedMerchant, getPersistedMerchant(returnedMerchant));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMerchant = returnedMerchant;
    }

    @Test
    @Transactional
    void createMerchantWithExistingId() throws Exception {
        // Create the Merchant with an existing ID
        merchant.setId(1L);
        MerchantDTO merchantDTO = merchantMapper.toDto(merchant);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restMerchantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(merchantDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Merchant in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void checkCompanyNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantSearchRepository.findAll());
        // set the field null
        merchant.setCompanyName(null);

        // Create the Merchant, which fails.
        MerchantDTO merchantDTO = merchantMapper.toDto(merchant);

        restMerchantMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(merchantDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);

        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllMerchants() throws Exception {
        // Initialize the database
        insertedMerchant = merchantRepository.saveAndFlush(merchant);

        // Get all the merchantList
        restMerchantMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(merchant.getId().intValue())))
            .andExpect(jsonPath("$.[*].companyName").value(hasItem(DEFAULT_COMPANY_NAME)))
            .andExpect(jsonPath("$.[*].brandName").value(hasItem(DEFAULT_BRAND_NAME)))
            .andExpect(jsonPath("$.[*].inn").value(hasItem(DEFAULT_INN)))
            .andExpect(jsonPath("$.[*].owner").value(hasItem(DEFAULT_OWNER)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)));
    }

    @Test
    @Transactional
    void getMerchant() throws Exception {
        // Initialize the database
        insertedMerchant = merchantRepository.saveAndFlush(merchant);

        // Get the merchant
        restMerchantMockMvc
            .perform(get(ENTITY_API_URL_ID, merchant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(merchant.getId().intValue()))
            .andExpect(jsonPath("$.companyName").value(DEFAULT_COMPANY_NAME))
            .andExpect(jsonPath("$.brandName").value(DEFAULT_BRAND_NAME))
            .andExpect(jsonPath("$.inn").value(DEFAULT_INN))
            .andExpect(jsonPath("$.owner").value(DEFAULT_OWNER))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.address").value(DEFAULT_ADDRESS));
    }

    @Test
    @Transactional
    void getNonExistingMerchant() throws Exception {
        // Get the merchant
        restMerchantMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMerchant() throws Exception {
        // Initialize the database
        insertedMerchant = merchantRepository.saveAndFlush(merchant);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        merchantSearchRepository.save(merchant);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantSearchRepository.findAll());

        // Update the merchant
        Merchant updatedMerchant = merchantRepository.findById(merchant.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMerchant are not directly saved in db
        em.detach(updatedMerchant);
        updatedMerchant
            .companyName(UPDATED_COMPANY_NAME)
            .brandName(UPDATED_BRAND_NAME)
            .inn(UPDATED_INN)
            .owner(UPDATED_OWNER)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS);
        MerchantDTO merchantDTO = merchantMapper.toDto(updatedMerchant);

        restMerchantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, merchantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(merchantDTO))
            )
            .andExpect(status().isOk());

        // Validate the Merchant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMerchantToMatchAllProperties(updatedMerchant);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Merchant> merchantSearchList = Streamable.of(merchantSearchRepository.findAll()).toList();
                Merchant testMerchantSearch = merchantSearchList.get(searchDatabaseSizeAfter - 1);

                assertMerchantAllPropertiesEquals(testMerchantSearch, updatedMerchant);
            });
    }

    @Test
    @Transactional
    void putNonExistingMerchant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantSearchRepository.findAll());
        merchant.setId(longCount.incrementAndGet());

        // Create the Merchant
        MerchantDTO merchantDTO = merchantMapper.toDto(merchant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMerchantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, merchantDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(merchantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Merchant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchMerchant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantSearchRepository.findAll());
        merchant.setId(longCount.incrementAndGet());

        // Create the Merchant
        MerchantDTO merchantDTO = merchantMapper.toDto(merchant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMerchantMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(merchantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Merchant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMerchant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantSearchRepository.findAll());
        merchant.setId(longCount.incrementAndGet());

        // Create the Merchant
        MerchantDTO merchantDTO = merchantMapper.toDto(merchant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMerchantMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(merchantDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Merchant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateMerchantWithPatch() throws Exception {
        // Initialize the database
        insertedMerchant = merchantRepository.saveAndFlush(merchant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the merchant using partial update
        Merchant partialUpdatedMerchant = new Merchant();
        partialUpdatedMerchant.setId(merchant.getId());

        partialUpdatedMerchant.companyName(UPDATED_COMPANY_NAME).inn(UPDATED_INN).owner(UPDATED_OWNER).phone(UPDATED_PHONE);

        restMerchantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMerchant.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMerchant))
            )
            .andExpect(status().isOk());

        // Validate the Merchant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMerchantUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedMerchant, merchant), getPersistedMerchant(merchant));
    }

    @Test
    @Transactional
    void fullUpdateMerchantWithPatch() throws Exception {
        // Initialize the database
        insertedMerchant = merchantRepository.saveAndFlush(merchant);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the merchant using partial update
        Merchant partialUpdatedMerchant = new Merchant();
        partialUpdatedMerchant.setId(merchant.getId());

        partialUpdatedMerchant
            .companyName(UPDATED_COMPANY_NAME)
            .brandName(UPDATED_BRAND_NAME)
            .inn(UPDATED_INN)
            .owner(UPDATED_OWNER)
            .phone(UPDATED_PHONE)
            .address(UPDATED_ADDRESS);

        restMerchantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMerchant.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMerchant))
            )
            .andExpect(status().isOk());

        // Validate the Merchant in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMerchantUpdatableFieldsEquals(partialUpdatedMerchant, getPersistedMerchant(partialUpdatedMerchant));
    }

    @Test
    @Transactional
    void patchNonExistingMerchant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantSearchRepository.findAll());
        merchant.setId(longCount.incrementAndGet());

        // Create the Merchant
        MerchantDTO merchantDTO = merchantMapper.toDto(merchant);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMerchantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, merchantDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(merchantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Merchant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMerchant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantSearchRepository.findAll());
        merchant.setId(longCount.incrementAndGet());

        // Create the Merchant
        MerchantDTO merchantDTO = merchantMapper.toDto(merchant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMerchantMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(merchantDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Merchant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMerchant() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantSearchRepository.findAll());
        merchant.setId(longCount.incrementAndGet());

        // Create the Merchant
        MerchantDTO merchantDTO = merchantMapper.toDto(merchant);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMerchantMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(merchantDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Merchant in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteMerchant() throws Exception {
        // Initialize the database
        insertedMerchant = merchantRepository.saveAndFlush(merchant);
        merchantRepository.save(merchant);
        merchantSearchRepository.save(merchant);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the merchant
        restMerchantMockMvc
            .perform(delete(ENTITY_API_URL_ID, merchant.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchMerchant() throws Exception {
        // Initialize the database
        insertedMerchant = merchantRepository.saveAndFlush(merchant);
        merchantSearchRepository.save(merchant);

        // Search the merchant
        restMerchantMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + merchant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(merchant.getId().intValue())))
            .andExpect(jsonPath("$.[*].companyName").value(hasItem(DEFAULT_COMPANY_NAME)))
            .andExpect(jsonPath("$.[*].brandName").value(hasItem(DEFAULT_BRAND_NAME)))
            .andExpect(jsonPath("$.[*].inn").value(hasItem(DEFAULT_INN)))
            .andExpect(jsonPath("$.[*].owner").value(hasItem(DEFAULT_OWNER)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].address").value(hasItem(DEFAULT_ADDRESS)));
    }

    protected long getRepositoryCount() {
        return merchantRepository.count();
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

    protected Merchant getPersistedMerchant(Merchant merchant) {
        return merchantRepository.findById(merchant.getId()).orElseThrow();
    }

    protected void assertPersistedMerchantToMatchAllProperties(Merchant expectedMerchant) {
        assertMerchantAllPropertiesEquals(expectedMerchant, getPersistedMerchant(expectedMerchant));
    }

    protected void assertPersistedMerchantToMatchUpdatableProperties(Merchant expectedMerchant) {
        assertMerchantAllUpdatablePropertiesEquals(expectedMerchant, getPersistedMerchant(expectedMerchant));
    }
}
