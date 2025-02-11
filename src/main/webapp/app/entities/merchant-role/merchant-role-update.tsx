import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntities as getMerchants } from 'app/entities/merchant/merchant.reducer';
import { getEntities as getMerchantBranches } from 'app/entities/merchant-branch/merchant-branch.reducer';
import { MerchantRoleEnum } from 'app/shared/model/enumerations/merchant-role-enum.model';
import { createEntity, getEntity, reset, updateEntity } from './merchant-role.reducer';

export const MerchantRoleUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const merchants = useAppSelector(state => state.merchant.entities);
  const merchantBranches = useAppSelector(state => state.merchantBranch.entities);
  const merchantRoleEntity = useAppSelector(state => state.merchantRole.entity);
  const loading = useAppSelector(state => state.merchantRole.loading);
  const updating = useAppSelector(state => state.merchantRole.updating);
  const updateSuccess = useAppSelector(state => state.merchantRole.updateSuccess);
  const merchantRoleEnumValues = Object.keys(MerchantRoleEnum);

  const handleClose = () => {
    navigate(`/merchant-role${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
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

    const entity = {
      ...merchantRoleEntity,
      ...values,
      user: users.find(it => it.id.toString() === values.user?.toString()),
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
          merchantRoleType: 'OWNER',
          ...merchantRoleEntity,
          user: merchantRoleEntity?.user?.id,
          merchant: merchantRoleEntity?.merchant?.id,
          merchantBranch: merchantRoleEntity?.merchantBranch?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="rentcarappjhipApp.merchantRole.home.createOrEditLabel" data-cy="MerchantRoleCreateUpdateHeading">
            <Translate contentKey="rentcarappjhipApp.merchantRole.home.createOrEditLabel">Create or edit a MerchantRole</Translate>
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
                  id="merchant-role-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('rentcarappjhipApp.merchantRole.merchantRoleType')}
                id="merchant-role-merchantRoleType"
                name="merchantRoleType"
                data-cy="merchantRoleType"
                type="select"
              >
                {merchantRoleEnumValues.map(merchantRoleEnum => (
                  <option value={merchantRoleEnum} key={merchantRoleEnum}>
                    {translate(`rentcarappjhipApp.MerchantRoleEnum.${merchantRoleEnum}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                id="merchant-role-user"
                name="user"
                data-cy="user"
                label={translate('rentcarappjhipApp.merchantRole.user')}
                type="select"
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="merchant-role-merchant"
                name="merchant"
                data-cy="merchant"
                label={translate('rentcarappjhipApp.merchantRole.merchant')}
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
                id="merchant-role-merchantBranch"
                name="merchantBranch"
                data-cy="merchantBranch"
                label={translate('rentcarappjhipApp.merchantRole.merchantBranch')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/merchant-role" replace color="info">
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

export default MerchantRoleUpdate;
