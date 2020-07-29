/* global Ext */

Ext.define('testextjs.model.statistics.FactureFournisseurs', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'DATEBL', type: 'string'},
        {name: 'LIBELLE', type: 'string'},
        {name: 'AMOUNT', type: 'float'},
        {name: 'MONATANTAVOIR', type: 'float'},
        {name: 'TOTALAMOUNT', type: 'float'},
        {name: 'TOTALAVOIR', type: 'float'},
        {name: 'NETPAYER', type: 'float'},
        {name: 'TVA', type: 'float'},
        {name: 'DATEECHEANCE', type: 'string'},
        {name: 'STATUS', type: 'string'},
        {name: 'FOURNISSEURS', type: 'string'}

    ]
});
