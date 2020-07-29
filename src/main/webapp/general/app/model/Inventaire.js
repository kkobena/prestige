Ext.define('testextjs.model.Inventaire', {
    extend: 'Ext.data.Model',
    fields: [
    {
        name: 'lg_INVENTAIRE_ID',
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
        name: 'str_TYPE',
        type: 'string'
    },    
    {
        name: 'lg_USER_ID',
        type: 'string'
    },
    {
        name: 'str_STATUT',
        type: 'string'
    },
    {
        name: 'dt_CREATED',
        type: 'string'
    },
    {
        name: 'dt_UPDATED',
        type: 'string'
    }
    ,
    {
        name: 'etat',
        type: 'string'
    },
    {
        name: 'int_TOTAL',
        type: 'int'
    }
    ]
});
