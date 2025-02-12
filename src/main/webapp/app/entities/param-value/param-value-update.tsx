import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getParams } from 'app/entities/param/param.reducer';
import { createEntity, getEntity, reset, updateEntity } from './param-value.reducer';

export const ParamValueUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const params = useAppSelector(state => state.param.entities);
  const paramValueEntity = useAppSelector(state => state.paramValue.entity);
  const loading = useAppSelector(state => state.paramValue.loading);
  const updating = useAppSelector(state => state.paramValue.updating);
  const updateSuccess = useAppSelector(state => state.paramValue.updateSuccess);

  const handleClose = () => {
    navigate(`/param-value${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

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
      ...paramValueEntity,
      ...values,
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
          ...paramValueEntity,
          param: paramValueEntity?.param?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="rentcarappjhipApp.paramValue.home.createOrEditLabel" data-cy="ParamValueCreateUpdateHeading">
            <Translate contentKey="rentcarappjhipApp.paramValue.home.createOrEditLabel">Create or edit a ParamValue</Translate>
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
                  id="param-value-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('rentcarappjhipApp.paramValue.name')}
                id="param-value-name"
                name="name"
                data-cy="name"
                type="text"
              />
              <ValidatedField
                label={translate('rentcarappjhipApp.paramValue.status')}
                id="param-value-status"
                name="status"
                data-cy="status"
                check
                type="checkbox"
              />
              <ValidatedField
                id="param-value-param"
                name="param"
                data-cy="param"
                label={translate('rentcarappjhipApp.paramValue.param')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/param-value" replace color="info">
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

export default ParamValueUpdate;
