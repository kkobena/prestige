/* global Ext */

Ext.define('testextjs.model.promotions.Promotion', {
    extend: 'Ext.data.Model',
    idProperty: 'lg_CODE_PROMOTION_ID',
    fields: [
        {name: 'lg_CODE_PROMOTION_ID', type: 'string'},
        {name: 'str_TYPE', type: 'string'},
        {name: 'bl_ACTIVE', type: 'boolean'},
        {name: 'dt_START_DATE', type: 'string'},
        {name: 'dt_END_DATE', type: 'string'},
        {name: 'int_NUMBER_PRODUCT', type: 'int'}


    ]
});

