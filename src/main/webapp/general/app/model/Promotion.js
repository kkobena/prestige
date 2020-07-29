/* global Ext */

Ext.define('testextjs.model.Promotion', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'lg_PROMOTION_ID',
            type: 'string'
        },
        {
            name: 'dt_START_DATE',
            type: 'date'
        },
        {
            name: 'dt_END_DATE',
            type: 'date'
        }   
    ]
    
});