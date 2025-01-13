import { IAttachment } from 'app/shared/model/attachment.model';

export interface IBrand {
  id?: number;
  name?: string;
  status?: boolean | null;
  attachment?: IAttachment | null;
}

export const defaultValue: Readonly<IBrand> = {
  status: false,
};
