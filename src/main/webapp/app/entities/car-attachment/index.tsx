import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import CarAttachment from './car-attachment';
import CarAttachmentDetail from './car-attachment-detail';
import CarAttachmentUpdate from './car-attachment-update';
import CarAttachmentDeleteDialog from './car-attachment-delete-dialog';

const CarAttachmentRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<CarAttachment />} />
    <Route path="new" element={<CarAttachmentUpdate />} />
    <Route path=":id">
      <Route index element={<CarAttachmentDetail />} />
      <Route path="edit" element={<CarAttachmentUpdate />} />
      <Route path="delete" element={<CarAttachmentDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default CarAttachmentRoutes;
