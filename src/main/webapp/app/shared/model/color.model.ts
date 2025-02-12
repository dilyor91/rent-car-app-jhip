export interface IColor {
  id?: number;
  name?: string | null;
  hex?: string | null;
  status?: boolean | null;
}

export const defaultValue: Readonly<IColor> = {
  status: false,
};
