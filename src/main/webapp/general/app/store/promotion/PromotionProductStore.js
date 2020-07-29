/* global Ext */

Ext.define('testextjs.store.promotion.PromotionProductStore', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.promotions.PromotionProduct'
    ],
    model: 'testextjs.model.promotions.PromotionProduct',
    pageSize: 10,
    //storeId: 'familleStoreID',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '../webservices/Promotions/promotion/ws_promotion_products_data.jsp',
        reader: {
            type: 'json',
            root: 'results',
            totalProperty: 'total'
        },
        timeout: 240000
    }
});
