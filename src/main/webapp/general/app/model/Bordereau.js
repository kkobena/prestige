/* global Ext */

Ext.define('testextjs.model.Bordereau', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'lg_BORDEREAU_ID',
            type: 'string'
        }, {
            name: 'str_CODE_BORDEREAU',
            type: 'string'
        }, {
            name: 'dbl_MONTANT',
            type: 'string'
        }, {
            name: 'int_nb_FACTURE',
            type: 'string'
        }, {
            name: 'dbl_MONTANT_RESTANT',
            type: 'string'
        },
        {
            name: 'str_STATUT',
            type: 'string'
        }, {
            name: 'dbl_MONTANT_PAYE',
            type: 'string'
        }, {
            name: 'dt_CREATED',
            type: 'string'
        }
        , {
            name: 'dt_UPDATED',
            type: 'string'
        }
    ]
});
