export interface ICarClass {
  id?: number;
  name?: string;
  status?: boolean | null;
}

export const defaultValue: Readonly<ICarClass> = {
  status: false,
};
