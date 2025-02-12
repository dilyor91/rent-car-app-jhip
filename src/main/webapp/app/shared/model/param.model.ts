import { FieldTypeEnum } from 'app/shared/model/enumerations/field-type-enum.model';

export interface IParam {
  id?: number;
  name?: string | null;
  description?: string | null;
  fieldType?: keyof typeof FieldTypeEnum | null;
  status?: boolean | null;
  isDefault?: boolean | null;
}

export const defaultValue: Readonly<IParam> = {
  status: false,
  isDefault: false,
};
