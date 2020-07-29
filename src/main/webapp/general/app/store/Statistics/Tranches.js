Ext.define('testextjs.store.Statistics.Tranches', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.TrancheHoraireStisticsData'
    ],
   
    model:'testextjs.model.statistics.TrancheHoraireStisticsData',
    pageSize:20,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url:'../webservices/Report/visitorstatistics/ws_graphe_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
         timeout: 240000
    }
});