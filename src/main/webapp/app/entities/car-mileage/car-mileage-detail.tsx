import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './car-mileage.reducer';

export const CarMileageDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const carMileageEntity = useAppSelector(state => state.carMileage.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="carMileageDetailsHeading">
          <Translate contentKey="rentcarappjhipApp.carMileage.detail.title">CarMileage</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{carMileageEntity.id}</dd>
          <dt>
            <span id="value">
              <Translate contentKey="rentcarappjhipApp.carMileage.value">Value</Translate>
            </span>
          </dt>
          <dd>{carMileageEntity.value}</dd>
          <dt>
            <span id="unit">
              <Translate contentKey="rentcarappjhipApp.carMileage.unit">Unit</Translate>
            </span>
          </dt>
          <dd>{carMileageEntity.unit}</dd>
          <dt>
            <span id="date">
              <Translate contentKey="rentcarappjhipApp.carMileage.date">Date</Translate>
            </span>
          </dt>
          <dd>{carMileageEntity.date ? <TextFormat value={carMileageEntity.date} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.carMileage.car">Car</Translate>
          </dt>
          <dd>{carMileageEntity.car ? carMileageEntity.car.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/car-mileage" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/car-mileage/${carMileageEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CarMileageDetail;
