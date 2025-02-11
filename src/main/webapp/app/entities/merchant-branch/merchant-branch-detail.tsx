import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './merchant-branch.reducer';

export const MerchantBranchDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const merchantBranchEntity = useAppSelector(state => state.merchantBranch.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="merchantBranchDetailsHeading">
          <Translate contentKey="rentcarappjhipApp.merchantBranch.detail.title">MerchantBranch</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{merchantBranchEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="rentcarappjhipApp.merchantBranch.name">Name</Translate>
            </span>
          </dt>
          <dd>{merchantBranchEntity.name}</dd>
          <dt>
            <span id="address">
              <Translate contentKey="rentcarappjhipApp.merchantBranch.address">Address</Translate>
            </span>
          </dt>
          <dd>{merchantBranchEntity.address}</dd>
          <dt>
            <span id="latitude">
              <Translate contentKey="rentcarappjhipApp.merchantBranch.latitude">Latitude</Translate>
            </span>
          </dt>
          <dd>{merchantBranchEntity.latitude}</dd>
          <dt>
            <span id="longitude">
              <Translate contentKey="rentcarappjhipApp.merchantBranch.longitude">Longitude</Translate>
            </span>
          </dt>
          <dd>{merchantBranchEntity.longitude}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="rentcarappjhipApp.merchantBranch.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{merchantBranchEntity.phone}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.merchantBranch.merchant">Merchant</Translate>
          </dt>
          <dd>{merchantBranchEntity.merchant ? merchantBranchEntity.merchant.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/merchant-branch" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/merchant-branch/${merchantBranchEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default MerchantBranchDetail;
