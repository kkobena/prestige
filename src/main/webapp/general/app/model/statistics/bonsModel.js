/* global Ext */

Ext.define('testextjs.model.statistics.bonsModel', {
    extend: 'Ext.data.Model',
    idProperty: 'lg_PCMT_ID',
    fields: [
        {name: 'lg_PCMT_ID', type: 'string'},
        {name: 'REFBON', type: 'string'},git
        {name: 'AMOUNT', type: 'number'},
         {name: 'AMOUNT_VENTE', type: 'number'},
          {name: 'CLIENT_FULLNAME', type: 'string'},
           {name: 'DATE_VENTE', type: 'string'},
        {name: 'dtUPDATED', type: 'string'},
        {name: 'isChecked', type: 'boolean'}
    ]
});


