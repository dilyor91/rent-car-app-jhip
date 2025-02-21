import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Col, Form, FormGroup, Input, InputGroup, Row, Table } from 'reactstrap';
import { TextFormat, Translate, getSortState, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC } from 'app/shared/util/pagination.constants';
import { overrideSortStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities, searchEntities } from './car-mileage.reducer';

export const CarMileage = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');
  const [sortState, setSortState] = useState(overrideSortStateWithQueryParams(getSortState(pageLocation, 'id'), pageLocation.search));

  const carMileageList = useAppSelector(state => state.carMileage.entities);
  const loading = useAppSelector(state => state.carMileage.loading);

  const getAllEntities = () => {
    if (search) {
      dispatch(
        searchEntities({
          query: search,
          sort: `${sortState.sort},${sortState.order}`,
        }),
      );
    } else {
      dispatch(
        getEntities({
          sort: `${sortState.sort},${sortState.order}`,
        }),
      );
    }
  };

  const startSearching = e => {
    if (search) {
      dispatch(
        searchEntities({
          query: search,
          sort: `${sortState.sort},${sortState.order}`,
        }),
      );
    }
    e.preventDefault();
  };

  const clear = () => {
    setSearch('');
    dispatch(getEntities({}));
  };

  const handleSearch = event => setSearch(event.target.value);

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?sort=${sortState.sort},${sortState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [sortState.order, sortState.sort, search]);

  const sort = p => () => {
    setSortState({
      ...sortState,
      order: sortState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = sortState.sort;
    const order = sortState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="car-mileage-heading" data-cy="CarMileageHeading">
        <Translate contentKey="rentcarappjhipApp.carMileage.home.title">Car Mileages</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="rentcarappjhipApp.carMileage.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/car-mileage/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="rentcarappjhipApp.carMileage.home.createLabel">Create new Car Mileage</Translate>
          </Link>
        </div>
      </h2>
      <Row>
        <Col sm="12">
          <Form onSubmit={startSearching}>
            <FormGroup>
              <InputGroup>
                <Input
                  type="text"
                  name="search"
                  defaultValue={search}
                  onChange={handleSearch}
                  placeholder={translate('rentcarappjhipApp.carMileage.home.search')}
                />
                <Button className="input-group-addon">
                  <FontAwesomeIcon icon="search" />
                </Button>
                <Button type="reset" className="input-group-addon" onClick={clear}>
                  <FontAwesomeIcon icon="trash" />
                </Button>
              </InputGroup>
            </FormGroup>
          </Form>
        </Col>
      </Row>
      <div className="table-responsive">
        {carMileageList && carMileageList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="rentcarappjhipApp.carMileage.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('value')}>
                  <Translate contentKey="rentcarappjhipApp.carMileage.value">Value</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('value')} />
                </th>
                <th className="hand" onClick={sort('unit')}>
                  <Translate contentKey="rentcarappjhipApp.carMileage.unit">Unit</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('unit')} />
                </th>
                <th className="hand" onClick={sort('date')}>
                  <Translate contentKey="rentcarappjhipApp.carMileage.date">Date</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('date')} />
                </th>
                <th>
                  <Translate contentKey="rentcarappjhipApp.carMileage.car">Car</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {carMileageList.map((carMileage, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/car-mileage/${carMileage.id}`} color="link" size="sm">
                      {carMileage.id}
                    </Button>
                  </td>
                  <td>{carMileage.value}</td>
                  <td>
                    <Translate contentKey={`rentcarappjhipApp.MileageEnum.${carMileage.unit}`} />
                  </td>
                  <td>{carMileage.date ? <TextFormat type="date" value={carMileage.date} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{carMileage.car ? <Link to={`/car/${carMileage.car.id}`}>{carMileage.car.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/car-mileage/${carMileage.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/car-mileage/${carMileage.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() => (window.location.href = `/car-mileage/${carMileage.id}/delete`)}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="rentcarappjhipApp.carMileage.home.notFound">No Car Mileages found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default CarMileage;
