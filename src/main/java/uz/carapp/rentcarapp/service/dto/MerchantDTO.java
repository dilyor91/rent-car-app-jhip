package uz.carapp.rentcarapp.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link uz.carapp.rentcarapp.domain.Merchant} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MerchantDTO implements Serializable {

    private Long id;

    @NotNull
    private String companyName;

    private String brandName;

    private String inn;

    private String owner;

    private String phone;

    private String address;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MerchantDTO)) {
            return false;
        }

        MerchantDTO merchantDTO = (MerchantDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, merchantDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MerchantDTO{" +
            "id=" + getId() +
            ", companyName='" + getCompanyName() + "'" +
            ", brandName='" + getBrandName() + "'" +
            ", inn='" + getInn() + "'" +
            ", owner='" + getOwner() + "'" +
            ", phone='" + getPhone() + "'" +
            ", address='" + getAddress() + "'" +
            "}";
    }
}
