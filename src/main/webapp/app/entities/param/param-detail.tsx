import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './param.reducer';

export const ParamDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const paramEntity = useAppSelector(state => state.param.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="paramDetailsHeading">
          <Translate contentKey="rentcarappjhipApp.param.detail.title">Param</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{paramEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="rentcarappjhipApp.param.name">Name</Translate>
            </span>
          </dt>
          <dd>{paramEntity.name}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="rentcarappjhipApp.param.description">Description</Translate>
            </span>
          </dt>
          <dd>{paramEntity.description}</dd>
          <dt>
            <span id="fieldType">
              <Translate contentKey="rentcarappjhipApp.param.fieldType">Field Type</Translate>
            </span>
          </dt>
          <dd>{paramEntity.fieldType}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="rentcarappjhipApp.param.status">Status</Translate>
            </span>
          </dt>
          <dd>{paramEntity.status ? 'true' : 'false'}</dd>
          <dt>
            <span id="isDefault">
              <Translate contentKey="rentcarappjhipApp.param.isDefault">Is Default</Translate>
            </span>
          </dt>
          <dd>{paramEntity.isDefault ? 'true' : 'false'}</dd>
        </dl>
        <Button tag={Link} to="/param" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/param/${paramEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ParamDetail;
