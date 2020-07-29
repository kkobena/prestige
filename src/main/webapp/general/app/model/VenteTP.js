/* global Ext */

Ext.define('testextjs.model.VenteTP', {
    extend: 'Ext.data.Model',
    fields: [
         {
            name: 'TPNAME',
            type: 'string'
        },
        {
            name: 'CODE',
            type: 'string'
        },
        {
            name: 'NBBON',
            type: 'int'
        },
        {
            name: 'MONTANTVENTE',
            type: 'number'
        },
         {
            name: 'TOTALBON',
            type: 'number'
        },
         {
            name: 'TOTALMONTANT',
            type: 'number'
        }
        
    ]
});



