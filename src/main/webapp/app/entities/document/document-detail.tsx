import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './document.reducer';

export const DocumentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const documentEntity = useAppSelector(state => state.document.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="documentDetailsHeading">
          <Translate contentKey="rentcarappjhipApp.document.detail.title">Document</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{documentEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="rentcarappjhipApp.document.name">Name</Translate>
            </span>
          </dt>
          <dd>{documentEntity.name}</dd>
          <dt>
            <span id="docType">
              <Translate contentKey="rentcarappjhipApp.document.docType">Doc Type</Translate>
            </span>
          </dt>
          <dd>{documentEntity.docType}</dd>
          <dt>
            <span id="givenDate">
              <Translate contentKey="rentcarappjhipApp.document.givenDate">Given Date</Translate>
            </span>
          </dt>
          <dd>{documentEntity.givenDate ? <TextFormat value={documentEntity.givenDate} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="issuedDate">
              <Translate contentKey="rentcarappjhipApp.document.issuedDate">Issued Date</Translate>
            </span>
          </dt>
          <dd>
            {documentEntity.issuedDate ? <TextFormat value={documentEntity.issuedDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="docStatus">
              <Translate contentKey="rentcarappjhipApp.document.docStatus">Doc Status</Translate>
            </span>
          </dt>
          <dd>{documentEntity.docStatus ? 'true' : 'false'}</dd>
        </dl>
        <Button tag={Link} to="/document" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/document/${documentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default DocumentDetail;
