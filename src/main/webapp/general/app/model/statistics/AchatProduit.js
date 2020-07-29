/* global Ext */

Ext.define('testextjs.model.statistics.AchatProduit', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'ANNEE', type: 'string'},
        {name: 'LIBELLE', type: 'string'},
        {name: 'JANVIER', type: 'float'},
        {name: 'FEVRIER', type: 'float'},
        {name: 'MARS', type: 'float'},
        {name: 'AVRIL', type: 'float'},
        {name: 'MAI', type: 'float'},
        {name: 'JUIN', type: 'float'},
        {name: 'JUIELLET', type: 'float'},
        {name: 'AOUT', type: 'float'},
        {name: 'SEPT', type: 'float'},
        {name: 'OCT', type: 'float'},
        {name: 'NOV', type: 'float'},
        {name: 'DEC', type: 'float'}
    ]
});

