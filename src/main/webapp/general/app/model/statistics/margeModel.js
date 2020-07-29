/* global Ext */

Ext.define('testextjs.model.statistics.margeModel', {
    extend: 'Ext.data.Model',
    idProperty: 'lg_FAMILLE_ID',
    fields: [
        {name: 'lg_FAMILLE_ID', type: 'string'},
        {name: 'CIP', type: 'string'},
        {name: 'DESC', type: 'string'},

        {name: 'INTPU', type: 'number'},
        {name: 'INTPA', type: 'number'},
        {name: 'AMOUNTPA', type: 'number'},
        {name: 'AMOUNTPV', type: 'number'},
        {name: 'MARGE', type: 'number'},
        {name: 'QTY', type: 'number'},
        {name: 'AMOUNTTTC', type: 'number'}
        
        

    ]
});

