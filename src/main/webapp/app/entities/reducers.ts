import merchant from 'app/entities/merchant/merchant.reducer';
import attachment from 'app/entities/attachment/attachment.reducer';
import brand from 'app/entities/brand/brand.reducer';
import carBody from 'app/entities/car-body/car-body.reducer';
import category from 'app/entities/category/category.reducer';
import carClass from 'app/entities/car-class/car-class.reducer';
import vehicle from 'app/entities/vehicle/vehicle.reducer';
import parametr from 'app/entities/parametr/parametr.reducer';
import merchantBranch from 'app/entities/merchant-branch/merchant-branch.reducer';
import merchantRole from 'app/entities/merchant-role/merchant-role.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  merchant,
  attachment,
  brand,
  carBody,
  category,
  carClass,
  vehicle,
  parametr,
  merchantBranch,
  merchantRole,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
