import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getCars } from 'app/entities/car/car.reducer';
import { MileageEnum } from 'app/shared/model/enumerations/mileage-enum.model';
import { createEntity, getEntity, reset, updateEntity } from './car-mileage.reducer';

export const CarMileageUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const cars = useAppSelector(state => state.car.entities);
  const carMileageEntity = useAppSelector(state => state.carMileage.entity);
  const loading = useAppSelector(state => state.carMileage.loading);
  const updating = useAppSelector(state => state.carMileage.updating);
  const updateSuccess = useAppSelector(state => state.carMileage.updateSuccess);
  const mileageEnumValues = Object.keys(MileageEnum);

  const handleClose = () => {
    navigate('/car-mileage');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getCars({}));
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
    if (values.value !== undefined && typeof values.value !== 'number') {
      values.value = Number(values.value);
    }
    values.date = convertDateTimeToServer(values.date);

    const entity = {
      ...carMileageEntity,
      ...values,
      car: cars.find(it => it.id.toString() === values.car?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          date: displayDefaultDateTime(),
        }
      : {
          unit: 'KILOMETRES',
          ...carMileageEntity,
          date: convertDateTimeFromServer(carMileageEntity.date),
          car: carMileageEntity?.car?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="rentcarappjhipApp.carMileage.home.createOrEditLabel" data-cy="CarMileageCreateUpdateHeading">
            <Translate contentKey="rentcarappjhipApp.carMileage.home.createOrEditLabel">Create or edit a CarMileage</Translate>
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
                  id="car-mileage-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('rentcarappjhipApp.carMileage.value')}
                id="car-mileage-value"
                name="value"
                data-cy="value"
                type="text"
              />
              <ValidatedField
                label={translate('rentcarappjhipApp.carMileage.unit')}
                id="car-mileage-unit"
                name="unit"
                data-cy="unit"
                type="select"
              >
                {mileageEnumValues.map(mileageEnum => (
                  <option value={mileageEnum} key={mileageEnum}>
                    {translate(`rentcarappjhipApp.MileageEnum.${mileageEnum}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('rentcarappjhipApp.carMileage.date')}
                id="car-mileage-date"
                name="date"
                data-cy="date"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
              />
              <ValidatedField
                id="car-mileage-car"
                name="car"
                data-cy="car"
                label={translate('rentcarappjhipApp.carMileage.car')}
                type="select"
              >
                <option value="" key="0" />
                {cars
                  ? cars.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/car-mileage" replace color="info">
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

export default CarMileageUpdate;
