import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CarTemplateParam from './car-template-param';
import CarTemplateParamDetail from './car-template-param-detail';
import CarTemplateParamUpdate from './car-template-param-update';
import CarTemplateParamDeleteDialog from './car-template-param-delete-dialog';

const CarTemplateParamRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CarTemplateParam />} />
    <Route path="new" element={<CarTemplateParamUpdate />} />
    <Route path=":id">
      <Route index element={<CarTemplateParamDetail />} />
      <Route path="edit" element={<CarTemplateParamUpdate />} />
      <Route path="delete" element={<CarTemplateParamDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CarTemplateParamRoutes;
