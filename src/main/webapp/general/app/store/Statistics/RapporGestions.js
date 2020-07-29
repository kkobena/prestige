/* global Ext */

Ext.define('testextjs.store.Statistics.RapporGestions', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.RapportGestion'
    ],
    model: 'testextjs.model.statistics.RapportGestion',
    groupField: 'STATUS',
    autoLoad: true,
    pageSize:20,
    proxy: {
        type: 'ajax',
       
        url: '../webservices/Report/RapportGestions/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
        sorters: [{
                property: 'STATUS',
                direction: 'DESC'
            }],
        timeout: 240000
    }
});