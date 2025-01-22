import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './parametr.reducer';

export const ParametrDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const parametrEntity = useAppSelector(state => state.parametr.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="parametrDetailsHeading">
          <Translate contentKey="rentcarappjhipApp.parametr.detail.title">Parametr</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{parametrEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="rentcarappjhipApp.parametr.name">Name</Translate>
            </span>
          </dt>
          <dd>{parametrEntity.name}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="rentcarappjhipApp.parametr.status">Status</Translate>
            </span>
          </dt>
          <dd>{parametrEntity.status ? 'true' : 'false'}</dd>
        </dl>
        <Button tag={Link} to="/parametr" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/parametr/${parametrEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ParametrDetail;
