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
      <MenuItem icon="asterisk" to="/merchant-branch">
        <Translate contentKey="global.menu.entities.merchantBranch" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/merchant-role">
        <Translate contentKey="global.menu.entities.merchantRole" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/model">
        <Translate contentKey="global.menu.entities.model" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/color">
        <Translate contentKey="global.menu.entities.color" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/param">
        <Translate contentKey="global.menu.entities.param" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/param-value">
        <Translate contentKey="global.menu.entities.paramValue" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/translation">
        <Translate contentKey="global.menu.entities.translation" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/car">
        <Translate contentKey="global.menu.entities.car" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/car-param">
        <Translate contentKey="global.menu.entities.carParam" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/car-template">
        <Translate contentKey="global.menu.entities.carTemplate" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/car-template-param">
        <Translate contentKey="global.menu.entities.carTemplateParam" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/car-attachment">
        <Translate contentKey="global.menu.entities.carAttachment" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/model-attachment">
        <Translate contentKey="global.menu.entities.modelAttachment" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
