package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class MerchantRoleAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMerchantRoleAllPropertiesEquals(MerchantRole expected, MerchantRole actual) {
        assertMerchantRoleAutoGeneratedPropertiesEquals(expected, actual);
        assertMerchantRoleAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMerchantRoleAllUpdatablePropertiesEquals(MerchantRole expected, MerchantRole actual) {
        assertMerchantRoleUpdatableFieldsEquals(expected, actual);
        assertMerchantRoleUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMerchantRoleAutoGeneratedPropertiesEquals(MerchantRole expected, MerchantRole actual) {
        assertThat(actual)
            .as("Verify MerchantRole auto generated properties")
            .satisfies(a -> assertThat(a.getId()).as("check id").isEqualTo(expected.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMerchantRoleUpdatableFieldsEquals(MerchantRole expected, MerchantRole actual) {
        assertThat(actual)
            .as("Verify MerchantRole relevant properties")
            .satisfies(a -> assertThat(a.getMerchantRoleType()).as("check merchantRoleType").isEqualTo(expected.getMerchantRoleType()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMerchantRoleUpdatableRelationshipsEquals(MerchantRole expected, MerchantRole actual) {
        assertThat(actual)
            .as("Verify MerchantRole relationships")
            .satisfies(a -> assertThat(a.getMerchant()).as("check merchant").isEqualTo(expected.getMerchant()))
            .satisfies(a -> assertThat(a.getMerchantBranch()).as("check merchantBranch").isEqualTo(expected.getMerchantBranch()));
    }
}
