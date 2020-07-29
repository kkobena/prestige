Ext.define('testextjs.model.DossierFacture', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'lg_DOSSIER_FACTURE_ID', 
            type: 'string'
        }, {
            name: 'str_NUM_DOSSIER',
            type: 'string'
        }, {
            name: 'lg_PREENREGISTREMENT_ID',
            type: 'string'
        }, {
            name: 'dbl_MONTANT',
            type: 'string'
        }, {
            name: 'str_NOM',
            type: 'string'
        },
        {
            name: 'str_MATRICULE',
            type: 'string'
        }, {
            name: 'str_PRENOM',
            type: 'string'
        }, {
            name: 'str_SECURITE_SOCIAL',
            type: 'string'
        },
        {
            name: 'str_STATUT',
            type: 'string'
        }, {
            name: 'dbl_MONTANT_REGLE',
            type: 'string'
        }, {
            name: 'dbl_MONTANT_RESTANT',
            type: 'string'
        }, {
            name: 'dt_CREATED',
            type: 'string'
        }, {
            name: 'str_REF',
            type: 'string'
        }, {
            name: 'lg_PREENREGISTREMENT_COMPTE_CLIENT_PAYENT_ID',
            type: 'string'
        }, {
            name: 'str_REF_BON',
            type: 'string'
        }, {
            name: 'int_CUST_PART',
            type: 'int'
        }, {
            name: 'int_PERCENT',
            type: 'int'
        }, {
            name: 'int_PRICE',
            type: 'int'
        }, {
            name: 'int_NB_DOSSIER',
            type: 'int'
        },
    {
        name: 'isChecked',
        type: 'boolean'
    } ,  {
        name: 'lg_DOSSIER_FACTURE',
        type: 'string'
    } ,  {
        name: 'dt_DATE',
        type: 'string'
    },  {
        name: 'dt_HEURE',
        type: 'string'
    } 
    ,  {
        name: 'MONTANTBRUT',
        type: 'float'
    },  {
        name: 'MONTANTREMISE',
        type: 'float'
    }
    
    ]
});
