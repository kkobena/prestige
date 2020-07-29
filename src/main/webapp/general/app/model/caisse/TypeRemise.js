/* global Ext */

Ext.define('testextjs.model.caisse.TypeRemise', {
    extend: 'Ext.data.Model',
    idProperty: 'lgTYPEREMISEID',
    fields: [
        {
            name: 'lgTYPEREMISEID',
            type: 'string'
        },
        {
            name: 'strNAME',
            type: 'string'
        }
        ,
        {
            name: 'strDESCRIPTION',
            type: 'string'
        }
        ,{
            name:'remises',
            type:'auto'
        }
        

    ]

});
