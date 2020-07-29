/* global Ext */

Ext.define('testextjs.model.statistics.AnalyseVenteStock', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'str_FAmille', type: 'string'},
        {name: 'str_CODE_FAMILLE', type: 'string'},
        {name: 'QTE VENDUE', type: 'int'},
        {name: 'NBRE SORTIE', type: 'int'},
        {name: 'UNITE MOY VENTE', type: 'float'},
        {name: 'int_SEUIL_ACTUEL', type: 'int'},
        {name: 'int_SEUIL_REAPP', type: 'int'},
        {name: 'int_QTE_REAPP', type: 'int'},
        {name: 'MONTANT VENTES', type: 'float'},
        {name: 'NB VENTES VNO', type: 'float'},
        {name: 'NB VENTES VO', type: 'float'},
        {name: 'Pourcentage', type: 'float'},
        {name: 'QTY', type: 'int'}
    ]
});

