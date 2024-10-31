import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './merchant.reducer';

export const MerchantDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const merchantEntity = useAppSelector(state => state.merchant.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="merchantDetailsHeading">
          <Translate contentKey="rentcarappjhipApp.merchant.detail.title">Merchant</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{merchantEntity.id}</dd>
          <dt>
            <span id="companyName">
              <Translate contentKey="rentcarappjhipApp.merchant.companyName">Company Name</Translate>
            </span>
          </dt>
          <dd>{merchantEntity.companyName}</dd>
          <dt>
            <span id="brandName">
              <Translate contentKey="rentcarappjhipApp.merchant.brandName">Brand Name</Translate>
            </span>
          </dt>
          <dd>{merchantEntity.brandName}</dd>
          <dt>
            <span id="inn">
              <Translate contentKey="rentcarappjhipApp.merchant.inn">Inn</Translate>
            </span>
          </dt>
          <dd>{merchantEntity.inn}</dd>
          <dt>
            <span id="owner">
              <Translate contentKey="rentcarappjhipApp.merchant.owner">Owner</Translate>
            </span>
          </dt>
          <dd>{merchantEntity.owner}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="rentcarappjhipApp.merchant.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{merchantEntity.phone}</dd>
          <dt>
            <span id="address">
              <Translate contentKey="rentcarappjhipApp.merchant.address">Address</Translate>
            </span>
          </dt>
          <dd>{merchantEntity.address}</dd>
        </dl>
        <Button tag={Link} to="/merchant" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/merchant/${merchantEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default MerchantDetail;
