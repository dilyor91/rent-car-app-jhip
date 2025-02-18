import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './car-attachment.reducer';

export const CarAttachmentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const carAttachmentEntity = useAppSelector(state => state.carAttachment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="carAttachmentDetailsHeading">
          <Translate contentKey="rentcarappjhipApp.carAttachment.detail.title">CarAttachment</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{carAttachmentEntity.id}</dd>
          <dt>
            <span id="isMain">
              <Translate contentKey="rentcarappjhipApp.carAttachment.isMain">Is Main</Translate>
            </span>
          </dt>
          <dd>{carAttachmentEntity.isMain ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.carAttachment.car">Car</Translate>
          </dt>
          <dd>{carAttachmentEntity.car ? carAttachmentEntity.car.id : ''}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.carAttachment.attachment">Attachment</Translate>
          </dt>
          <dd>{carAttachmentEntity.attachment ? carAttachmentEntity.attachment.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/car-attachment" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/car-attachment/${carAttachmentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CarAttachmentDetail;
