/* global Ext */

Ext.define('testextjs.model.TierspayantAccount', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'MONTANTCA',
            type: 'float'
        }, {
            name: 'NBLIENTS',
            type: 'float'
        }, {
            name: 'ACCOUNT',
            type: 'float'
        }
        
    ]
});
