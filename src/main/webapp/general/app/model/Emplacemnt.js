/* global Ext */

Ext.define('testextjs.model.Emplacemnt', {
    extend: 'Ext.data.Model',
    idProperty: 'lgEMPLACEMENTID',
    fields: [
        {
            name: 'lgEMPLACEMENTID',
            type: 'string'
        },
        {
            name: 'strNAME',
            type: 'string'
        }
       
    ]
});
