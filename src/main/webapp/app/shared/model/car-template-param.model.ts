import { ICarTemplate } from 'app/shared/model/car-template.model';
import { IParam } from 'app/shared/model/param.model';
import { IParamValue } from 'app/shared/model/param-value.model';

export interface ICarTemplateParam {
  id?: number;
  paramVal?: string | null;
  carTemplate?: ICarTemplate | null;
  param?: IParam | null;
  paramValue?: IParamValue | null;
}

export const defaultValue: Readonly<ICarTemplateParam> = {};
