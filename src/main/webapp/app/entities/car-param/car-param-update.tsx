import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getCars } from 'app/entities/car/car.reducer';
import { getEntities as getParams } from 'app/entities/param/param.reducer';
import { createEntity, getEntity, reset, updateEntity } from './car-param.reducer';

export const CarParamUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const cars = useAppSelector(state => state.car.entities);
  const params = useAppSelector(state => state.param.entities);
  const carParamEntity = useAppSelector(state => state.carParam.entity);
  const loading = useAppSelector(state => state.carParam.loading);
  const updating = useAppSelector(state => state.carParam.updating);
  const updateSuccess = useAppSelector(state => state.carParam.updateSuccess);

  const handleClose = () => {
    navigate(`/car-param${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCars({}));
    dispatch(getParams({}));
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
      ...carParamEntity,
      ...values,
      car: cars.find(it => it.id.toString() === values.car?.toString()),
      param: params.find(it => it.id.toString() === values.param?.toString()),
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
          ...carParamEntity,
          car: carParamEntity?.car?.id,
          param: carParamEntity?.param?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="rentcarappjhipApp.carParam.home.createOrEditLabel" data-cy="CarParamCreateUpdateHeading">
            <Translate contentKey="rentcarappjhipApp.carParam.home.createOrEditLabel">Create or edit a CarParam</Translate>
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
                  id="car-param-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('rentcarappjhipApp.carParam.paramItemValue')}
                id="car-param-paramItemValue"
                name="paramItemValue"
                data-cy="paramItemValue"
                type="text"
              />
              <ValidatedField
                label={translate('rentcarappjhipApp.carParam.paramValue')}
                id="car-param-paramValue"
                name="paramValue"
                data-cy="paramValue"
                type="text"
              />
              <ValidatedField id="car-param-car" name="car" data-cy="car" label={translate('rentcarappjhipApp.carParam.car')} type="select">
                <option value="" key="0" />
                {cars
                  ? cars.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="car-param-param"
                name="param"
                data-cy="param"
                label={translate('rentcarappjhipApp.carParam.param')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/car-param" replace color="info">
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

export default CarParamUpdate;
