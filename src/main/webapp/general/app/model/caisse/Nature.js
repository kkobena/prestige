/* global Ext */

Ext.define('testextjs.model.caisse.Nature', {
    extend: 'Ext.data.Model',
     idProperty: 'lgNATUREVENTEID',
    fields: [
        {
            name: 'lgNATUREVENTEID',
            type: 'string'
        },
        {
            name: 'strLIBELLE',
            type: 'string'
        }



    ]
});
