import { IMerchant } from 'app/shared/model/merchant.model';
import { IDocument } from 'app/shared/model/document.model';

export interface IMerchantDocument {
  id?: number;
  merchant?: IMerchant | null;
  document?: IDocument | null;
}

export const defaultValue: Readonly<IMerchantDocument> = {};
