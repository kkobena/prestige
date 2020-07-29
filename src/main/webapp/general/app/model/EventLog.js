Ext.define('testextjs.model.EventLog', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'lg_EVENT_LOG_ID',
            type: 'string'
        },
        {
            name: 'MATRICULE_ELEVE',
            type: 'string'
        },
        {
            name: 'dt_CREATED',
            type: 'string'
        },
        {
            name: 'dt_UPDATED',
            type: 'string'
        },
        {
            name: 'str_DESCRIPTION',
            type: 'string'
        },
        {
            name: 'str_CREATED_BY',
            type: 'string'
        },
        {
            name: 'str_UPDATED_BY',
            type: 'string'
        },
        {
            name: 'str_STATUT',
            type: 'string'
        },
        {
            name: 'str_TABLE_CONCERN',
            type: 'string'
        }, {
            name: 'str_MODULE_CONCERN',
            type: 'string'
        }

    ]
});
