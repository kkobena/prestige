Ext.define('testextjs.model.SuggestionOrder', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'lg_SUGGESTION_ORDER_ID',
            type: 'string'
        },
        // str_REF
        {
            name: 'str_REF',
            type: 'string'
        },
        // int_QTE_ARTICLES
        {
            name: 'int_NOMBRE_ARTICLES',
            type: 'string'
        },
        {
            name: 'lg_GROSSISTE_ID',
            type: 'string'
        },
        {
            name: 'int_NUMBER',
            type: 'string'
        },
        {
            name: 'dt_UPDATED',
            type: 'string'
        },
        {
            name: 'str_STATUT',
            type: 'String'
        },
        //lg_FAMILLE_PRIX_VENTE
        {
            name: 'lg_FAMILLE_PRIX_VENTE',
            type: 'String'
        },
        {
            name: 'lg_FAMILLE_PRIX_ACHAT',
            type: 'string'
        },
        {
            name: 'dt_CREATED',
            type: 'String'
        },
        {
            name: 'str_FAMILLE_ITEM',
            type: 'string'
        },
        
        {
            name: 'int_TOTAL_VENTE',
            type: 'String'
        },
        {
            name: 'int_TOTAL_ACHAT',
            type: 'string'
        },
        {
            name: 'int_DATE_BUTOIR_ARTICLE',
            type: 'int'
        }       
    ]
});
