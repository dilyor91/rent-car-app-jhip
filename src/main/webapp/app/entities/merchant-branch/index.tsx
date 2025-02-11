import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import MerchantBranch from './merchant-branch';
import MerchantBranchDetail from './merchant-branch-detail';
import MerchantBranchUpdate from './merchant-branch-update';
import MerchantBranchDeleteDialog from './merchant-branch-delete-dialog';

const MerchantBranchRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<MerchantBranch />} />
    <Route path="new" element={<MerchantBranchUpdate />} />
    <Route path=":id">
      <Route index element={<MerchantBranchDetail />} />
      <Route path="edit" element={<MerchantBranchUpdate />} />
      <Route path="delete" element={<MerchantBranchDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default MerchantBranchRoutes;
