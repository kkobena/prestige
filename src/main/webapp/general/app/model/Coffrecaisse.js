Ext.define('testextjs.model.Coffrecaisse', {
    extend: 'Ext.data.Model',
    fields: [
    {
        name: 'ID_COFFRE_CAISSE',
        type: 'string'
    },
    {
        name: 'lg_USER_ID',
        type: 'string'
    }, 


    {
        name: 'int_AMOUNT',
        type: 'int'
    },
    {
        name: 'str_STATUT',
        type: 'string'
    },
    {
        name: 'ld_CREATED_BY',
        type: 'string'
    },
    {
        name: 'ld_UPDATED_BY',
        type: 'string'
    },
    {
        name: 'dt_CREATED',
        type: 'string'
    },
    {
        name: 'lg_EMPLACEMENT_ID',
        type: 'string'
    },
    {
        name: 'show',
        type: 'boolean'
    }
    ]
});
