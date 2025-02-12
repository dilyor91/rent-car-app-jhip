import { IParam } from 'app/shared/model/param.model';

export interface IParamValue {
  id?: number;
  name?: string | null;
  status?: boolean | null;
  param?: IParam | null;
}

export const defaultValue: Readonly<IParamValue> = {
  status: false,
};
