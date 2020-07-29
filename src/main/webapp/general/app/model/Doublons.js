/* global Ext */

Ext.define('testextjs.model.Doublons', {
    extend: 'Ext.data.Model',
    fields: [
         {
            name: 'lg_FAMILLE_ID',
            type: 'string'
        },
        {
            name: 'lg_FAMILLESTOCK_ID',
            type: 'string'
        },
        {
            name: 'str_LIB',
            type: 'string'
        },
        {
            name: 'CIP',
            type: 'string'
        },
        {
            name: 'STOCK',
            type: 'int'
        },
        {
            name: 'PU',
            type: 'number'
        },
        {
            name: 'PA',
            type: 'number'
        }
        ,{
            name: 'DATECREATION',
            type: 'string'
        }
        ,{
            name: 'DATEVENTE',
            type: 'string'
        },
        {
            name: 'DATEENTREE',
            type: 'string'
        },
        {
            name: 'DATEINVENTAIRE',
            type: 'string'
        }
        
    ]
});


