import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ParamValue from './param-value';
import ParamValueDetail from './param-value-detail';
import ParamValueUpdate from './param-value-update';
import ParamValueDeleteDialog from './param-value-delete-dialog';

const ParamValueRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ParamValue />} />
    <Route path="new" element={<ParamValueUpdate />} />
    <Route path=":id">
      <Route index element={<ParamValueDetail />} />
      <Route path="edit" element={<ParamValueUpdate />} />
      <Route path="delete" element={<ParamValueDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ParamValueRoutes;
