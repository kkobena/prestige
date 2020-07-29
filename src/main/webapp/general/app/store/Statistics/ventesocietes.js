/* global Ext */

Ext.define('testextjs.store.Statistics.ventesocietes', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.AchatFournisseurs'
    ],
   
    model:'testextjs.model.statistics.AchatFournisseurs',
    pageSize:15,
    groupField: 'ANNEE',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url:'../webservices/Report/ventessocietes/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
        timeout: 240000
    }
});