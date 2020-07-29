/* global Ext */

Ext.define('testextjs.store.promotion.PromotionStore', {
    extend: 'Ext.data.Store',
    requires: [
      'testextjs.model.promotions.Promotion'
    ],
   
    model:'testextjs.model.promotions.Promotion',
    pageSize:10,
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url:'../webservices/Promotions/promotion/ws_data.jsp',
        reader: {
            type: 'json',
            root: 'results',
            totalProperty: 'total'
        },
         timeout: 240000
    }
});