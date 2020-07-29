Ext.define('testextjs.model.CompteClientTierspayant', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'lg_COMPTE_CLIENT_TIERS_PAYANT_ID',
            type: 'string'
        },
        {
            name: 'lg_COMPTE_CLIENT_ID',
            type: 'string'
        },
        {
            name: 'str_REGIME',
            type: 'string'
        },
        {
            name: 'lg_TIERS_PAYANT_ID',
            type: 'string'
        },
        {
            name: 'int_POURCENTAGE',
            type: 'string'
        }, {
            name: 'str_TIERS_PAYANT_NAME',
            type: 'string'

        },{
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
            name: 'dbl_SOLDE',
            type: 'string'
        },
        {
            name: 'dt_CREATED',
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
        },{
            name: 'str_CODE_ORGANISME',
            type: 'string'
            
        },{
            name: 'int_PRIORITY',
            type: 'int'
            
        }, 
        {
            name: 'BTNDELETE', 
            type: 'boolean'
        },{
            name: 'dbl_PLAFOND',
            type: 'double'
            
        },{
            name: 'dbl_QUOTA_CONSO_MENSUELLE',
            type: 'double'
        },{
            name: 'dbl_QUOTA_CONSO_VENTE',
            type: 'double'
        }
        
        
        ,{
            name: 'db_PLAFOND_ENCOURS',
            type: 'number'
        },{
            name: 'dbl_PLAFOND',
            type: 'number'
        },
        {
            name: 'db_CONSOMMATION_MENSUELLE',
            type: 'number'
        },
        {
            name:'b_IsAbsolute',
            type:'boolean'
        },
        {
            name:'b_CANBEUSE',
            type:'boolean'
        }
        
        
    ]
});


