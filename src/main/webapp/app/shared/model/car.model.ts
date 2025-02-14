import { IModel } from 'app/shared/model/model.model';
import { IMerchant } from 'app/shared/model/merchant.model';
import { IMerchantBranch } from 'app/shared/model/merchant-branch.model';

export interface ICar {
  id?: number;
  stateNumberPlate?: number | null;
  deposit?: number | null;
  model?: IModel | null;
  merchant?: IMerchant | null;
  merchantBranch?: IMerchantBranch | null;
}

export const defaultValue: Readonly<ICar> = {};
