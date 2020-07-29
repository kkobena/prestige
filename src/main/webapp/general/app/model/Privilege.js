Ext.define('testextjs.model.Privilege', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'lg_PRIVELEGE_ID',
            type: 'string'
        },
        {
            name: 'str_NAME',
            type: 'string'
        },
        {
            name: 'str_DESCRIPTION',
            type: 'string'
        },
        {
            name: 'is_select',
            type: 'boolean'
        }
    ]
});
