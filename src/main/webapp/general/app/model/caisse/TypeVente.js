/* global Ext */

Ext.define('testextjs.model.caisse.TypeVente', {
    extend: 'Ext.data.Model',
     idProperty: 'lgTYPEVENTEID',
    fields: [
        {
            name: 'lgTYPEVENTEID',
            type: 'string'
        },
        {
            name: 'strNAME',
            type: 'string'
        }
    ]
});
