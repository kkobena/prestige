Ext.define('testextjs.model.Lot', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'lg_LOT_ID',
            type: 'string'
        },
        {
            name: 'str_NAME',
            type: 'string'
        },
        {
            name: 'lg_FAMILLE_ID',
            type: 'string'
        },
        {
            name: 'int_QUANTITE',
            type: 'int'
        },
        {
            name: 'int_QUANTITE_FAMILLE',
            type: 'int'
        },
        {
            name: 'int_QUANTITE_FAMILLE_BYLOT',
            type: 'int'
        },
        {
            name: 'str_STATUT',
            type: 'string'
        },
        {
            name: 'dt_PEROMPTION',
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
    ]
});
