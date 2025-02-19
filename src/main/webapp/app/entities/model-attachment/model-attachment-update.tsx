import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getModels } from 'app/entities/model/model.reducer';
import { getEntities as getAttachments } from 'app/entities/attachment/attachment.reducer';
import { createEntity, getEntity, reset, updateEntity } from './model-attachment.reducer';

export const ModelAttachmentUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const models = useAppSelector(state => state.model.entities);
  const attachments = useAppSelector(state => state.attachment.entities);
  const modelAttachmentEntity = useAppSelector(state => state.modelAttachment.entity);
  const loading = useAppSelector(state => state.modelAttachment.loading);
  const updating = useAppSelector(state => state.modelAttachment.updating);
  const updateSuccess = useAppSelector(state => state.modelAttachment.updateSuccess);

  const handleClose = () => {
    navigate(`/model-attachment${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getModels({}));
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
      ...modelAttachmentEntity,
      ...values,
      model: models.find(it => it.id.toString() === values.model?.toString()),
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
          ...modelAttachmentEntity,
          model: modelAttachmentEntity?.model?.id,
          attachment: modelAttachmentEntity?.attachment?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="rentcarappjhipApp.modelAttachment.home.createOrEditLabel" data-cy="ModelAttachmentCreateUpdateHeading">
            <Translate contentKey="rentcarappjhipApp.modelAttachment.home.createOrEditLabel">Create or edit a ModelAttachment</Translate>
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
                  id="model-attachment-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('rentcarappjhipApp.modelAttachment.isMain')}
                id="model-attachment-isMain"
                name="isMain"
                data-cy="isMain"
                check
                type="checkbox"
              />
              <ValidatedField
                id="model-attachment-model"
                name="model"
                data-cy="model"
                label={translate('rentcarappjhipApp.modelAttachment.model')}
                type="select"
              >
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
                id="model-attachment-attachment"
                name="attachment"
                data-cy="attachment"
                label={translate('rentcarappjhipApp.modelAttachment.attachment')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/model-attachment" replace color="info">
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

export default ModelAttachmentUpdate;
