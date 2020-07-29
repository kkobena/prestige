Ext.define('testextjs.model.DetailsBorderaux', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'lg_FACTURE_ID',
            type: 'string'
        }, {
            name: 'lg_FACTURE_DETAIL_ID',
            type: 'string'
        }, {
            name: 'dbl_MONTANT_PAYE',
            type: 'int'
        },
        {
            name: 'Amount',
            type: 'int'
        }, {
            name: 'dbl_MONTANT_PAYE',
            type: 'int'
        },
        {
            name: 'dbl_MONTANT_RESTANT',
            type: 'int'
        }
        ,
        {
            name: 'isChecked',
            type: 'boolean'
        },
        {
            name: 'str_REF',
            type: 'string'
        },
        {
            name: 'CLIENT_FULL_NAME',
            type: 'string'
        },
        {
            name: 'CLIENT_MATRICULE',
            type: 'string'
        }
, {
            name: 'int_NB_DOSSIER_RESTANT',
            type: 'string'
        }, {
            name: 'dt_DATE',
            type: 'string'
        }, {
            name: 'dt_HEURE',
            type: 'string'
        }



    ]
});
