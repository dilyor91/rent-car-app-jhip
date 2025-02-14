import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getModels } from 'app/entities/model/model.reducer';
import { getEntities as getMerchants } from 'app/entities/merchant/merchant.reducer';
import { getEntities as getMerchantBranches } from 'app/entities/merchant-branch/merchant-branch.reducer';
import { createEntity, getEntity, reset, updateEntity } from './car.reducer';

export const CarUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const models = useAppSelector(state => state.model.entities);
  const merchants = useAppSelector(state => state.merchant.entities);
  const merchantBranches = useAppSelector(state => state.merchantBranch.entities);
  const carEntity = useAppSelector(state => state.car.entity);
  const loading = useAppSelector(state => state.car.loading);
  const updating = useAppSelector(state => state.car.updating);
  const updateSuccess = useAppSelector(state => state.car.updateSuccess);

  const handleClose = () => {
    navigate(`/car${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getModels({}));
    dispatch(getMerchants({}));
    dispatch(getMerchantBranches({}));
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
    if (values.stateNumberPlate !== undefined && typeof values.stateNumberPlate !== 'number') {
      values.stateNumberPlate = Number(values.stateNumberPlate);
    }
    if (values.deposit !== undefined && typeof values.deposit !== 'number') {
      values.deposit = Number(values.deposit);
    }

    const entity = {
      ...carEntity,
      ...values,
      model: models.find(it => it.id.toString() === values.model?.toString()),
      merchant: merchants.find(it => it.id.toString() === values.merchant?.toString()),
      merchantBranch: merchantBranches.find(it => it.id.toString() === values.merchantBranch?.toString()),
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
          ...carEntity,
          model: carEntity?.model?.id,
          merchant: carEntity?.merchant?.id,
          merchantBranch: carEntity?.merchantBranch?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="rentcarappjhipApp.car.home.createOrEditLabel" data-cy="CarCreateUpdateHeading">
            <Translate contentKey="rentcarappjhipApp.car.home.createOrEditLabel">Create or edit a Car</Translate>
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
                  id="car-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('rentcarappjhipApp.car.stateNumberPlate')}
                id="car-stateNumberPlate"
                name="stateNumberPlate"
                data-cy="stateNumberPlate"
                type="text"
              />
              <ValidatedField
                label={translate('rentcarappjhipApp.car.deposit')}
                id="car-deposit"
                name="deposit"
                data-cy="deposit"
                type="text"
              />
              <ValidatedField id="car-model" name="model" data-cy="model" label={translate('rentcarappjhipApp.car.model')} type="select">
                <option value="" key="0" />
                {models
                  ? models.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="car-merchant"
                name="merchant"
                data-cy="merchant"
                label={translate('rentcarappjhipApp.car.merchant')}
                type="select"
              >
                <option value="" key="0" />
                {merchants
                  ? merchants.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="car-merchantBranch"
                name="merchantBranch"
                data-cy="merchantBranch"
                label={translate('rentcarappjhipApp.car.merchantBranch')}
                type="select"
              >
                <option value="" key="0" />
                {merchantBranches
                  ? merchantBranches.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/car" replace color="info">
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

export default CarUpdate;
