import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './car-template-param.reducer';

export const CarTemplateParamDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const carTemplateParamEntity = useAppSelector(state => state.carTemplateParam.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="carTemplateParamDetailsHeading">
          <Translate contentKey="rentcarappjhipApp.carTemplateParam.detail.title">CarTemplateParam</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{carTemplateParamEntity.id}</dd>
          <dt>
            <span id="paramVal">
              <Translate contentKey="rentcarappjhipApp.carTemplateParam.paramVal">Param Val</Translate>
            </span>
          </dt>
          <dd>{carTemplateParamEntity.paramVal}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.carTemplateParam.carTemplate">Car Template</Translate>
          </dt>
          <dd>{carTemplateParamEntity.carTemplate ? carTemplateParamEntity.carTemplate.id : ''}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.carTemplateParam.param">Param</Translate>
          </dt>
          <dd>{carTemplateParamEntity.param ? carTemplateParamEntity.param.id : ''}</dd>
          <dt>
            <Translate contentKey="rentcarappjhipApp.carTemplateParam.paramValue">Param Value</Translate>
          </dt>
          <dd>{carTemplateParamEntity.paramValue ? carTemplateParamEntity.paramValue.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/car-template-param" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/car-template-param/${carTemplateParamEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CarTemplateParamDetail;
