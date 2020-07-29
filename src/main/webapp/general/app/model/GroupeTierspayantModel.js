/* global Ext */

Ext.define('testextjs.model.GroupeTierspayantModel', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'lgTIERSPAYANTID',
            type: 'string'
        },
        {
            name: 'str_GROUPE_LIB',
            type: 'string'
        },
        {
            name: 'str_LIB',
            type: 'string'
        },
        {
            name: 'lg_GROUPE_ID',
            type: 'number'

        },
         {
            name: 'isChecked',
            type: 'boolean'

        }, {
            name: 'AMOUNT',
            type: 'number'

        },
        {
            name: 'NBBONS',
            type: 'number'

        }



    ]
});
