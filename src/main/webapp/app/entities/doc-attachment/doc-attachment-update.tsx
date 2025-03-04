import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getDocuments } from 'app/entities/document/document.reducer';
import { getEntities as getAttachments } from 'app/entities/attachment/attachment.reducer';
import { createEntity, getEntity, reset, updateEntity } from './doc-attachment.reducer';

export const DocAttachmentUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const documents = useAppSelector(state => state.document.entities);
  const attachments = useAppSelector(state => state.attachment.entities);
  const docAttachmentEntity = useAppSelector(state => state.docAttachment.entity);
  const loading = useAppSelector(state => state.docAttachment.loading);
  const updating = useAppSelector(state => state.docAttachment.updating);
  const updateSuccess = useAppSelector(state => state.docAttachment.updateSuccess);

  const handleClose = () => {
    navigate('/doc-attachment');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getDocuments({}));
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
      ...docAttachmentEntity,
      ...values,
      document: documents.find(it => it.id.toString() === values.document?.toString()),
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
          ...docAttachmentEntity,
          document: docAttachmentEntity?.document?.id,
          attachment: docAttachmentEntity?.attachment?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="rentcarappjhipApp.docAttachment.home.createOrEditLabel" data-cy="DocAttachmentCreateUpdateHeading">
            <Translate contentKey="rentcarappjhipApp.docAttachment.home.createOrEditLabel">Create or edit a DocAttachment</Translate>
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
                  id="doc-attachment-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                id="doc-attachment-document"
                name="document"
                data-cy="document"
                label={translate('rentcarappjhipApp.docAttachment.document')}
                type="select"
              >
                <option value="" key="0" />
                {documents
                  ? documents.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="doc-attachment-attachment"
                name="attachment"
                data-cy="attachment"
                label={translate('rentcarappjhipApp.docAttachment.attachment')}
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
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/doc-attachment" replace color="info">
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

export default DocAttachmentUpdate;
