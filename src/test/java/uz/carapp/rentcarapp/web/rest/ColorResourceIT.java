package uz.carapp.rentcarapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uz.carapp.rentcarapp.domain.ColorAsserts.*;
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
import uz.carapp.rentcarapp.domain.Color;
import uz.carapp.rentcarapp.repository.ColorRepository;
import uz.carapp.rentcarapp.repository.search.ColorSearchRepository;
import uz.carapp.rentcarapp.service.dto.ColorDTO;
import uz.carapp.rentcarapp.service.mapper.ColorMapper;

/**
 * Integration tests for the {@link ColorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ColorResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_HEX = "AAAAAAAAAA";
    private static final String UPDATED_HEX = "BBBBBBBBBB";

    private static final Boolean DEFAULT_STATUS = false;
    private static final Boolean UPDATED_STATUS = true;

    private static final String ENTITY_API_URL = "/api/colors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/colors/_search";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private ColorMapper colorMapper;

    @Autowired
    private ColorSearchRepository colorSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restColorMockMvc;

    private Color color;

    private Color insertedColor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Color createEntity() {
        return new Color().name(DEFAULT_NAME).hex(DEFAULT_HEX).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Color createUpdatedEntity() {
        return new Color().name(UPDATED_NAME).hex(UPDATED_HEX).status(UPDATED_STATUS);
    }

    @BeforeEach
    public void initTest() {
        color = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedColor != null) {
            colorRepository.delete(insertedColor);
            colorSearchRepository.delete(insertedColor);
            insertedColor = null;
        }
    }

    @Test
    @Transactional
    void createColor() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(colorSearchRepository.findAll());
        // Create the Color
        ColorDTO colorDTO = colorMapper.toDto(color);
        var returnedColorDTO = om.readValue(
            restColorMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(colorDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ColorDTO.class
        );

        // Validate the Color in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedColor = colorMapper.toEntity(returnedColorDTO);
        assertColorUpdatableFieldsEquals(returnedColor, getPersistedColor(returnedColor));

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(colorSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });

        insertedColor = returnedColor;
    }

    @Test
    @Transactional
    void createColorWithExistingId() throws Exception {
        // Create the Color with an existing ID
        color.setId(1L);
        ColorDTO colorDTO = colorMapper.toDto(color);

        long databaseSizeBeforeCreate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(colorSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restColorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(colorDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Color in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(colorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void getAllColors() throws Exception {
        // Initialize the database
        insertedColor = colorRepository.saveAndFlush(color);

        // Get all the colorList
        restColorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(color.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].hex").value(hasItem(DEFAULT_HEX)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @Test
    @Transactional
    void getColor() throws Exception {
        // Initialize the database
        insertedColor = colorRepository.saveAndFlush(color);

        // Get the color
        restColorMockMvc
            .perform(get(ENTITY_API_URL_ID, color.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(color.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.hex").value(DEFAULT_HEX))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    void getNonExistingColor() throws Exception {
        // Get the color
        restColorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingColor() throws Exception {
        // Initialize the database
        insertedColor = colorRepository.saveAndFlush(color);

        long databaseSizeBeforeUpdate = getRepositoryCount();
        colorSearchRepository.save(color);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(colorSearchRepository.findAll());

        // Update the color
        Color updatedColor = colorRepository.findById(color.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedColor are not directly saved in db
        em.detach(updatedColor);
        updatedColor.name(UPDATED_NAME).hex(UPDATED_HEX).status(UPDATED_STATUS);
        ColorDTO colorDTO = colorMapper.toDto(updatedColor);

        restColorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, colorDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(colorDTO))
            )
            .andExpect(status().isOk());

        // Validate the Color in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedColorToMatchAllProperties(updatedColor);

        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(colorSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Color> colorSearchList = Streamable.of(colorSearchRepository.findAll()).toList();
                Color testColorSearch = colorSearchList.get(searchDatabaseSizeAfter - 1);

                assertColorAllPropertiesEquals(testColorSearch, updatedColor);
            });
    }

    @Test
    @Transactional
    void putNonExistingColor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(colorSearchRepository.findAll());
        color.setId(longCount.incrementAndGet());

        // Create the Color
        ColorDTO colorDTO = colorMapper.toDto(color);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restColorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, colorDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(colorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Color in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(colorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithIdMismatchColor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(colorSearchRepository.findAll());
        color.setId(longCount.incrementAndGet());

        // Create the Color
        ColorDTO colorDTO = colorMapper.toDto(color);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restColorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(colorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Color in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(colorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamColor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(colorSearchRepository.findAll());
        color.setId(longCount.incrementAndGet());

        // Create the Color
        ColorDTO colorDTO = colorMapper.toDto(color);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restColorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(colorDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Color in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(colorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void partialUpdateColorWithPatch() throws Exception {
        // Initialize the database
        insertedColor = colorRepository.saveAndFlush(color);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the color using partial update
        Color partialUpdatedColor = new Color();
        partialUpdatedColor.setId(color.getId());

        partialUpdatedColor.hex(UPDATED_HEX);

        restColorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedColor.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedColor))
            )
            .andExpect(status().isOk());

        // Validate the Color in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertColorUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedColor, color), getPersistedColor(color));
    }

    @Test
    @Transactional
    void fullUpdateColorWithPatch() throws Exception {
        // Initialize the database
        insertedColor = colorRepository.saveAndFlush(color);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the color using partial update
        Color partialUpdatedColor = new Color();
        partialUpdatedColor.setId(color.getId());

        partialUpdatedColor.name(UPDATED_NAME).hex(UPDATED_HEX).status(UPDATED_STATUS);

        restColorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedColor.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedColor))
            )
            .andExpect(status().isOk());

        // Validate the Color in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertColorUpdatableFieldsEquals(partialUpdatedColor, getPersistedColor(partialUpdatedColor));
    }

    @Test
    @Transactional
    void patchNonExistingColor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(colorSearchRepository.findAll());
        color.setId(longCount.incrementAndGet());

        // Create the Color
        ColorDTO colorDTO = colorMapper.toDto(color);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restColorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, colorDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(colorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Color in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(colorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithIdMismatchColor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(colorSearchRepository.findAll());
        color.setId(longCount.incrementAndGet());

        // Create the Color
        ColorDTO colorDTO = colorMapper.toDto(color);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restColorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(colorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Color in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(colorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamColor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(colorSearchRepository.findAll());
        color.setId(longCount.incrementAndGet());

        // Create the Color
        ColorDTO colorDTO = colorMapper.toDto(color);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restColorMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(colorDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Color in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(colorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    @Transactional
    void deleteColor() throws Exception {
        // Initialize the database
        insertedColor = colorRepository.saveAndFlush(color);
        colorRepository.save(color);
        colorSearchRepository.save(color);

        long databaseSizeBeforeDelete = getRepositoryCount();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(colorSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the color
        restColorMockMvc
            .perform(delete(ENTITY_API_URL_ID, color.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(colorSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    @Transactional
    void searchColor() throws Exception {
        // Initialize the database
        insertedColor = colorRepository.saveAndFlush(color);
        colorSearchRepository.save(color);

        // Search the color
        restColorMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + color.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(color.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].hex").value(hasItem(DEFAULT_HEX)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    protected long getRepositoryCount() {
        return colorRepository.count();
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

    protected Color getPersistedColor(Color color) {
        return colorRepository.findById(color.getId()).orElseThrow();
    }

    protected void assertPersistedColorToMatchAllProperties(Color expectedColor) {
        assertColorAllPropertiesEquals(expectedColor, getPersistedColor(expectedColor));
    }

    protected void assertPersistedColorToMatchUpdatableProperties(Color expectedColor) {
        assertColorAllUpdatablePropertiesEquals(expectedColor, getPersistedColor(expectedColor));
    }
}
