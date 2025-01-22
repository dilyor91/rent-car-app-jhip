import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CarBody from './car-body';
import CarBodyDetail from './car-body-detail';
import CarBodyUpdate from './car-body-update';
import CarBodyDeleteDialog from './car-body-delete-dialog';

const CarBodyRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CarBody />} />
    <Route path="new" element={<CarBodyUpdate />} />
    <Route path=":id">
      <Route index element={<CarBodyDetail />} />
      <Route path="edit" element={<CarBodyUpdate />} />
      <Route path="delete" element={<CarBodyDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CarBodyRoutes;
