import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './translation.reducer';

export const TranslationDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const translationEntity = useAppSelector(state => state.translation.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="translationDetailsHeading">
          <Translate contentKey="rentcarappjhipApp.translation.detail.title">Translation</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{translationEntity.id}</dd>
          <dt>
            <span id="entityType">
              <Translate contentKey="rentcarappjhipApp.translation.entityType">Entity Type</Translate>
            </span>
          </dt>
          <dd>{translationEntity.entityType}</dd>
          <dt>
            <span id="entityId">
              <Translate contentKey="rentcarappjhipApp.translation.entityId">Entity Id</Translate>
            </span>
          </dt>
          <dd>{translationEntity.entityId}</dd>
          <dt>
            <span id="lang">
              <Translate contentKey="rentcarappjhipApp.translation.lang">Lang</Translate>
            </span>
          </dt>
          <dd>{translationEntity.lang}</dd>
          <dt>
            <span id="value">
              <Translate contentKey="rentcarappjhipApp.translation.value">Value</Translate>
            </span>
          </dt>
          <dd>{translationEntity.value}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="rentcarappjhipApp.translation.description">Description</Translate>
            </span>
          </dt>
          <dd>{translationEntity.description}</dd>
        </dl>
        <Button tag={Link} to="/translation" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/translation/${translationEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default TranslationDetail;
