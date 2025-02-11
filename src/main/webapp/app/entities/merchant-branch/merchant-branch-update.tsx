import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getMerchants } from 'app/entities/merchant/merchant.reducer';
import { createEntity, getEntity, reset, updateEntity } from './merchant-branch.reducer';

export const MerchantBranchUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const merchants = useAppSelector(state => state.merchant.entities);
  const merchantBranchEntity = useAppSelector(state => state.merchantBranch.entity);
  const loading = useAppSelector(state => state.merchantBranch.loading);
  const updating = useAppSelector(state => state.merchantBranch.updating);
  const updateSuccess = useAppSelector(state => state.merchantBranch.updateSuccess);

  const handleClose = () => {
    navigate('/merchant-branch');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getMerchants({}));
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
      ...merchantBranchEntity,
      ...values,
      merchant: merchants.find(it => it.id.toString() === values.merchant?.toString()),
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
          ...merchantBranchEntity,
          merchant: merchantBranchEntity?.merchant?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="rentcarappjhipApp.merchantBranch.home.createOrEditLabel" data-cy="MerchantBranchCreateUpdateHeading">
            <Translate contentKey="rentcarappjhipApp.merchantBranch.home.createOrEditLabel">Create or edit a MerchantBranch</Translate>
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
                  id="merchant-branch-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('rentcarappjhipApp.merchantBranch.name')}
                id="merchant-branch-name"
                name="name"
                data-cy="name"
                type="text"
              />
              <ValidatedField
                label={translate('rentcarappjhipApp.merchantBranch.address')}
                id="merchant-branch-address"
                name="address"
                data-cy="address"
                type="text"
              />
              <ValidatedField
                label={translate('rentcarappjhipApp.merchantBranch.latitude')}
                id="merchant-branch-latitude"
                name="latitude"
                data-cy="latitude"
                type="text"
              />
              <ValidatedField
                label={translate('rentcarappjhipApp.merchantBranch.longitude')}
                id="merchant-branch-longitude"
                name="longitude"
                data-cy="longitude"
                type="text"
              />
              <ValidatedField
                label={translate('rentcarappjhipApp.merchantBranch.phone')}
                id="merchant-branch-phone"
                name="phone"
                data-cy="phone"
                type="text"
              />
              <ValidatedField
                id="merchant-branch-merchant"
                name="merchant"
                data-cy="merchant"
                label={translate('rentcarappjhipApp.merchantBranch.merchant')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/merchant-branch" replace color="info">
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

export default MerchantBranchUpdate;
