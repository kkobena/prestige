Ext.define('testextjs.store.Statistics.VenteChart', {
    extend: 'Ext.data.Store',
     requires: [
        'testextjs.model.statistics.VenteChart'
    ],

    model:'testextjs.model.statistics.VenteChart',
  pageSize:20,
    autoLoad: true,
     alias: 'VenteChart',
    proxy: {
        type: 'ajax',
        url: '../webservices/Report/statistiquevente/ws_graphe_data.jsp',
        reader: {
            type: 'json',
            root: 'data'
        },
         timeout: 240000
    }
});
