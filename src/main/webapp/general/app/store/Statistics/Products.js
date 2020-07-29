/* global Ext */

Ext.define('testextjs.store.Statistics.Products', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.Famille'
    ],
    model: 'testextjs.model.Famille',
    pageSize: 20,
   
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '../webservices/sm_user/famille/ws_datas.jsp',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        }
        
    }
});