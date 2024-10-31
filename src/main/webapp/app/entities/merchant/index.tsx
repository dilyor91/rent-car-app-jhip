import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Merchant from './merchant';
import MerchantDetail from './merchant-detail';
import MerchantUpdate from './merchant-update';
import MerchantDeleteDialog from './merchant-delete-dialog';

const MerchantRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Merchant />} />
    <Route path="new" element={<MerchantUpdate />} />
    <Route path=":id">
      <Route index element={<MerchantDetail />} />
      <Route path="edit" element={<MerchantUpdate />} />
      <Route path="delete" element={<MerchantDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default MerchantRoutes;
