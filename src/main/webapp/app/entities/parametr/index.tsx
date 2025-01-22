import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Parametr from './parametr';
import ParametrDetail from './parametr-detail';
import ParametrUpdate from './parametr-update';
import ParametrDeleteDialog from './parametr-delete-dialog';

const ParametrRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Parametr />} />
    <Route path="new" element={<ParametrUpdate />} />
    <Route path=":id">
      <Route index element={<ParametrDetail />} />
      <Route path="edit" element={<ParametrUpdate />} />
      <Route path="delete" element={<ParametrDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ParametrRoutes;
