/* global Ext */

Ext.define('testextjs.model.CategoryClient', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'lg_CATEGORY_CLIENT_ID',
            type: 'string'
        }, {
            name: 'str_LIBELLE',
            type: 'string'
        }, {
            name: 'str_ESCRIPTION',
            type: 'string'
        }
        , {
            name: 'int_taux',
            type: 'int'
        }
    ]
});
