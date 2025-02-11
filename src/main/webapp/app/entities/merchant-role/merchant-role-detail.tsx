import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './merchant-role.reducer';

export const MerchantRoleDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const merchantRoleEntity = useAppSelector(state => state.merchantRole.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="merchantRoleDetailsHeading">
          <Translate contentKey="rentcarappjhipApp.merchantRole.detail.title">MerchantRole</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{merchantRoleEntity.id}</dd>
          <dt>
            <span id="merchantRoleType">
              <Translate contentKey="rentcarappjhipApp.merchantRole.merchantRoleType">Merchant Role Type</Translate>
            </span>
          </dt>
          <dd>{merchantRoleEntity.merchantRoleType}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.merchantRole.user">User</Translate>
          </dt>
          <dd>{merchantRoleEntity.user ? merchantRoleEntity.user.id : ''}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.merchantRole.merchant">Merchant</Translate>
          </dt>
          <dd>{merchantRoleEntity.merchant ? merchantRoleEntity.merchant.id : ''}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.merchantRole.merchantBranch">Merchant Branch</Translate>
          </dt>
          <dd>{merchantRoleEntity.merchantBranch ? merchantRoleEntity.merchantBranch.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/merchant-role" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/merchant-role/${merchantRoleEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default MerchantRoleDetail;
