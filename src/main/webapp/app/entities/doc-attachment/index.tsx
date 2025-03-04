import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import DocAttachment from './doc-attachment';
import DocAttachmentDetail from './doc-attachment-detail';
import DocAttachmentUpdate from './doc-attachment-update';
import DocAttachmentDeleteDialog from './doc-attachment-delete-dialog';

const DocAttachmentRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<DocAttachment />} />
    <Route path="new" element={<DocAttachmentUpdate />} />
    <Route path=":id">
      <Route index element={<DocAttachmentDetail />} />
      <Route path="edit" element={<DocAttachmentUpdate />} />
      <Route path="delete" element={<DocAttachmentDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default DocAttachmentRoutes;
