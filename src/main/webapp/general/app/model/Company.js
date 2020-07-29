/* global Ext */

Ext.define('testextjs.model.Company', {
    extend: 'Ext.data.Model',
    idProperty: 'lg_COMPANY_ID',
    fields: [
        {
            name: 'lg_COMPANY_ID',
            type: 'string'
        },
        {
            name: 'str_RAISONSOCIALE',
            type: 'string'
        },
        {
            name: 'str_ADRESS',
            type: 'string'
        },
        {
            name: 'str_PHONE',
            type: 'string'
        },
        {
            name: 'str_CEL',
            type: 'string'
        }
       
    ]
});
