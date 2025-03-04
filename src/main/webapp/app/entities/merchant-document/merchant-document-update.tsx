import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getMerchants } from 'app/entities/merchant/merchant.reducer';
import { getEntities as getDocuments } from 'app/entities/document/document.reducer';
import { createEntity, getEntity, reset, updateEntity } from './merchant-document.reducer';

export const MerchantDocumentUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const merchants = useAppSelector(state => state.merchant.entities);
  const documents = useAppSelector(state => state.document.entities);
  const merchantDocumentEntity = useAppSelector(state => state.merchantDocument.entity);
  const loading = useAppSelector(state => state.merchantDocument.loading);
  const updating = useAppSelector(state => state.merchantDocument.updating);
  const updateSuccess = useAppSelector(state => state.merchantDocument.updateSuccess);

  const handleClose = () => {
    navigate('/merchant-document');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getMerchants({}));
    dispatch(getDocuments({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }

    const entity = {
      ...merchantDocumentEntity,
      ...values,
      merchant: merchants.find(it => it.id.toString() === values.merchant?.toString()),
      document: documents.find(it => it.id.toString() === values.document?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...merchantDocumentEntity,
          merchant: merchantDocumentEntity?.merchant?.id,
          document: merchantDocumentEntity?.document?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="rentcarappjhipApp.merchantDocument.home.createOrEditLabel" data-cy="MerchantDocumentCreateUpdateHeading">
            <Translate contentKey="rentcarappjhipApp.merchantDocument.home.createOrEditLabel">Create or edit a MerchantDocument</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="merchant-document-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                id="merchant-document-merchant"
                name="merchant"
                data-cy="merchant"
                label={translate('rentcarappjhipApp.merchantDocument.merchant')}
                type="select"
              >
                <option value="" key="0" />
                {merchants
                  ? merchants.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField
                id="merchant-document-document"
                name="document"
                data-cy="document"
                label={translate('rentcarappjhipApp.merchantDocument.document')}
                type="select"
              >
                <option value="" key="0" />
                {documents
                  ? documents.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/merchant-document" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default MerchantDocumentUpdate;
