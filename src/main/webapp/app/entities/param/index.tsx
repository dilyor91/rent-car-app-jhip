import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Param from './param';
import ParamDetail from './param-detail';
import ParamUpdate from './param-update';
import ParamDeleteDialog from './param-delete-dialog';

const ParamRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Param />} />
    <Route path="new" element={<ParamUpdate />} />
    <Route path=":id">
      <Route index element={<ParamDetail />} />
      <Route path="edit" element={<ParamUpdate />} />
      <Route path="delete" element={<ParamDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ParamRoutes;
