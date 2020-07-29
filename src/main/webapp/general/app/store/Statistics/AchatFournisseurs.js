/* global Ext */

Ext.define('testextjs.store.Statistics.AchatFournisseurs', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.AchatFournisseurs'
    ],
   
    model:'testextjs.model.statistics.AchatFournisseurs',
    groupField: 'ANNEE',
    pageSize:20,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url:'../webservices/Report/achatfournisseur/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
         timeout: 240000
    }
});