import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './param-value.reducer';

export const ParamValueDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const paramValueEntity = useAppSelector(state => state.paramValue.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="paramValueDetailsHeading">
          <Translate contentKey="rentcarappjhipApp.paramValue.detail.title">ParamValue</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{paramValueEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="rentcarappjhipApp.paramValue.name">Name</Translate>
            </span>
          </dt>
          <dd>{paramValueEntity.name}</dd>
          <dt>
            <span id="status">
              <Translate contentKey="rentcarappjhipApp.paramValue.status">Status</Translate>
            </span>
          </dt>
          <dd>{paramValueEntity.status ? 'true' : 'false'}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.paramValue.param">Param</Translate>
          </dt>
          <dd>{paramValueEntity.param ? paramValueEntity.param.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/param-value" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/param-value/${paramValueEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ParamValueDetail;
