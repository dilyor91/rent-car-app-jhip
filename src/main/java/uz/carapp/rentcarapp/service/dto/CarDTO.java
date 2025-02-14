package uz.carapp.rentcarapp.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link uz.carapp.rentcarapp.domain.Car} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CarDTO implements Serializable {

    private Long id;

    private Integer stateNumberPlate;

    private Integer deposit;

    private ModelDTO model;

    private MerchantDTO merchant;

    private MerchantBranchDTO merchantBranch;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStateNumberPlate() {
        return stateNumberPlate;
    }

    public void setStateNumberPlate(Integer stateNumberPlate) {
        this.stateNumberPlate = stateNumberPlate;
    }

    public Integer getDeposit() {
        return deposit;
    }

    public void setDeposit(Integer deposit) {
        this.deposit = deposit;
    }

    public ModelDTO getModel() {
        return model;
    }

    public void setModel(ModelDTO model) {
        this.model = model;
    }

    public MerchantDTO getMerchant() {
        return merchant;
    }

    public void setMerchant(MerchantDTO merchant) {
        this.merchant = merchant;
    }

    public MerchantBranchDTO getMerchantBranch() {
        return merchantBranch;
    }

    public void setMerchantBranch(MerchantBranchDTO merchantBranch) {
        this.merchantBranch = merchantBranch;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CarDTO)) {
            return false;
        }

        CarDTO carDTO = (CarDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, carDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CarDTO{" +
            "id=" + getId() +
            ", stateNumberPlate=" + getStateNumberPlate() +
            ", deposit=" + getDeposit() +
            ", model=" + getModel() +
            ", merchant=" + getMerchant() +
            ", merchantBranch=" + getMerchantBranch() +
            "}";
    }
}
