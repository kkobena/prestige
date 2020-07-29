

/* global Ext */

Ext.define('testextjs.model.ModelQuinzaine', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'lg_QUINZAINE_ID',
            type: 'string'
        },
        {
            name: 'str_GROSSISTE_LIBELLE',
            type: 'string'
        },
        {
            name: 'dt_START_DATE',
            type: 'string'
        },
        {
            name: 'dt_END_DATE',
            type: 'string'
        }

    ]
});
