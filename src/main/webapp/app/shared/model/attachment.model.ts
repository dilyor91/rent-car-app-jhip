export interface IAttachment {
  id?: number;
  fileName?: string | null;
  fileSize?: number | null;
  originalFileName?: string | null;
  path?: string | null;
  ext?: string | null;
}

export const defaultValue: Readonly<IAttachment> = {};
