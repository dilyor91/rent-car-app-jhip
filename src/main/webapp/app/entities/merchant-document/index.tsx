import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import MerchantDocument from './merchant-document';
import MerchantDocumentDetail from './merchant-document-detail';
import MerchantDocumentUpdate from './merchant-document-update';
import MerchantDocumentDeleteDialog from './merchant-document-delete-dialog';

const MerchantDocumentRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<MerchantDocument />} />
    <Route path="new" element={<MerchantDocumentUpdate />} />
    <Route path=":id">
      <Route index element={<MerchantDocumentDetail />} />
      <Route path="edit" element={<MerchantDocumentUpdate />} />
      <Route path="delete" element={<MerchantDocumentDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default MerchantDocumentRoutes;
