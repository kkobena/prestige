Ext.define('testextjs.model.statistics.Articles', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'str_FAmille', type: 'string'},
        {name: 'str_CODE_CIP', type: 'string'},
        {name: 'str_Libelle_Produit', type: 'string'},
        {name: 'VENTE COMPT', type: 'float'},
        {name: 'VENTE CREDIT', type: 'float'},
        {name: 'QTE VENDUE', type: 'int'},
        {name: 'NBRE SORTIE', type: 'int'},
        {name: 'UNITE MOY VENTE', type: 'float'},
        {name: 'int_SEUIL_ACTUEL', type: 'int'},
        {name: 'MONTANT VENTES', type: 'float'},
        {name: 'NB VENTES VNO', type: 'float'},
        {name: 'NB VENTES VO', type: 'float'},
        {name: 'Pourcentage', type: 'float'},
        {name: 'QTY', type: 'int'}
    ]
});

