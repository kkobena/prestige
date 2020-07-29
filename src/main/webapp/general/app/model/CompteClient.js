/* global Ext */

Ext.define('testextjs.model.CompteClient', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'lg_COMPTE_CLIENT_ID',
            type: 'string'
        },
        {
            name: 'str_CODE_COMPTE_CLIENT',
            type: 'string'
        },
        {
            name: 'dbl_QUOTA_CONSO_MENSUELLE',
            type: 'string'
        },
        {
            name: 'dbl_CAUTION',
            type: 'string'
        },
        {
            name: 'dbl_SOLDE',
            type: 'string'
        },
        {
            name: 'DT_CREATED',
            type: 'string'
        },
        {
            name: 'DT_UPDATED',
            type: 'string'
        }, {
            name: 'STR_STATUT',
            type: 'string'
        }, {
            name: 'lg_CLIENT_ID',
            type: 'string'
        },
        {
            name: 'str_LAST_NAME',
            type: 'string'
        },
        {
            name: 'str_FIRST_NAME',
            type: 'string'
        },
        {
            name: 'str_SEXE',
            type: 'string'
        },
        {
            name: 'str_ADRESSE',
            type: 'string'
        },
        {
            name: 'str_NUMERO_SECURITE_SOCIAL',
            type: 'string'
        },
        {
            name: 'str_CODE_INTERNE',
            type: 'string'
        },
        {
            name: 'lg_VILLE_ID',
            type: 'string'
        },
        {
            name: 'str_CODE_POSTAL',
            type: 'string'
        },
        {
            name: 'RO',
            type: 'string'
        },
        {
            name: 'RO_TAUX',
            type: 'string'
        },
        {
            name: 'lg_COMPTE_CLIENT_TIERS_PAYANT_RO_ID',
            type: 'string'
        },
        {
            name: 'isCustSolvable',
            type: 'string'
        },
        {
            name: 'lg_AYANTS_DROITS_ID',
            type: 'string'
        },
        {
            name: 'dbl_total_differe',
            type: 'int'
        },
        {
            name: 'BTNDELETE', 
            type: 'boolean'
        },
        {
            name: 'dbl_PLAFOND_RO_ID', 
            type: 'double'
        },
        {
            name: 'dbl_QUOTA_CONSO_MENSUELLE_RO_ID', 
            type: 'double'
        },
        {
            name: 'dbl_PLAFOND_QUOTA_DIFFERENCE_RO_ID', 
            type: 'double'
        },
        {
            name: 'dbl_PLAFOND_RO', 
            type: 'double'
        },
        {
            name: 'dbl_QUOTA_CONSO_MENSUELLE_RO', 
            type: 'double'
        },
        {
            name: 'dbl_PLAFOND_QUOTA_DIFFERENCE_RO', 
            type: 'double'
        },
        {
            name: 'COMPTCLTTIERSPAYANT', 
            type: 'auto'
        },
        {
            name: 'TYPECLIENT', 
            type: 'string'
        }
    ]
});


