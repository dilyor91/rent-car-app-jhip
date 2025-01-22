import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './car-body.reducer';

export const CarBodyDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const carBodyEntity = useAppSelector(state => state.carBody.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="carBodyDetailsHeading">
          <Translate contentKey="rentcarappjhipApp.carBody.detail.title">CarBody</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{carBodyEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="rentcarappjhipApp.carBody.name">Name</Translate>
            </span>
          </dt>
          <dd>{carBodyEntity.name}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="rentcarappjhipApp.carBody.status">Status</Translate>
            </span>
          </dt>
          <dd>{carBodyEntity.status ? 'true' : 'false'}</dd>
        </dl>
        <Button tag={Link} to="/car-body" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/car-body/${carBodyEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CarBodyDetail;
