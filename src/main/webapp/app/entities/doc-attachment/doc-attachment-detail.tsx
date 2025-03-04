import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './doc-attachment.reducer';

export const DocAttachmentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const docAttachmentEntity = useAppSelector(state => state.docAttachment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="docAttachmentDetailsHeading">
          <Translate contentKey="rentcarappjhipApp.docAttachment.detail.title">DocAttachment</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{docAttachmentEntity.id}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.docAttachment.document">Document</Translate>
          </dt>
          <dd>{docAttachmentEntity.document ? docAttachmentEntity.document.id : ''}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.docAttachment.attachment">Attachment</Translate>
          </dt>
          <dd>{docAttachmentEntity.attachment ? docAttachmentEntity.attachment.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/doc-attachment" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/doc-attachment/${docAttachmentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DocAttachmentDetail;
