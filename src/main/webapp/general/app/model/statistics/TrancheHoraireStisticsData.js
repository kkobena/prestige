Ext.define('testextjs.model.statistics.TrancheHoraireStisticsData', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'TRANCHEHORAIRE', type: 'string'},
        {name: 'AMOUNT', type: 'float'},
        {name: 'NB', type: 'float'},
        {name: 'REFERENCES', type: 'float'}
    ]
});

