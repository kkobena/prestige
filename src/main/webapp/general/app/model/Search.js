/* global Ext */

Ext.define('testextjs.model.Search', {
    extend: 'Ext.data.Model',
    idProperty: 'lg_FAMILLE_ID',
    fields: [
        {name: 'lg_FAMILLE_ID', type: 'string'},
        {name: 'str_DESCRIPTION', type: 'string'},
        {name: 'CIP', type: 'string'},
        {name: 'int_NUMBER_AVAILABLE', type: 'number'},
        {name: 'int_NUMBER', type: 'number'},
        {name: 'int_PRICE', type: 'number'},
        {name: 'int_PAF', type: 'number'},
        {name: 'str_DESCRIPTION', type: 'string'},
         {name: 'str_DESCRIPTION_PLUS', type: 'string'},
        

        {name: 'lg_ZONE_GEO_ID', type: 'string'}

    ]
});


