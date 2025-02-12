import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { FieldTypeEnum } from 'app/shared/model/enumerations/field-type-enum.model';
import { createEntity, getEntity, reset, updateEntity } from './param.reducer';

export const ParamUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const paramEntity = useAppSelector(state => state.param.entity);
  const loading = useAppSelector(state => state.param.loading);
  const updating = useAppSelector(state => state.param.updating);
  const updateSuccess = useAppSelector(state => state.param.updateSuccess);
  const fieldTypeEnumValues = Object.keys(FieldTypeEnum);

  const handleClose = () => {
    navigate(`/param${location.search}`);
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
      ...paramEntity,
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
          fieldType: 'INPUT_FIELD',
          ...paramEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="rentcarappjhipApp.param.home.createOrEditLabel" data-cy="ParamCreateUpdateHeading">
            <Translate contentKey="rentcarappjhipApp.param.home.createOrEditLabel">Create or edit a Param</Translate>
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
                  id="param-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField label={translate('rentcarappjhipApp.param.name')} id="param-name" name="name" data-cy="name" type="text" />
              <ValidatedField
                label={translate('rentcarappjhipApp.param.description')}
                id="param-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <ValidatedField
                label={translate('rentcarappjhipApp.param.fieldType')}
                id="param-fieldType"
                name="fieldType"
                data-cy="fieldType"
                type="select"
              >
                {fieldTypeEnumValues.map(fieldTypeEnum => (
                  <option value={fieldTypeEnum} key={fieldTypeEnum}>
                    {translate(`rentcarappjhipApp.FieldTypeEnum.${fieldTypeEnum}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('rentcarappjhipApp.param.status')}
                id="param-status"
                name="status"
                data-cy="status"
                check
                type="checkbox"
              />
              <ValidatedField
                label={translate('rentcarappjhipApp.param.isDefault')}
                id="param-isDefault"
                name="isDefault"
                data-cy="isDefault"
                check
                type="checkbox"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/param" replace color="info">
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

export default ParamUpdate;
