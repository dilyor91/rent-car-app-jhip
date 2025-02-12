import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { LanguageEnum } from 'app/shared/model/enumerations/language-enum.model';
import { createEntity, getEntity, reset, updateEntity } from './translation.reducer';

export const TranslationUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const translationEntity = useAppSelector(state => state.translation.entity);
  const loading = useAppSelector(state => state.translation.loading);
  const updating = useAppSelector(state => state.translation.updating);
  const updateSuccess = useAppSelector(state => state.translation.updateSuccess);
  const languageEnumValues = Object.keys(LanguageEnum);

  const handleClose = () => {
    navigate(`/translation${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
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
    if (values.entityId !== undefined && typeof values.entityId !== 'number') {
      values.entityId = Number(values.entityId);
    }

    const entity = {
      ...translationEntity,
      ...values,
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
          lang: 'UZ',
          ...translationEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="rentcarappjhipApp.translation.home.createOrEditLabel" data-cy="TranslationCreateUpdateHeading">
            <Translate contentKey="rentcarappjhipApp.translation.home.createOrEditLabel">Create or edit a Translation</Translate>
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
                  id="translation-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('rentcarappjhipApp.translation.entityType')}
                id="translation-entityType"
                name="entityType"
                data-cy="entityType"
                type="text"
              />
              <ValidatedField
                label={translate('rentcarappjhipApp.translation.entityId')}
                id="translation-entityId"
                name="entityId"
                data-cy="entityId"
                type="text"
              />
              <ValidatedField
                label={translate('rentcarappjhipApp.translation.lang')}
                id="translation-lang"
                name="lang"
                data-cy="lang"
                type="select"
              >
                {languageEnumValues.map(languageEnum => (
                  <option value={languageEnum} key={languageEnum}>
                    {translate(`rentcarappjhipApp.LanguageEnum.${languageEnum}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('rentcarappjhipApp.translation.value')}
                id="translation-value"
                name="value"
                data-cy="value"
                type="text"
              />
              <ValidatedField
                label={translate('rentcarappjhipApp.translation.description')}
                id="translation-description"
                name="description"
                data-cy="description"
                type="text"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/translation" replace color="info">
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

export default TranslationUpdate;
