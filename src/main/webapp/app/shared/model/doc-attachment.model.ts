import { IDocument } from 'app/shared/model/document.model';
import { IAttachment } from 'app/shared/model/attachment.model';

export interface IDocAttachment {
  id?: number;
  document?: IDocument | null;
  attachment?: IAttachment | null;
}

export const defaultValue: Readonly<IDocAttachment> = {};
