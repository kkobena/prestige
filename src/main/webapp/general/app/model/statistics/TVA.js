/* global Ext */

Ext.define('testextjs.model.statistics.TVA', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields: [
        {name: 'id'},
        {name: 'TAUX'},
        {name: 'Total HT'},
        {name: 'Total TVA'},
        {name: 'Total TTC'}
        ]

         

});

