package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class MerchantBranchAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMerchantBranchAllPropertiesEquals(MerchantBranch expected, MerchantBranch actual) {
        assertMerchantBranchAutoGeneratedPropertiesEquals(expected, actual);
        assertMerchantBranchAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMerchantBranchAllUpdatablePropertiesEquals(MerchantBranch expected, MerchantBranch actual) {
        assertMerchantBranchUpdatableFieldsEquals(expected, actual);
        assertMerchantBranchUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMerchantBranchAutoGeneratedPropertiesEquals(MerchantBranch expected, MerchantBranch actual) {
        assertThat(actual)
            .as("Verify MerchantBranch auto generated properties")
            .satisfies(a -> assertThat(a.getId()).as("check id").isEqualTo(expected.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMerchantBranchUpdatableFieldsEquals(MerchantBranch expected, MerchantBranch actual) {
        assertThat(actual)
            .as("Verify MerchantBranch relevant properties")
            .satisfies(a -> assertThat(a.getName()).as("check name").isEqualTo(expected.getName()))
            .satisfies(a -> assertThat(a.getAddress()).as("check address").isEqualTo(expected.getAddress()))
            .satisfies(a -> assertThat(a.getLatitude()).as("check latitude").isEqualTo(expected.getLatitude()))
            .satisfies(a -> assertThat(a.getLongitude()).as("check longitude").isEqualTo(expected.getLongitude()))
            .satisfies(a -> assertThat(a.getPhone()).as("check phone").isEqualTo(expected.getPhone()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertMerchantBranchUpdatableRelationshipsEquals(MerchantBranch expected, MerchantBranch actual) {
        assertThat(actual)
            .as("Verify MerchantBranch relationships")
            .satisfies(a -> assertThat(a.getMerchant()).as("check merchant").isEqualTo(expected.getMerchant()));
    }
}
