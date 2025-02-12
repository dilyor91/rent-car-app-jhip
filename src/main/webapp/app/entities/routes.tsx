import React from 'react';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Merchant from './merchant';
import Attachment from './attachment';
import Brand from './brand';
import CarBody from './car-body';
import Category from './category';
import CarClass from './car-class';
import Vehicle from './vehicle';
import Parametr from './parametr';
import MerchantBranch from './merchant-branch';
import MerchantRole from './merchant-role';
import Model from './model';
import Color from './color';
import Param from './param';
import ParamValue from './param-value';
import Translation from './translation';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="merchant/*" element={<Merchant />} />
        <Route path="attachment/*" element={<Attachment />} />
        <Route path="brand/*" element={<Brand />} />
        <Route path="car-body/*" element={<CarBody />} />
        <Route path="category/*" element={<Category />} />
        <Route path="car-class/*" element={<CarClass />} />
        <Route path="vehicle/*" element={<Vehicle />} />
        <Route path="parametr/*" element={<Parametr />} />
        <Route path="merchant-branch/*" element={<MerchantBranch />} />
        <Route path="merchant-role/*" element={<MerchantRole />} />
        <Route path="model/*" element={<Model />} />
        <Route path="color/*" element={<Color />} />
        <Route path="param/*" element={<Param />} />
        <Route path="param-value/*" element={<ParamValue />} />
        <Route path="translation/*" element={<Translation />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
