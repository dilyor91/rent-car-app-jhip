import { IMerchant } from 'app/shared/model/merchant.model';

export interface IMerchantBranch {
  id?: number;
  name?: string | null;
  address?: string | null;
  latitude?: string | null;
  longitude?: string | null;
  phone?: string | null;
  merchant?: IMerchant | null;
}

export const defaultValue: Readonly<IMerchantBranch> = {};
