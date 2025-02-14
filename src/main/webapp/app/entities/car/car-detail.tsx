import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './car.reducer';

export const CarDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const carEntity = useAppSelector(state => state.car.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="carDetailsHeading">
          <Translate contentKey="rentcarappjhipApp.car.detail.title">Car</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{carEntity.id}</dd>
          <dt>
            <span id="stateNumberPlate">
              <Translate contentKey="rentcarappjhipApp.car.stateNumberPlate">State Number Plate</Translate>
            </span>
          </dt>
          <dd>{carEntity.stateNumberPlate}</dd>
          <dt>
            <span id="deposit">
              <Translate contentKey="rentcarappjhipApp.car.deposit">Deposit</Translate>
            </span>
          </dt>
          <dd>{carEntity.deposit}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.car.model">Model</Translate>
          </dt>
          <dd>{carEntity.model ? carEntity.model.id : ''}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.car.merchant">Merchant</Translate>
          </dt>
          <dd>{carEntity.merchant ? carEntity.merchant.id : ''}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.car.merchantBranch">Merchant Branch</Translate>
          </dt>
          <dd>{carEntity.merchantBranch ? carEntity.merchantBranch.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/car" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/car/${carEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CarDetail;
