/* global Ext */

Ext.define('testextjs.model.caisse.User', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'lgUSERID',
            type: 'string'
        },
        {
            name: 'fullName',
            type: 'string'
        },
        {
            name: 'strFIRSTNAME',
            type: 'string'
        },
        {
            name: 'strLASTNAME',
            type: 'string'
        }


    ]
});
