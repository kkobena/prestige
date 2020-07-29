Ext.define('testextjs.model.ResumeCaisse', {
    extend: 'Ext.data.Model', 
    fields: [
    {
        name: 'lg_CAISSE_ID',
        type: 'string'
    },
    {
        name: 'lg_USER_ID',
        type: 'string'
    },
    {  
        name: 'str_NAME_USER',
        type: 'string'
    },
    {  
        name: 'etat',
        type: 'string'
    },


    {
        name: 'int_SOLDE_MATIN',
        type: 'int'
    },
    {
        name: 'int_SOLDE_SOIR',
        type: 'int'
    },{
        name: 'int_SOLDE_SOIR_STRING',
        type: 'string'
    },
    {
        name: 'int_SOLDE',
        type: 'int'
    },
    {
        name: 'int_SOLDE_STRING',
        type: 'string'
    },
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
    {
        name: 'int_AMOUNT_BILLETAGE',
        type: 'int'
    },
    {
        name: 'int_AMOUNT_ECART',
        type: 'int'
    },
    {
        name: 'int_AMOUNT_ECART_BIS',
        type: 'string'
    },
    
    {
        name: 'int_AMOUNT_ANNULE',
        type: 'int'
    },
    
    {
        name: 'btn_annulation',
        type: 'boolean'
    }
    
    
    ]
});
