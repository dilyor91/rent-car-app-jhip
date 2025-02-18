import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getCars } from 'app/entities/car/car.reducer';
import { getEntities as getAttachments } from 'app/entities/attachment/attachment.reducer';
import { createEntity, getEntity, reset, updateEntity } from './car-attachment.reducer';

export const CarAttachmentUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const cars = useAppSelector(state => state.car.entities);
  const attachments = useAppSelector(state => state.attachment.entities);
  const carAttachmentEntity = useAppSelector(state => state.carAttachment.entity);
  const loading = useAppSelector(state => state.carAttachment.loading);
  const updating = useAppSelector(state => state.carAttachment.updating);
  const updateSuccess = useAppSelector(state => state.carAttachment.updateSuccess);

  const handleClose = () => {
    navigate(`/car-attachment${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCars({}));
    dispatch(getAttachments({}));
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
      ...carAttachmentEntity,
      ...values,
      car: cars.find(it => it.id.toString() === values.car?.toString()),
      attachment: attachments.find(it => it.id.toString() === values.attachment?.toString()),
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
          ...carAttachmentEntity,
          car: carAttachmentEntity?.car?.id,
          attachment: carAttachmentEntity?.attachment?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="rentcarappjhipApp.carAttachment.home.createOrEditLabel" data-cy="CarAttachmentCreateUpdateHeading">
            <Translate contentKey="rentcarappjhipApp.carAttachment.home.createOrEditLabel">Create or edit a CarAttachment</Translate>
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
                  id="car-attachment-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('rentcarappjhipApp.carAttachment.isMain')}
                id="car-attachment-isMain"
                name="isMain"
                data-cy="isMain"
                check
                type="checkbox"
              />
              <ValidatedField
                id="car-attachment-car"
                name="car"
                data-cy="car"
                label={translate('rentcarappjhipApp.carAttachment.car')}
                type="select"
              >
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
                id="car-attachment-attachment"
                name="attachment"
                data-cy="attachment"
                label={translate('rentcarappjhipApp.carAttachment.attachment')}
                type="select"
              >
                <option value="" key="0" />
                {attachments
                  ? attachments.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/car-attachment" replace color="info">
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

export default CarAttachmentUpdate;
