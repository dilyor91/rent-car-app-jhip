package uz.carapp.rentcarapp.service.dto;

import java.io.Serializable;
import java.util.Objects;
import uz.carapp.rentcarapp.domain.enumeration.MerchantRoleEnum;

/**
 * A DTO for the {@link uz.carapp.rentcarapp.domain.MerchantRole} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MerchantRoleDTO implements Serializable {

    private Long id;

    private MerchantRoleEnum merchantRoleType;

    private UserDTO user;

    private MerchantDTO merchant;

    private MerchantBranchDTO merchantBranch;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MerchantRoleEnum getMerchantRoleType() {
        return merchantRoleType;
    }

    public void setMerchantRoleType(MerchantRoleEnum merchantRoleType) {
        this.merchantRoleType = merchantRoleType;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
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
        if (!(o instanceof MerchantRoleDTO)) {
            return false;
        }

        MerchantRoleDTO merchantRoleDTO = (MerchantRoleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, merchantRoleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MerchantRoleDTO{" +
            "id=" + getId() +
            ", merchantRoleType='" + getMerchantRoleType() + "'" +
            ", user=" + getUser() +
            ", merchant=" + getMerchant() +
            ", merchantBranch=" + getMerchantBranch() +
            "}";
    }
}
