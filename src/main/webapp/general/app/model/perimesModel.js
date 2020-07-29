Ext.define('testextjs.model.perimesModel', {
    extend: 'Ext.data.Model',
    fields: [

        {
            name: 'ID',
            type: 'string'
        },
        {
            name: 'CIP',
            type: 'string'
        },
        {
            name: 'ARTICLE',
            type: 'string'
        },
        {
            name: 'QTY',
            type: 'int'
        },
        {
            name: 'DATEENTREE',
            type: 'string'
        },
        {
            name: 'LOT',
            type: 'string'
        },
        {
            name: 'DATEPEREMPTION',
            type: 'string'
        },
        {
            name: 'lg_FAMILLE_ID',
            type: 'string'
        },
        {
            name: 'OPERATEUR',
            type: 'string'
        },{
            name: 'GROSSISTE',
            type: 'string'
        },{
            name: 'MONTANT',
            type: 'number'
        },{
            name: 'PU',
            type: 'number'
        }


    ]
});
