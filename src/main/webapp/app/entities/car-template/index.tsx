import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CarTemplate from './car-template';
import CarTemplateDetail from './car-template-detail';
import CarTemplateUpdate from './car-template-update';
import CarTemplateDeleteDialog from './car-template-delete-dialog';

const CarTemplateRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CarTemplate />} />
    <Route path="new" element={<CarTemplateUpdate />} />
    <Route path=":id">
      <Route index element={<CarTemplateDetail />} />
      <Route path="edit" element={<CarTemplateUpdate />} />
      <Route path="delete" element={<CarTemplateDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CarTemplateRoutes;
