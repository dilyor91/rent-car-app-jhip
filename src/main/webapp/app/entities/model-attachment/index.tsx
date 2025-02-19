import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import ModelAttachment from './model-attachment';
import ModelAttachmentDetail from './model-attachment-detail';
import ModelAttachmentUpdate from './model-attachment-update';
import ModelAttachmentDeleteDialog from './model-attachment-delete-dialog';

const ModelAttachmentRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<ModelAttachment />} />
    <Route path="new" element={<ModelAttachmentUpdate />} />
    <Route path=":id">
      <Route index element={<ModelAttachmentDetail />} />
      <Route path="edit" element={<ModelAttachmentUpdate />} />
      <Route path="delete" element={<ModelAttachmentDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default ModelAttachmentRoutes;
