package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class MerchantDocumentAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMerchantDocumentAllPropertiesEquals(MerchantDocument expected, MerchantDocument actual) {
        assertMerchantDocumentAutoGeneratedPropertiesEquals(expected, actual);
        assertMerchantDocumentAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMerchantDocumentAllUpdatablePropertiesEquals(MerchantDocument expected, MerchantDocument actual) {
        assertMerchantDocumentUpdatableFieldsEquals(expected, actual);
        assertMerchantDocumentUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMerchantDocumentAutoGeneratedPropertiesEquals(MerchantDocument expected, MerchantDocument actual) {
        assertThat(actual)
            .as("Verify MerchantDocument auto generated properties")
            .satisfies(a -> assertThat(a.getId()).as("check id").isEqualTo(expected.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMerchantDocumentUpdatableFieldsEquals(MerchantDocument expected, MerchantDocument actual) {
        // empty method

    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMerchantDocumentUpdatableRelationshipsEquals(MerchantDocument expected, MerchantDocument actual) {
        assertThat(actual)
            .as("Verify MerchantDocument relationships")
            .satisfies(a -> assertThat(a.getMerchant()).as("check merchant").isEqualTo(expected.getMerchant()))
            .satisfies(a -> assertThat(a.getDocument()).as("check document").isEqualTo(expected.getDocument()));
    }
}
