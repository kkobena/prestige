/* global Ext */

Ext.define('testextjs.model.statistics.FamilleArticle', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'CODE FAMILLE', type: 'string'},
        {name: 'MONTANT NET TTC', type: 'float'},
        {name: 'str_Libelle_Produit', type: 'string'},
        {name: 'MONTANT NET HT', type: 'float'},
        {name: 'VALEUR ACHAT', type: 'float'},
       // {name: 'POURCENTAGE PERIODE', type: 'float'},
        {name: 'MARGE NET', type: 'float'},
        {name: 'MARGE POURCENTAGE', type: 'float'},
        {name: 'POURCENTAGE TOTAL', type: 'float'}
        
    ]
});

