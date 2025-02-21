import dayjs from 'dayjs';
import { ICar } from 'app/shared/model/car.model';
import { MileageEnum } from 'app/shared/model/enumerations/mileage-enum.model';

export interface ICarMileage {
  id?: number;
  value?: number | null;
  unit?: keyof typeof MileageEnum | null;
  date?: dayjs.Dayjs | null;
  car?: ICar | null;
}

export const defaultValue: Readonly<ICarMileage> = {};
