Ext.define('testextjs.model.Retrocession', {
    extend: 'Ext.data.Model',
    fields: [
    {
        name: 'lg_RETROCESSION_ID',
        type: 'string'
    },
    {
        name: 'str_REFERENCE',
        type: 'string'
    },
    {
        name: 'str_COMMENTAIRE',
        type: 'string'
    },
    {
        name: 'int_MONTANT_HT',
        type: 'int'
    },
    {
        name: 'int_MONTANT_TTC',
        type: 'int'
    },{
        name: 'int_TOTAL_PRODUIT',
        type: 'int'
    },
    {
        name: 'lg_CLIENT_ID',
        type: 'string'
    },
    {
        name: 'int_REMISE',
        type: 'int'
    },
    {
        name: 'int_ESCOMPTE_SOCIETE',
        type: 'int'
    },
    {
        name: 'lg_TVA_ID',
        type: 'string'
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
        name: 'str_FAMILLE_ITEM',
        type: 'string'
    }
    ]
});
