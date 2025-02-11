import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import MerchantRole from './merchant-role';
import MerchantRoleDetail from './merchant-role-detail';
import MerchantRoleUpdate from './merchant-role-update';
import MerchantRoleDeleteDialog from './merchant-role-delete-dialog';

const MerchantRoleRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<MerchantRole />} />
    <Route path="new" element={<MerchantRoleUpdate />} />
    <Route path=":id">
      <Route index element={<MerchantRoleDetail />} />
      <Route path="edit" element={<MerchantRoleUpdate />} />
      <Route path="delete" element={<MerchantRoleDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default MerchantRoleRoutes;
