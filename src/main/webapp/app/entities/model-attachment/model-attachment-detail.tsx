import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './model-attachment.reducer';

export const ModelAttachmentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const modelAttachmentEntity = useAppSelector(state => state.modelAttachment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="modelAttachmentDetailsHeading">
          <Translate contentKey="rentcarappjhipApp.modelAttachment.detail.title">ModelAttachment</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{modelAttachmentEntity.id}</dd>
          <dt>
            <span id="isMain">
              <Translate contentKey="rentcarappjhipApp.modelAttachment.isMain">Is Main</Translate>
            </span>
          </dt>
          <dd>{modelAttachmentEntity.isMain ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.modelAttachment.model">Model</Translate>
          </dt>
          <dd>{modelAttachmentEntity.model ? modelAttachmentEntity.model.id : ''}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.modelAttachment.attachment">Attachment</Translate>
          </dt>
          <dd>{modelAttachmentEntity.attachment ? modelAttachmentEntity.attachment.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/model-attachment" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/model-attachment/${modelAttachmentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ModelAttachmentDetail;
