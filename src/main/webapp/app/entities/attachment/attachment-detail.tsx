import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './attachment.reducer';

export const AttachmentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const attachmentEntity = useAppSelector(state => state.attachment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="attachmentDetailsHeading">
          <Translate contentKey="rentcarappjhipApp.attachment.detail.title">Attachment</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{attachmentEntity.id}</dd>
          <dt>
            <span id="fileName">
              <Translate contentKey="rentcarappjhipApp.attachment.fileName">File Name</Translate>
            </span>
          </dt>
          <dd>{attachmentEntity.fileName}</dd>
          <dt>
            <span id="fileSize">
              <Translate contentKey="rentcarappjhipApp.attachment.fileSize">File Size</Translate>
            </span>
          </dt>
          <dd>{attachmentEntity.fileSize}</dd>
          <dt>
            <span id="originalFileName">
              <Translate contentKey="rentcarappjhipApp.attachment.originalFileName">Original File Name</Translate>
            </span>
          </dt>
          <dd>{attachmentEntity.originalFileName}</dd>
          <dt>
            <span id="path">
              <Translate contentKey="rentcarappjhipApp.attachment.path">Path</Translate>
            </span>
          </dt>
          <dd>{attachmentEntity.path}</dd>
          <dt>
            <span id="ext">
              <Translate contentKey="rentcarappjhipApp.attachment.ext">Ext</Translate>
            </span>
          </dt>
          <dd>{attachmentEntity.ext}</dd>
        </dl>
        <Button tag={Link} to="/attachment" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/attachment/${attachmentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AttachmentDetail;
