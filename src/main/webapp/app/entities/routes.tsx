import React from 'react';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Merchant from './merchant';
import Attachment from './attachment';
import Brand from './brand';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="merchant/*" element={<Merchant />} />
        <Route path="attachment/*" element={<Attachment />} />
        <Route path="brand/*" element={<Brand />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
