/* global Ext */

Ext.define('testextjs.model.DetailsVentesClient', {
    extend: 'Ext.data.Model',
    fields: [
         {
            name: 'ID',
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
            name: 'MONTANTVENTE',
            type: 'number'
        },
        {
            name: 'PU',
            type: 'number'
        },
        
         {
            name: 'QTY',
            type: 'int'
        }
      
        
        
    ]
});

