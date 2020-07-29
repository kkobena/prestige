/* global Ext */

Ext.define('testextjs.model.TableauBordPharmacien', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'DATEOPERATION', type: 'string'},
        {name: 'COMPTANT', type: 'float'},
        {name: 'CREDIT', type: 'float'},
        {name: 'REMISE', type: 'float'},
        {name: 'CA Net', type: 'float'},
        {name: 'NOMBRE CLIENTS', type: 'float'},
        {name: 'LABOREX', type: 'float'},
        {name: 'DPCI', type: 'float'},
        {name: 'COPHARMED', type: 'float'},
        {name: 'TEDIS PHARMA', type: 'float'},
        {name: 'AUTRES', type: 'float'},
        {name: 'AVOIRS', type: 'float'},
        {name: 'ACHATS Nets', type: 'float'},
        {name: 'RATIOS', type: 'float'},
        {name: 'TOTALREMISE', type: 'float'},
        {name: 'TOTALCANET', type: 'float'},
        {name: 'TOTALACHATSNET', type: 'float'},
        {name: 'TOTALCLIENTS', type: 'float'},
        {name: 'RATIOVA', type: 'float'},
        {name: 'RATIOACHV', type: 'float'},
        {name: 'RATIOSAV', type: 'float'}
        


    ]
});

