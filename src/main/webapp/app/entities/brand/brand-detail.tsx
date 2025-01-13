import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './brand.reducer';

export const BrandDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const brandEntity = useAppSelector(state => state.brand.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="brandDetailsHeading">
          <Translate contentKey="rentcarappjhipApp.brand.detail.title">Brand</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{brandEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="rentcarappjhipApp.brand.name">Name</Translate>
            </span>
          </dt>
          <dd>{brandEntity.name}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="rentcarappjhipApp.brand.status">Status</Translate>
            </span>
          </dt>
          <dd>{brandEntity.status ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.brand.attachment">Attachment</Translate>
          </dt>
          <dd>{brandEntity.attachment ? brandEntity.attachment.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/brand" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/brand/${brandEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default BrandDetail;
