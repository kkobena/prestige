/* global Ext */

Ext.define('testextjs.model.caisse.Remise', {
    extend: 'Ext.data.Model',
    idProperty: 'lgREMISEID',
    fields: [
        {
            name: 'lgREMISEID',
            type: 'string'
        },
        {
            name: 'strNAME',
            type: 'string'
        },
        {
            name: 'strCODE',
            type: 'string'
        },
        {
            name: 'dblTAUX',
            type: 'float'
        },
        {
            name: 'lgTYPEREMISEID',
            type: 'string'
        },
        {
            name: 'libelleType',
            type: 'string'
        }
    ]

});

