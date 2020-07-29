Ext.define('testextjs.store.Statistics.FamilleArticles', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.FamilleArticle'
    ],

    model:'testextjs.model.statistics.FamilleArticle',
   pageSize:20,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url:'../webservices/Report/statistiquefamillearticle/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
         timeout: 240000
    }
});
