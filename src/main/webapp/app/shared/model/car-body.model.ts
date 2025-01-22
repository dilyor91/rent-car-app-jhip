export interface ICarBody {
  id?: number;
  name?: string;
  status?: boolean | null;
}

export const defaultValue: Readonly<ICarBody> = {
  status: false,
};
