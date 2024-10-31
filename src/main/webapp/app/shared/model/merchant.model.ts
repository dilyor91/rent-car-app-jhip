export interface IMerchant {
  id?: number;
  companyName?: string;
  brandName?: string | null;
  inn?: string | null;
  owner?: string | null;
  phone?: string | null;
  address?: string | null;
}

export const defaultValue: Readonly<IMerchant> = {};
