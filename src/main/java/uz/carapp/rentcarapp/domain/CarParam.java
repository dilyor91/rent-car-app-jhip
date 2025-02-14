package uz.carapp.rentcarapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A CarParam.
 */
@Entity
@Table(name = "car_param")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "carparam")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CarParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "param_item_value")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String paramItemValue;

    @Column(name = "param_value")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String paramValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "model", "merchant", "merchantBranch" }, allowSetters = true)
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY)
    private Param param;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CarParam id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParamItemValue() {
        return this.paramItemValue;
    }

    public CarParam paramItemValue(String paramItemValue) {
        this.setParamItemValue(paramItemValue);
        return this;
    }

    public void setParamItemValue(String paramItemValue) {
        this.paramItemValue = paramItemValue;
    }

    public String getParamValue() {
        return this.paramValue;
    }

    public CarParam paramValue(String paramValue) {
        this.setParamValue(paramValue);
        return this;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public Car getCar() {
        return this.car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public CarParam car(Car car) {
        this.setCar(car);
        return this;
    }

    public Param getParam() {
        return this.param;
    }

    public void setParam(Param param) {
        this.param = param;
    }

    public CarParam param(Param param) {
        this.setParam(param);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CarParam)) {
            return false;
        }
        return getId() != null && getId().equals(((CarParam) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CarParam{" +
            "id=" + getId() +
            ", paramItemValue='" + getParamItemValue() + "'" +
            ", paramValue='" + getParamValue() + "'" +
            "}";
    }
}
