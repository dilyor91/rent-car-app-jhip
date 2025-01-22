import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CarClass from './car-class';
import CarClassDetail from './car-class-detail';
import CarClassUpdate from './car-class-update';
import CarClassDeleteDialog from './car-class-delete-dialog';

const CarClassRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CarClass />} />
    <Route path="new" element={<CarClassUpdate />} />
    <Route path=":id">
      <Route index element={<CarClassDetail />} />
      <Route path="edit" element={<CarClassUpdate />} />
      <Route path="delete" element={<CarClassDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CarClassRoutes;
