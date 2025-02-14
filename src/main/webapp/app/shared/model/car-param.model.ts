import { ICar } from 'app/shared/model/car.model';
import { IParam } from 'app/shared/model/param.model';

export interface ICarParam {
  id?: number;
  paramItemValue?: string | null;
  paramValue?: string | null;
  car?: ICar | null;
  param?: IParam | null;
}

export const defaultValue: Readonly<ICarParam> = {};
