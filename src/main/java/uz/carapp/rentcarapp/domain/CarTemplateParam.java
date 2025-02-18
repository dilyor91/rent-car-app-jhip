package uz.carapp.rentcarapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A CarTemplateParam.
 */
@Entity
@Table(name = "car_template_param")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "cartemplateparam")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CarTemplateParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "param_val")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String paramVal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "model" }, allowSetters = true)
    private CarTemplate carTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Param param;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "param" }, allowSetters = true)
    private ParamValue paramValue;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public CarTemplateParam id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParamVal() {
        return this.paramVal;
    }

    public CarTemplateParam paramVal(String paramVal) {
        this.setParamVal(paramVal);
        return this;
    }

    public void setParamVal(String paramVal) {
        this.paramVal = paramVal;
    }

    public CarTemplate getCarTemplate() {
        return this.carTemplate;
    }

    public void setCarTemplate(CarTemplate carTemplate) {
        this.carTemplate = carTemplate;
    }

    public CarTemplateParam carTemplate(CarTemplate carTemplate) {
        this.setCarTemplate(carTemplate);
        return this;
    }

    public Param getParam() {
        return this.param;
    }

    public void setParam(Param param) {
        this.param = param;
    }

    public CarTemplateParam param(Param param) {
        this.setParam(param);
        return this;
    }

    public ParamValue getParamValue() {
        return this.paramValue;
    }

    public void setParamValue(ParamValue paramValue) {
        this.paramValue = paramValue;
    }

    public CarTemplateParam paramValue(ParamValue paramValue) {
        this.setParamValue(paramValue);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CarTemplateParam)) {
            return false;
        }
        return getId() != null && getId().equals(((CarTemplateParam) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CarTemplateParam{" +
            "id=" + getId() +
            ", paramVal='" + getParamVal() + "'" +
            "}";
    }
}
