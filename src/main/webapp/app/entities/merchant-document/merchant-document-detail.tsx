import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './merchant-document.reducer';

export const MerchantDocumentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const merchantDocumentEntity = useAppSelector(state => state.merchantDocument.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="merchantDocumentDetailsHeading">
          <Translate contentKey="rentcarappjhipApp.merchantDocument.detail.title">MerchantDocument</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{merchantDocumentEntity.id}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.merchantDocument.merchant">Merchant</Translate>
          </dt>
          <dd>{merchantDocumentEntity.merchant ? merchantDocumentEntity.merchant.id : ''}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.merchantDocument.document">Document</Translate>
          </dt>
          <dd>{merchantDocumentEntity.document ? merchantDocumentEntity.document.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/merchant-document" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/merchant-document/${merchantDocumentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default MerchantDocumentDetail;
