Ext.define('testextjs.model.statistics.AchatGrossiste', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields: [
        {name: 'id', type: 'string'},
        
        {name: 'str_LIBELLE', type: 'string'},
        {name: 'str_Libelle_Produit', type: 'string'},
        {name: 'QTECMD', type: 'int'},
        {name: 'QTEUG', type: 'int'},
        {name: 'QTERECU', type: 'int'},
        {name: 'PRIXACHAT', type: 'int'},
        {name: 'QTEMANQUANT', type: 'int'},
        {name: 'DATEACHAT', type: 'string'},
        {name: 'MONTANT', type: 'float'},
        {name: 'OPERATEUR', type: 'string'}
       
    ]
});

