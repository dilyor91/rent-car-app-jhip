import { IModel } from 'app/shared/model/model.model';
import { IAttachment } from 'app/shared/model/attachment.model';

export interface IModelAttachment {
  id?: number;
  isMain?: boolean | null;
  model?: IModel | null;
  attachment?: IAttachment | null;
}

export const defaultValue: Readonly<IModelAttachment> = {
  isMain: false,
};
