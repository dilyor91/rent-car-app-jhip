import { IBrand } from 'app/shared/model/brand.model';

export interface IModel {
  id?: number;
  name?: string | null;
  status?: boolean | null;
  brand?: IBrand | null;
}

export const defaultValue: Readonly<IModel> = {
  status: false,
};
