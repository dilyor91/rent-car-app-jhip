import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './car-param.reducer';

export const CarParamDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const carParamEntity = useAppSelector(state => state.carParam.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="carParamDetailsHeading">
          <Translate contentKey="rentcarappjhipApp.carParam.detail.title">CarParam</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{carParamEntity.id}</dd>
          <dt>
            <span id="paramItemValue">
              <Translate contentKey="rentcarappjhipApp.carParam.paramItemValue">Param Item Value</Translate>
            </span>
          </dt>
          <dd>{carParamEntity.paramItemValue}</dd>
          <dt>
            <span id="paramValue">
              <Translate contentKey="rentcarappjhipApp.carParam.paramValue">Param Value</Translate>
            </span>
          </dt>
          <dd>{carParamEntity.paramValue}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.carParam.car">Car</Translate>
          </dt>
          <dd>{carParamEntity.car ? carParamEntity.car.id : ''}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.carParam.param">Param</Translate>
          </dt>
          <dd>{carParamEntity.param ? carParamEntity.param.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/car-param" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/car-param/${carParamEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CarParamDetail;
