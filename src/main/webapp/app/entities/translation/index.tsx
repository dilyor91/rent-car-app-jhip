import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Translation from './translation';
import TranslationDetail from './translation-detail';
import TranslationUpdate from './translation-update';
import TranslationDeleteDialog from './translation-delete-dialog';

const TranslationRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Translation />} />
    <Route path="new" element={<TranslationUpdate />} />
    <Route path=":id">
      <Route index element={<TranslationDetail />} />
      <Route path="edit" element={<TranslationUpdate />} />
      <Route path="delete" element={<TranslationDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default TranslationRoutes;
