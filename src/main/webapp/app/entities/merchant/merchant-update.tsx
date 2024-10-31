import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { createEntity, getEntity, reset, updateEntity } from './merchant.reducer';

export const MerchantUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const merchantEntity = useAppSelector(state => state.merchant.entity);
  const loading = useAppSelector(state => state.merchant.loading);
  const updating = useAppSelector(state => state.merchant.updating);
  const updateSuccess = useAppSelector(state => state.merchant.updateSuccess);

  const handleClose = () => {
    navigate(`/merchant${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
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
      ...merchantEntity,
      ...values,
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
          ...merchantEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="rentcarappjhipApp.merchant.home.createOrEditLabel" data-cy="MerchantCreateUpdateHeading">
            <Translate contentKey="rentcarappjhipApp.merchant.home.createOrEditLabel">Create or edit a Merchant</Translate>
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
                  id="merchant-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('rentcarappjhipApp.merchant.companyName')}
                id="merchant-companyName"
                name="companyName"
                data-cy="companyName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('rentcarappjhipApp.merchant.brandName')}
                id="merchant-brandName"
                name="brandName"
                data-cy="brandName"
                type="text"
              />
              <ValidatedField label={translate('rentcarappjhipApp.merchant.inn')} id="merchant-inn" name="inn" data-cy="inn" type="text" />
              <ValidatedField
                label={translate('rentcarappjhipApp.merchant.owner')}
                id="merchant-owner"
                name="owner"
                data-cy="owner"
                type="text"
              />
              <ValidatedField
                label={translate('rentcarappjhipApp.merchant.phone')}
                id="merchant-phone"
                name="phone"
                data-cy="phone"
                type="text"
              />
              <ValidatedField
                label={translate('rentcarappjhipApp.merchant.address')}
                id="merchant-address"
                name="address"
                data-cy="address"
                type="text"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/merchant" replace color="info">
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

export default MerchantUpdate;
