import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CarMileage from './car-mileage';
import CarMileageDetail from './car-mileage-detail';
import CarMileageUpdate from './car-mileage-update';
import CarMileageDeleteDialog from './car-mileage-delete-dialog';

const CarMileageRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CarMileage />} />
    <Route path="new" element={<CarMileageUpdate />} />
    <Route path=":id">
      <Route index element={<CarMileageDetail />} />
      <Route path="edit" element={<CarMileageUpdate />} />
      <Route path="delete" element={<CarMileageDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CarMileageRoutes;
