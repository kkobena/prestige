/* global Ext */

Ext.define('testextjs.model.ReferenceVente', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'str_REF',
            type: 'string'
        },{
            name: 'str_TYPE_VENTE',
            type: 'string'
        },{
            name: 'str_STATUT_FACTURE',
            type: 'string'
        },{
            name: 'str_DESCRIPTION',
            type: 'string'
        }
        
    ]
});

