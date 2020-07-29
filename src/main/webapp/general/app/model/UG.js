/* global Ext */

Ext.define('testextjs.model.UG', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields: [{
            name: 'id',
            type: 'string'
        },
        {
            name: 'CIP',
            type: 'string'
        },

        {
            name: 'NAME',
            type: 'string'
        },
        {
            name: 'BLREF',
            type: 'string'
        },
        {
            name: 'PRIXVENTE',
            type: 'string'
        },

        {
            name: 'QTY',
            type: 'string'
        },
        {
            name: 'QTYINI',
            type: 'string'
        },
        {
            name: 'VALEURQTYINI',
            type: 'string'
        },
        {
            name: 'VALEURVENTE',
            type: 'string'
        },
        {
            name: 'GROSSISTE',
            type: 'string'
        }
        ,
        {
            name: 'TOTALAMONT',
            type: 'number'
        },
        {
            name: 'TOTALQTY',
            type: 'number'
        },
        {
            name: 'TOTALAMONTINI',
            type: 'number'
        },
        {
            name: 'TOTALQTYINI',
            type: 'number'
        },
        {
            name: 'PRIXACHAT',
            type: 'number'
        }


    ]
});

