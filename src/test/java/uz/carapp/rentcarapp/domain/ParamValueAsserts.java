package uz.carapp.rentcarapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class ParamValueAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertParamValueAllPropertiesEquals(ParamValue expected, ParamValue actual) {
        assertParamValueAutoGeneratedPropertiesEquals(expected, actual);
        assertParamValueAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertParamValueAllUpdatablePropertiesEquals(ParamValue expected, ParamValue actual) {
        assertParamValueUpdatableFieldsEquals(expected, actual);
        assertParamValueUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertParamValueAutoGeneratedPropertiesEquals(ParamValue expected, ParamValue actual) {
        assertThat(actual)
            .as("Verify ParamValue auto generated properties")
            .satisfies(a -> assertThat(a.getId()).as("check id").isEqualTo(expected.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertParamValueUpdatableFieldsEquals(ParamValue expected, ParamValue actual) {
        assertThat(actual)
            .as("Verify ParamValue relevant properties")
            .satisfies(a -> assertThat(a.getName()).as("check name").isEqualTo(expected.getName()))
            .satisfies(a -> assertThat(a.getStatus()).as("check status").isEqualTo(expected.getStatus()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertParamValueUpdatableRelationshipsEquals(ParamValue expected, ParamValue actual) {
        assertThat(actual)
            .as("Verify ParamValue relationships")
            .satisfies(a -> assertThat(a.getParam()).as("check param").isEqualTo(expected.getParam()));
    }
}
