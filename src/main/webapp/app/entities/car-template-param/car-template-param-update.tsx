import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getCarTemplates } from 'app/entities/car-template/car-template.reducer';
import { getEntities as getParams } from 'app/entities/param/param.reducer';
import { getEntities as getParamValues } from 'app/entities/param-value/param-value.reducer';
import { createEntity, getEntity, reset, updateEntity } from './car-template-param.reducer';

export const CarTemplateParamUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const carTemplates = useAppSelector(state => state.carTemplate.entities);
  const params = useAppSelector(state => state.param.entities);
  const paramValues = useAppSelector(state => state.paramValue.entities);
  const carTemplateParamEntity = useAppSelector(state => state.carTemplateParam.entity);
  const loading = useAppSelector(state => state.carTemplateParam.loading);
  const updating = useAppSelector(state => state.carTemplateParam.updating);
  const updateSuccess = useAppSelector(state => state.carTemplateParam.updateSuccess);

  const handleClose = () => {
    navigate(`/car-template-param${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCarTemplates({}));
    dispatch(getParams({}));
    dispatch(getParamValues({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }

    const entity = {
      ...carTemplateParamEntity,
      ...values,
      carTemplate: carTemplates.find(it => it.id.toString() === values.carTemplate?.toString()),
      param: params.find(it => it.id.toString() === values.param?.toString()),
      paramValue: paramValues.find(it => it.id.toString() === values.paramValue?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...carTemplateParamEntity,
          carTemplate: carTemplateParamEntity?.carTemplate?.id,
          param: carTemplateParamEntity?.param?.id,
          paramValue: carTemplateParamEntity?.paramValue?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="rentcarappjhipApp.carTemplateParam.home.createOrEditLabel" data-cy="CarTemplateParamCreateUpdateHeading">
            <Translate contentKey="rentcarappjhipApp.carTemplateParam.home.createOrEditLabel">Create or edit a CarTemplateParam</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="car-template-param-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('rentcarappjhipApp.carTemplateParam.paramVal')}
                id="car-template-param-paramVal"
                name="paramVal"
                data-cy="paramVal"
                type="text"
              />
              <ValidatedField
                id="car-template-param-carTemplate"
                name="carTemplate"
                data-cy="carTemplate"
                label={translate('rentcarappjhipApp.carTemplateParam.carTemplate')}
                type="select"
              >
                <option value="" key="0" />
                {carTemplates
                  ? carTemplates.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="car-template-param-param"
                name="param"
                data-cy="param"
                label={translate('rentcarappjhipApp.carTemplateParam.param')}
                type="select"
              >
                <option value="" key="0" />
                {params
                  ? params.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="car-template-param-paramValue"
                name="paramValue"
                data-cy="paramValue"
                label={translate('rentcarappjhipApp.carTemplateParam.paramValue')}
                type="select"
              >
                <option value="" key="0" />
                {paramValues
                  ? paramValues.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/car-template-param" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default CarTemplateParamUpdate;
