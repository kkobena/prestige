Ext.define('testextjs.model.Statistiquefamille', {
    extend: 'Ext.data.Model',
    fields: [
    /*{
        name: 'lg_RETROCESSION_ID',
        type: 'string'
    },*/
    {
        name: 'str_Code',
        type: 'string'
    },
    {
        name: 'str_GROUPE_FAMILLE',
        type: 'string'
    },
    {
        name: 'int_PERIODE',
        type: 'date'
    },
    {
        name: 'int_MONTANT_NET_TTC',
        type: 'int'
    },{
        name: 'int_MONTANT_NET_HT',
        type: 'int'
    },
    {
        name: 'int_VALEUR_ACHAT',
        type: 'int'
    },
    {
        name: 'int_P_PERIODE',
        type: 'int'
    },
    {
        name: 'int_MARGE_NETTE',
        type: 'int'
    },
    {
        name: 'int_P_MARGE',
        type: 'int'
    }
    ,
    {
        name: 'int_P_TOTAL_HT',
        type: 'int'
    }
    ]
});
