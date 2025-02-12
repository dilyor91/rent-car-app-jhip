import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getBrands } from 'app/entities/brand/brand.reducer';
import { createEntity, getEntity, reset, updateEntity } from './model.reducer';

export const ModelUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const brands = useAppSelector(state => state.brand.entities);
  const modelEntity = useAppSelector(state => state.model.entity);
  const loading = useAppSelector(state => state.model.loading);
  const updating = useAppSelector(state => state.model.updating);
  const updateSuccess = useAppSelector(state => state.model.updateSuccess);

  const handleClose = () => {
    navigate(`/model${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getBrands({}));
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
      ...modelEntity,
      ...values,
      brand: brands.find(it => it.id.toString() === values.brand?.toString()),
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
          ...modelEntity,
          brand: modelEntity?.brand?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="rentcarappjhipApp.model.home.createOrEditLabel" data-cy="ModelCreateUpdateHeading">
            <Translate contentKey="rentcarappjhipApp.model.home.createOrEditLabel">Create or edit a Model</Translate>
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
                  id="model-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField label={translate('rentcarappjhipApp.model.name')} id="model-name" name="name" data-cy="name" type="text" />
              <ValidatedField
                label={translate('rentcarappjhipApp.model.status')}
                id="model-status"
                name="status"
                data-cy="status"
                check
                type="checkbox"
              />
              <ValidatedField
                id="model-brand"
                name="brand"
                data-cy="brand"
                label={translate('rentcarappjhipApp.model.brand')}
                type="select"
              >
                <option value="" key="0" />
                {brands
                  ? brands.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/model" replace color="info">
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

export default ModelUpdate;
