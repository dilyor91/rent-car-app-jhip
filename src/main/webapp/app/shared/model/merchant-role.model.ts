import { IUser } from 'app/shared/model/user.model';
import { IMerchant } from 'app/shared/model/merchant.model';
import { IMerchantBranch } from 'app/shared/model/merchant-branch.model';
import { MerchantRoleEnum } from 'app/shared/model/enumerations/merchant-role-enum.model';

export interface IMerchantRole {
  id?: number;
  merchantRoleType?: keyof typeof MerchantRoleEnum | null;
  user?: IUser | null;
  merchant?: IMerchant | null;
  merchantBranch?: IMerchantBranch | null;
}

export const defaultValue: Readonly<IMerchantRole> = {};
