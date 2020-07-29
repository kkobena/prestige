/* global Ext */

Ext.define('testextjs.model.statistics.RuptureStock', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'str_LIBELLE', type: 'string'},
        {name: 'CODECIP', type: 'string'},
        {name: 'CODEGESTION', type: 'string'},
        {name: 'QTEREAP', type: 'int'},
        {name: 'SEUILREAP', type: 'int'},
        {name: 'Nombre Fois', type: 'int'},
        {name: 'Quantite', type: 'int'},
        {name: 'QTEPROPOSE', type: 'int'},
        {name: 'SEUILPROPOSE', type: 'int'}
 ]
});
