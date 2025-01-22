export interface ICategory {
  id?: number;
  name?: string;
  status?: boolean | null;
}

export const defaultValue: Readonly<ICategory> = {
  status: false,
};
