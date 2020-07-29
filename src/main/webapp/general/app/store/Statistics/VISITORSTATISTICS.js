/* global Ext */

Ext.define('testextjs.store.Statistics.VISITORSTATISTICS', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.TrancheHoraire'
    ],
    model: 'testextjs.model.statistics.TrancheHoraire',
    pageSize:5,
    groupField: 'JOUR',
    groupDir: 'DESC',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '../webservices/Report/visitorstatistics/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
        timeout: 240000
    }
});