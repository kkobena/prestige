Ext.define('testextjs.model.Journalrecette', {
    extend: 'Ext.data.Model',
    fields: [
    {
        name: 'lg_ID',
        type: 'string'
    },
    {
        name: 'lg_TYPE_RECETTE_ID',
        type: 'string'
    },
    {
        name: 'int_AMOUNT',
        type: 'int'
    },
    {
        name: 'dt_DAY',
        type: 'string'
    },
    {
        name: 'int_NUMBER_TRANSACTION',
        type: 'int'
    },
    {
        name: 'type',
        type: 'string'
    },
    {
        name: 'name',
        type: 'string'
    }
    ]
});
