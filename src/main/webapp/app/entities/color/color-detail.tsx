import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './color.reducer';

export const ColorDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const colorEntity = useAppSelector(state => state.color.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="colorDetailsHeading">
          <Translate contentKey="rentcarappjhipApp.color.detail.title">Color</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{colorEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="rentcarappjhipApp.color.name">Name</Translate>
            </span>
          </dt>
          <dd>{colorEntity.name}</dd>
          <dt>
            <span id="hex">
              <Translate contentKey="rentcarappjhipApp.color.hex">Hex</Translate>
            </span>
          </dt>
          <dd>{colorEntity.hex}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="rentcarappjhipApp.color.status">Status</Translate>
            </span>
          </dt>
          <dd>{colorEntity.status ? 'true' : 'false'}</dd>
        </dl>
        <Button tag={Link} to="/color" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/color/${colorEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ColorDetail;
