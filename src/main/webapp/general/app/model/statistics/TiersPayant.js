/* global Ext */

Ext.define('testextjs.model.statistics.TiersPayant', {
    extend: 'Ext.data.Model',
    idProperty: 'lg_TIERS_PAYANT_ID',
    fields: [
        {name: 'lg_TIERS_PAYANT_ID', type: 'string'},
        {name: 'str_FULLNAME', type: 'string'}
]

});