/* global Ext */

Ext.define('testextjs.model.LOTS', {
    extend: 'Ext.data.Model',
    idProperty: 'lg_LOT_ID',
    fields: [
        {name: 'lg_LOT_ID', type: 'string'},
        {name: 'CIP', type: 'string'},
        {name: 'LIBELLE', type: 'string'},
        {name: 'NUMLOT', type: 'string'},
        {name: 'REFBL', type: 'string'},
        {name: 'NUMBER', type: 'int'},
        {name: 'NUMBERGT', type: 'int'},
        {name: 'GROSSISTE', type: 'string'},
        {name: 'DATESORTIE', type: 'string'},
        {name: 'GROSSISTE', type: 'string'},
        {name: 'REFCMDE', type: 'string'},
        {name: 'DATEPEREMPTION', type: 'string'},
        {name: 'ETIQUETTE', type: 'string'}
       


    ]
});


