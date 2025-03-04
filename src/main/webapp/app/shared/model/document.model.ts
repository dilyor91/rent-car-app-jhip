import dayjs from 'dayjs';
import { DocTypeEnum } from 'app/shared/model/enumerations/doc-type-enum.model';

export interface IDocument {
  id?: number;
  name?: string | null;
  docType?: keyof typeof DocTypeEnum | null;
  givenDate?: dayjs.Dayjs | null;
  issuedDate?: dayjs.Dayjs | null;
  docStatus?: boolean | null;
}

export const defaultValue: Readonly<IDocument> = {
  docStatus: false,
};
