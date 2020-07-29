/* global Ext */

Ext.define('testextjs.model.Balance', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'VENTE_BRUT',
            type: 'number'
        },
        {
            name: 'TOTAL_REMISE',
            type: 'number'
        },
        {
            name: 'VENTE_NET',
            type: 'number'
        },
        {
            name: 'str_TYPE_VENTE',
            type: 'string'
        },
        {
            name: 'NB',
            type: 'int'
        },
        {
            name: 'PART_TIERSPAYANT',
            type: 'number'
        },
        {
            name: 'PART_CLIENT',
            type: 'int'
        },
        {
            name: 'PANIER_MOYEN',
            type: 'int'
        },
        {
            name: 'POURCENTAGE',
            type: 'int'
        }
        ,
        {
            name: 'TOTAL_ESPECE',
            type: 'int'
        }
        ,
        {
            name: 'TOTAL_CHEQUE',
            type: 'number'
        }

        ,
        {
            name: 'TOTAL_CARTEBANCAIRE',
            type: 'number'
        },
        {
            name: 'TOTAL_DIFFERE',
            type: 'number'
        },
        {
            name: 'VENTE_NET_BIS',
            type: 'number'
        },
         {
            name: 'TOTALFOND',
            type: 'number'
        }, {
            name: 'TOTALDIF',
            type: 'number'
        },
        {
            name: 'TOTALENTRE',
            type: 'number'
        }, {
            name: 'TOTALSORTIE',
            type: 'number'
        }, {
            name: 'TOTALTIERP',
            type: 'number'
        }, 
        
        {
            name: 'TOTALVENTE',
            type: 'number'
        }, 
        {
            name: 'TOTALACHAT',
            type: 'number'
        }, 
        {
            name: 'TOTALMARGE',
            type: 'number'
        }, 
        {
            name: 'TOTALRATIO',
            type: 'number'
        },
        {
            name: 'TOTALANNULEESP',
            type: 'number'
        },
        {
            name: 'TOTALANNULE',
            type: 'number'
        }
        
    ]
});
