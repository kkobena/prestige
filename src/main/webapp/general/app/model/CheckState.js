/* global Ext */

Ext.define('testextjs.model.CheckState', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'lg_CHECK_NUMERO',
            type: 'string'
        }, {
            name: 'USERNAME_EMETTEUR',
            type: 'string'
        }, {
            name: 'USERNAME_ENCAISSEUR',
            type: 'string'
        },
         {
            name: 'int_MONTANT',
            type: 'string'
        }
        , {
            name: 'int_MONTANT_PAYE',
            type: 'string'
        }
        
        , {
            name: 'int_MONTANT_RESTANT',
            type: 'string'
        },
        
        {
            name: 'str_BANQUE',
            type: 'string'
        },
        {
            name: 'str_LIEU',
            type: 'string'
        }, {
            name: 'str_STATUT',
            type: 'string'
        }
        , {
            name: 'str_TYPE_CHECK',
            type: 'string'
        }
        
        , {
            name: 'dt_TRANSACTION_DATE',
            type: 'string'
        }
        
        , {
            name: 'dt_CREATED',
            type: 'string'
        }
        , {
            name: 'dt_UPDATED',
            type: 'string'
        }
    ]
});
