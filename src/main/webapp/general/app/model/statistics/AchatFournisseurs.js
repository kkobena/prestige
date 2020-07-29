/* global Ext */

Ext.define('testextjs.model.statistics.AchatFournisseurs', {
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
        {name: 'OCT', type: 'float'},
        {name: 'SET', type: 'float'},
        {name: 'NOV', type: 'float'},
        {name: 'DEC', type: 'float'},
        {name: 'JANVIER_AVOIR', type: 'float'},
        {name: 'FEVRIER_AVOIR', type: 'float'},
        {name: 'SET_AVOIR', type: 'float'},
        {name: 'MARS_AVOIR', type: 'float'},
        {name: 'AVRIL_AVOIR', type: 'float'},
        {name: 'MAI_AVOIR', type: 'float'},
        {name: 'JUIN_AVOIR', type: 'float'},
        {name: 'JUIELLET_AVOIR', type: 'float'},
        {name: 'AOUT_AVOIR', type: 'float'},
        {name: 'OCT_AVOIR', type: 'float'},
        {name: 'NOV_AVOIR', type: 'float'},
        {name: 'DEC_AVOIR', type: 'float'}
    ]
});

