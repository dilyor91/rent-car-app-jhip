export interface IVehicle {
  id?: number;
  name?: string;
  status?: boolean | null;
}

export const defaultValue: Readonly<IVehicle> = {
  status: false,
};
