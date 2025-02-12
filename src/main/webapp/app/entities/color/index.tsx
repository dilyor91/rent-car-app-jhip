import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Color from './color';
import ColorDetail from './color-detail';
import ColorUpdate from './color-update';
import ColorDeleteDialog from './color-delete-dialog';

const ColorRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Color />} />
    <Route path="new" element={<ColorUpdate />} />
    <Route path=":id">
      <Route index element={<ColorDetail />} />
      <Route path="edit" element={<ColorUpdate />} />
      <Route path="delete" element={<ColorDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ColorRoutes;
