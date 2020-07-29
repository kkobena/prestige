Ext.define('testextjs.model.Imprimante', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'lg_IMPRIMANTE_ID',
            type: 'string'
        },
        {
            name: 'str_NAME',
            type: 'string'
        },
        {
            name: 'str_DESCRIPTION',
            type: 'string'
        },{
            name: 'lg_USER_IMPRIMQNTE_ID',
            type: 'string'
        },
        {
            name: 'lg_USER_ID',
            type: 'string'
        },
        {
            name: 'is_select',
            type: 'boolean'
        }
    ]
});
