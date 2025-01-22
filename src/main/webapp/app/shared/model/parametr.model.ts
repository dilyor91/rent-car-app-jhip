export interface IParametr {
  id?: number;
  name?: string;
  status?: boolean | null;
}

export const defaultValue: Readonly<IParametr> = {
  status: false,
};
