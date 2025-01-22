import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './car-class.reducer';

export const CarClassDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const carClassEntity = useAppSelector(state => state.carClass.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="carClassDetailsHeading">
          <Translate contentKey="rentcarappjhipApp.carClass.detail.title">CarClass</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{carClassEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="rentcarappjhipApp.carClass.name">Name</Translate>
            </span>
          </dt>
          <dd>{carClassEntity.name}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="rentcarappjhipApp.carClass.status">Status</Translate>
            </span>
          </dt>
          <dd>{carClassEntity.status ? 'true' : 'false'}</dd>
        </dl>
        <Button tag={Link} to="/car-class" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/car-class/${carClassEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CarClassDetail;
