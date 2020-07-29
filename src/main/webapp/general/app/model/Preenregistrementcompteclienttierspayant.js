Ext.define('testextjs.model.Preenregistrementcompteclienttierspayant', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'lg_PREENREGISTREMENT_COMPTE_CLIENT_PAYENT_ID',
            type: 'string'
        },
        {
            name: 'lg_PREENREGISTREMENT_ID',
            type: 'string'
        },
        {
            name: 'lg_COMPTE_CLIENT_TIERS_PAYANT_ID',
            type: 'string'
        },
        {
            name: 'lg_USER_ID',
            type: 'string'
        }
        ,
        
        {
            name: 'int_PRICE',
            type: 'int'
        }
        ,
        {
            name: 'str_STATUT',
            type: 'string'
        },
        {
            name: 'dt_CREATED',
            type: 'string'
        },
        {
            name: 'dt_UPDATED',
            type: 'string'
        },
        // lg_CLIENT_ID
        {
            name: 'lg_CLIENT_ID',
            type: 'string'
        },
        // lg_TIERS_PAYANT_ID
        {
            name: 'lg_TIERS_PAYANT_ID',
            type: 'string'
        },
        {
            name: 'str_REF',
            type: 'string'
        },{
            name: 'str_FIRST_NAME',
            type: 'string'
        },{
            name: 'str_LAST_NAME',
            type: 'string'
        },
        {
            name: 'int_PRICE_CLIENT',
            type: 'int'
        },
        {
            name: 'int_PRICE_TOTAL',
            type: 'int'
        }
        
    ]
});
