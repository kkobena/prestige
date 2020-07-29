/* global Ext */

Ext.define('testextjs.model.statistics.QtyModel', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'CIP', type: 'string'},
        {name: 'DESC', type: 'string'},
        {name: 'ANNEE', type: 'number'},

        {name: 'JANUARY', type: 'number', defaultValue: 0},
        {name: 'FEBRUARY', type: 'number', defaultValue: 0},
        {name: 'MARCH', type: 'number', defaultValue: 0},
        {name: 'APRIL', type: 'number', defaultValue: 0},
        {name: 'MAY', type: 'number', defaultValue: 0},
        {name: 'JUNE', type: 'number', defaultValue: 0},
        {name: 'JULY', type: 'number', defaultValue: 0},
        {name: 'AUGUST', type: 'number', defaultValue: 0},
        {name: 'SEPTEMBER', type: 'number', defaultValue: 0},
        {name: 'OCTOBER', type: 'number', defaultValue: 0},
        {name: 'NOVEMBER', type: 'number', defaultValue: 0},
        {name: 'DECEMBER', type: 'number', defaultValue: 0}
    ]

});