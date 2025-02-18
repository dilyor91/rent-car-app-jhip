import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './car-template.reducer';

export const CarTemplateDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const carTemplateEntity = useAppSelector(state => state.carTemplate.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="carTemplateDetailsHeading">
          <Translate contentKey="rentcarappjhipApp.carTemplate.detail.title">CarTemplate</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{carTemplateEntity.id}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="rentcarappjhipApp.carTemplate.status">Status</Translate>
            </span>
          </dt>
          <dd>{carTemplateEntity.status ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.carTemplate.model">Model</Translate>
          </dt>
          <dd>{carTemplateEntity.model ? carTemplateEntity.model.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/car-template" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/car-template/${carTemplateEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CarTemplateDetail;
