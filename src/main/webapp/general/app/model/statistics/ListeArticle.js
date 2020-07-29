Ext.define('testextjs.model.statistics.ListeArticle', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields: [
        {name: 'id', type: 'int'},
        // {name: 'str_FAmille', type: 'string'},
        {name: 'str_CODE_CIP', type: 'string'},
        {name: 'str_Libelle_Produit', type: 'string'},
        {name: 'int_QTE_VENDUE', type: 'int'},
        {name: 'int_PU', type: 'float'},
        {name: 'int_MONTANT_VENTES', type: 'float'},
         {name: 'int_MONTANT_BRUT', type: 'float'},
        {name: 'MONTANREMISE', type: 'float'},
        {name: 'Emplacement', type: 'string'},
        {name: 'int_QTY', type: 'int'}
    ]
});

