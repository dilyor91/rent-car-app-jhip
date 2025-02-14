import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CarParam from './car-param';
import CarParamDetail from './car-param-detail';
import CarParamUpdate from './car-param-update';
import CarParamDeleteDialog from './car-param-delete-dialog';

const CarParamRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CarParam />} />
    <Route path="new" element={<CarParamUpdate />} />
    <Route path=":id">
      <Route index element={<CarParamDetail />} />
      <Route path="edit" element={<CarParamUpdate />} />
      <Route path="delete" element={<CarParamDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CarParamRoutes;
