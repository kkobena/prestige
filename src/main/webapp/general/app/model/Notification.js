Ext.define('testextjs.model.Notification', {
    extend: 'Ext.data.Model',
    fields: [
    {
        name: 'lg_ID',
        type: 'string'
    },
    {
        name: 'str_CONTENT',
        type: 'string'
    },

    {
        name: 'str_REF_RESSOURCE',
        type: 'string'
    },
    {
        name: 'lg_USER_ID',
        type: 'int'
    },
    {
        name: 'str_STATUT',
        type: 'string'
    },
    {
        name: 'str_TYPE',
        type: 'string'
    },
    {
        name: 'dt_CREATED',
        type: 'string'
    }


    ]
});
