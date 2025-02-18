package uz.carapp.rentcarapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uz.carapp.rentcarapp.domain.MerchantRoleAsserts.*;
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
import uz.carapp.rentcarapp.domain.MerchantRole;
import uz.carapp.rentcarapp.domain.enumeration.MerchantRoleEnum;
import uz.carapp.rentcarapp.repository.MerchantRoleRepository;
import uz.carapp.rentcarapp.repository.UserRepository;
import uz.carapp.rentcarapp.repository.search.MerchantRoleSearchRepository;
import uz.carapp.rentcarapp.service.dto.MerchantRoleDTO;
import uz.carapp.rentcarapp.service.mapper.MerchantRoleMapper;

/**
 * Integration tests for the {@link MerchantRoleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class MerchantRoleResourceIT {

    private static final MerchantRoleEnum DEFAULT_MERCHANT_ROLE_TYPE = MerchantRoleEnum.OWNER;
    private static final MerchantRoleEnum UPDATED_MERCHANT_ROLE_TYPE = MerchantRoleEnum.MERCHANT_ADMIN;

    private static final String ENTITY_API_URL = "/api/merchant-roles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/merchant-roles/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MerchantRoleRepository merchantRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MerchantRoleMapper merchantRoleMapper;

    @Autowired
    private MerchantRoleSearchRepository merchantRoleSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMerchantRoleMockMvc;

    private MerchantRole merchantRole;

    private MerchantRole insertedMerchantRole;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MerchantRole createEntity() {
        return new MerchantRole().merchantRoleType(DEFAULT_MERCHANT_ROLE_TYPE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MerchantRole createUpdatedEntity() {
        return new MerchantRole().merchantRoleType(UPDATED_MERCHANT_ROLE_TYPE);
    }

    @BeforeEach
    public void initTest() {
        merchantRole = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedMerchantRole != null) {
            merchantRoleRepository.delete(insertedMerchantRole);
            merchantRoleSearchRepository.delete(insertedMerchantRole);
            insertedMerchantRole = null;
        }
    }

    @Test
    @Transactional
    void createMerchantRole() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantRoleSearchRepository.findAll());
        // Create the MerchantRole
        MerchantRoleDTO merchantRoleDTO = merchantRoleMapper.toDto(merchantRole);
        var returnedMerchantRoleDTO = om.readValue(
            restMerchantRoleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(merchantRoleDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MerchantRoleDTO.class
        );

        // Validate the MerchantRole in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMerchantRole = merchantRoleMapper.toEntity(returnedMerchantRoleDTO);
        assertMerchantRoleUpdatableFieldsEquals(returnedMerchantRole, getPersistedMerchantRole(returnedMerchantRole));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantRoleSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedMerchantRole = returnedMerchantRole;
    }

    @Test
    @Transactional
    void createMerchantRoleWithExistingId() throws Exception {
        // Create the MerchantRole with an existing ID
        merchantRole.setId(1L);
        MerchantRoleDTO merchantRoleDTO = merchantRoleMapper.toDto(merchantRole);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantRoleSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restMerchantRoleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(merchantRoleDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MerchantRole in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantRoleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllMerchantRoles() throws Exception {
        // Initialize the database
        insertedMerchantRole = merchantRoleRepository.saveAndFlush(merchantRole);

        // Get all the merchantRoleList
        restMerchantRoleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(merchantRole.getId().intValue())))
            .andExpect(jsonPath("$.[*].merchantRoleType").value(hasItem(DEFAULT_MERCHANT_ROLE_TYPE.toString())));
    }

    @Test
    @Transactional
    void getMerchantRole() throws Exception {
        // Initialize the database
        insertedMerchantRole = merchantRoleRepository.saveAndFlush(merchantRole);

        // Get the merchantRole
        restMerchantRoleMockMvc
            .perform(get(ENTITY_API_URL_ID, merchantRole.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(merchantRole.getId().intValue()))
            .andExpect(jsonPath("$.merchantRoleType").value(DEFAULT_MERCHANT_ROLE_TYPE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingMerchantRole() throws Exception {
        // Get the merchantRole
        restMerchantRoleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMerchantRole() throws Exception {
        // Initialize the database
        insertedMerchantRole = merchantRoleRepository.saveAndFlush(merchantRole);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        merchantRoleSearchRepository.save(merchantRole);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantRoleSearchRepository.findAll());

        // Update the merchantRole
        MerchantRole updatedMerchantRole = merchantRoleRepository.findById(merchantRole.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMerchantRole are not directly saved in db
        em.detach(updatedMerchantRole);
        updatedMerchantRole.merchantRoleType(UPDATED_MERCHANT_ROLE_TYPE);
        MerchantRoleDTO merchantRoleDTO = merchantRoleMapper.toDto(updatedMerchantRole);

        restMerchantRoleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, merchantRoleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(merchantRoleDTO))
            )
            .andExpect(status().isOk());

        // Validate the MerchantRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMerchantRoleToMatchAllProperties(updatedMerchantRole);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantRoleSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<MerchantRole> merchantRoleSearchList = Streamable.of(merchantRoleSearchRepository.findAll()).toList();
                MerchantRole testMerchantRoleSearch = merchantRoleSearchList.get(searchDatabaseSizeAfter - 1);

                assertMerchantRoleAllPropertiesEquals(testMerchantRoleSearch, updatedMerchantRole);
            });
    }

    @Test
    @Transactional
    void putNonExistingMerchantRole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantRoleSearchRepository.findAll());
        merchantRole.setId(longCount.incrementAndGet());

        // Create the MerchantRole
        MerchantRoleDTO merchantRoleDTO = merchantRoleMapper.toDto(merchantRole);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMerchantRoleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, merchantRoleDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(merchantRoleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MerchantRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantRoleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchMerchantRole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantRoleSearchRepository.findAll());
        merchantRole.setId(longCount.incrementAndGet());

        // Create the MerchantRole
        MerchantRoleDTO merchantRoleDTO = merchantRoleMapper.toDto(merchantRole);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMerchantRoleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(merchantRoleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MerchantRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantRoleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMerchantRole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantRoleSearchRepository.findAll());
        merchantRole.setId(longCount.incrementAndGet());

        // Create the MerchantRole
        MerchantRoleDTO merchantRoleDTO = merchantRoleMapper.toDto(merchantRole);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMerchantRoleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(merchantRoleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MerchantRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantRoleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateMerchantRoleWithPatch() throws Exception {
        // Initialize the database
        insertedMerchantRole = merchantRoleRepository.saveAndFlush(merchantRole);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the merchantRole using partial update
        MerchantRole partialUpdatedMerchantRole = new MerchantRole();
        partialUpdatedMerchantRole.setId(merchantRole.getId());

        partialUpdatedMerchantRole.merchantRoleType(UPDATED_MERCHANT_ROLE_TYPE);

        restMerchantRoleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMerchantRole.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMerchantRole))
            )
            .andExpect(status().isOk());

        // Validate the MerchantRole in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMerchantRoleUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMerchantRole, merchantRole),
            getPersistedMerchantRole(merchantRole)
        );
    }

    @Test
    @Transactional
    void fullUpdateMerchantRoleWithPatch() throws Exception {
        // Initialize the database
        insertedMerchantRole = merchantRoleRepository.saveAndFlush(merchantRole);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the merchantRole using partial update
        MerchantRole partialUpdatedMerchantRole = new MerchantRole();
        partialUpdatedMerchantRole.setId(merchantRole.getId());

        partialUpdatedMerchantRole.merchantRoleType(UPDATED_MERCHANT_ROLE_TYPE);

        restMerchantRoleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMerchantRole.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMerchantRole))
            )
            .andExpect(status().isOk());

        // Validate the MerchantRole in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMerchantRoleUpdatableFieldsEquals(partialUpdatedMerchantRole, getPersistedMerchantRole(partialUpdatedMerchantRole));
    }

    @Test
    @Transactional
    void patchNonExistingMerchantRole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantRoleSearchRepository.findAll());
        merchantRole.setId(longCount.incrementAndGet());

        // Create the MerchantRole
        MerchantRoleDTO merchantRoleDTO = merchantRoleMapper.toDto(merchantRole);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMerchantRoleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, merchantRoleDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(merchantRoleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MerchantRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantRoleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMerchantRole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantRoleSearchRepository.findAll());
        merchantRole.setId(longCount.incrementAndGet());

        // Create the MerchantRole
        MerchantRoleDTO merchantRoleDTO = merchantRoleMapper.toDto(merchantRole);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMerchantRoleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(merchantRoleDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MerchantRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantRoleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMerchantRole() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantRoleSearchRepository.findAll());
        merchantRole.setId(longCount.incrementAndGet());

        // Create the MerchantRole
        MerchantRoleDTO merchantRoleDTO = merchantRoleMapper.toDto(merchantRole);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMerchantRoleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(merchantRoleDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MerchantRole in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantRoleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteMerchantRole() throws Exception {
        // Initialize the database
        insertedMerchantRole = merchantRoleRepository.saveAndFlush(merchantRole);
        merchantRoleRepository.save(merchantRole);
        merchantRoleSearchRepository.save(merchantRole);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(merchantRoleSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the merchantRole
        restMerchantRoleMockMvc
            .perform(delete(ENTITY_API_URL_ID, merchantRole.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(merchantRoleSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchMerchantRole() throws Exception {
        // Initialize the database
        insertedMerchantRole = merchantRoleRepository.saveAndFlush(merchantRole);
        merchantRoleSearchRepository.save(merchantRole);

        // Search the merchantRole
        restMerchantRoleMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + merchantRole.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(merchantRole.getId().intValue())))
            .andExpect(jsonPath("$.[*].merchantRoleType").value(hasItem(DEFAULT_MERCHANT_ROLE_TYPE.toString())));
    }

    protected long getRepositoryCount() {
        return merchantRoleRepository.count();
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

    protected MerchantRole getPersistedMerchantRole(MerchantRole merchantRole) {
        return merchantRoleRepository.findById(merchantRole.getId()).orElseThrow();
    }

    protected void assertPersistedMerchantRoleToMatchAllProperties(MerchantRole expectedMerchantRole) {
        assertMerchantRoleAllPropertiesEquals(expectedMerchantRole, getPersistedMerchantRole(expectedMerchantRole));
    }

    protected void assertPersistedMerchantRoleToMatchUpdatableProperties(MerchantRole expectedMerchantRole) {
        assertMerchantRoleAllUpdatablePropertiesEquals(expectedMerchantRole, getPersistedMerchantRole(expectedMerchantRole));
    }
}
