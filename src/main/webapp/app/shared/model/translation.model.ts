import { LanguageEnum } from 'app/shared/model/enumerations/language-enum.model';

export interface ITranslation {
  id?: number;
  entityType?: string | null;
  entityId?: number | null;
  lang?: keyof typeof LanguageEnum | null;
  value?: string | null;
  description?: string | null;
}

export const defaultValue: Readonly<ITranslation> = {};
