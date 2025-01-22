import React from 'react';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/merchant">
        <Translate contentKey="global.menu.entities.merchant" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/attachment">
        <Translate contentKey="global.menu.entities.attachment" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/brand">
        <Translate contentKey="global.menu.entities.brand" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/car-body">
        <Translate contentKey="global.menu.entities.carBody" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/category">
        <Translate contentKey="global.menu.entities.category" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/car-class">
        <Translate contentKey="global.menu.entities.carClass" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/vehicle">
        <Translate contentKey="global.menu.entities.vehicle" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/parametr">
        <Translate contentKey="global.menu.entities.parametr" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
