import { ICar } from 'app/shared/model/car.model';
import { IAttachment } from 'app/shared/model/attachment.model';

export interface ICarAttachment {
  id?: number;
  isMain?: boolean | null;
  car?: ICar | null;
  attachment?: IAttachment | null;
}

export const defaultValue: Readonly<ICarAttachment> = {
  isMain: false,
};
