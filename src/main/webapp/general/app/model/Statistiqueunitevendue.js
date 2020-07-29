Ext.define('testextjs.model.Statistiqueunitevendue', {
    extend: 'Ext.data.Model',
    fields: [
    {
        name: 'str_FAmille',
        type: 'string'
    },
    {
        name: 'str_CODE_CIP',
        type: 'string'
    },
    {
        name: 'str_Libelle_Produit',
        type: 'string'
    },
    {
        name: 'int_VENTE_COMPT',
        type: 'int'
    },
    {
        name: 'int_VENTE_CREDIT',
        type: 'int'
    },{
        name: 'int_QTE_VENDUE',
        type: 'int'
    },
    {
        name: 'int_QTE_STOCK',
        type: 'int'
    },
    {
        name: 'int_P_FAMILLE',
        type: 'int'
    },
    {
        name: 'int_NBRE_SORTIE',
        type: 'int'
    },
    {
        name: 'int_UNITE_MOY_VENTE',
        type: 'int'
    }
    ,
    {
        name: 'int_SEUIL_ACTUEL',
        type: 'int'
    },
    {
        name: 'int_MONTANT_VENTES',
        type: 'int'
    }
    ]
});
