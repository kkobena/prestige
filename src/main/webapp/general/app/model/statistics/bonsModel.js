/* global Ext */

Ext.define('testextjs.model.statistics.bonsModel', {
    extend: 'Ext.data.Model',
    idProperty: 'lg_PCMT_ID',
    fields: [
        {name: 'lg_PCMT_ID', type: 'string'},
        {name: 'REFBON', type: 'string'},
        {name: 'AMOUNT', type: 'number'},
        
        {name: 'dtUPDATED', type: 'string'},
        {name: 'isChecked', type: 'boolean'}
    ]
});


