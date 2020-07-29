/* global Ext */

Ext.define('testextjs.model.promotions.PromotionHistory', {
    extend: 'Ext.data.Model',
    //idProperty: 'lg_CODE_PROMOTION_HISTORY',
    fields: [
        {name: 'lg_CODE_PROMOTION_HISTORY', type: 'string'},
        {name: 'lg_CODE_PROMOTION_ID', type: 'string'},
        {name: 'int_CIP', type: 'string'},
        {name: 'lg_FAMILLE_ID', type: 'string'},
        {name: 'str_NAME', type: 'string'},
        {name: 'str_TYPE', type: 'string'},
        {name: 'int_DISCOUNT', type: 'string'},
        {name: 'bl_MODE', type: 'string'},
        {name: 'db_PROMOTION_PRICE',type: 'string'},
        {name: 'int_PACK_NUMBER', type: 'string'},
        {name: 'int_ACTIVE_AT', type: 'string'},
        {name: 'dt_START_DATE', type: 'string'},
        {name: 'dt_END_DATE', type: 'string'},
        {name: 'dt_PROMOTED_DATE', type: 'string'},
        {name: 'int_COUNT', type: 'int'}


    ]
});