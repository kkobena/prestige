/* global Ext */

Ext.define('testextjs.model.VenteClient', {
    extend: 'Ext.data.Model',
    fields: [
         {
            name: 'IDVENTE',
            type: 'string'
        },
        {
            name: 'REFVENTE',
            type: 'string'
        },
        {
            name: 'DATEVENTE',
            type: 'string'
        },
        {
            name: 'MONTANTVENTE',
            type: 'string'
        },
        {
            name: 'MONTANTCLIENT',
            type: 'number'
        },
        
        {
            name: 'MONTANTTP',
            type: 'number'
        },
        {
            name: 'POURCENTAGE',
            type: 'int'
        },
         {
            name: 'REFFACTURE',
            type: 'string'
        },
        
        {
            name: 'TIERSPAYANT',
            type: 'string'
        }
        ,
        {
            name: 'TOTALCLIENT',
            type: 'number'
        },
        
        {
            name: 'TOTALTTP',
            type: 'number'
        },
        {
            name: 'TOTALVENTE',
            type: 'number'
        },
        {
            name: 'REFBON',
            type: 'string'
        }
      
        
        
    ]
});

