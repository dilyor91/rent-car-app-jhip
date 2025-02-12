import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Model from './model';
import ModelDetail from './model-detail';
import ModelUpdate from './model-update';
import ModelDeleteDialog from './model-delete-dialog';

const ModelRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Model />} />
    <Route path="new" element={<ModelUpdate />} />
    <Route path=":id">
      <Route index element={<ModelDetail />} />
      <Route path="edit" element={<ModelUpdate />} />
      <Route path="delete" element={<ModelDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ModelRoutes;
