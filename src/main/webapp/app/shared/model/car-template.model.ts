import { IModel } from 'app/shared/model/model.model';

export interface ICarTemplate {
  id?: number;
  status?: boolean | null;
  model?: IModel | null;
}

export const defaultValue: Readonly<ICarTemplate> = {
  status: false,
};
