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
import model from 'app/entities/model/model.reducer';
import color from 'app/entities/color/color.reducer';
import param from 'app/entities/param/param.reducer';
import paramValue from 'app/entities/param-value/param-value.reducer';
import translation from 'app/entities/translation/translation.reducer';
import car from 'app/entities/car/car.reducer';
import carParam from 'app/entities/car-param/car-param.reducer';
import carTemplate from 'app/entities/car-template/car-template.reducer';
import carTemplateParam from 'app/entities/car-template-param/car-template-param.reducer';
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
  model,
  color,
  param,
  paramValue,
  translation,
  car,
  carParam,
  carTemplate,
  carTemplateParam,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
