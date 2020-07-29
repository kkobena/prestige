Ext.define('testextjs.store.Statistics.ArticleVendus', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.statistics.ListeArticle'
    ],

    model:'testextjs.model.statistics.ListeArticle',
   pageSize:20,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url:'../webservices/Report/listeArticleVendus/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
         timeout: 240000
    }
});
