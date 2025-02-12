import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './model.reducer';

export const ModelDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const modelEntity = useAppSelector(state => state.model.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="modelDetailsHeading">
          <Translate contentKey="rentcarappjhipApp.model.detail.title">Model</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{modelEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="rentcarappjhipApp.model.name">Name</Translate>
            </span>
          </dt>
          <dd>{modelEntity.name}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="rentcarappjhipApp.model.status">Status</Translate>
            </span>
          </dt>
          <dd>{modelEntity.status ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.model.brand">Brand</Translate>
          </dt>
          <dd>{modelEntity.brand ? modelEntity.brand.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/model" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/model/${modelEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ModelDetail;
