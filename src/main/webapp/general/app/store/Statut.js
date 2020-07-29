Ext.define('testextjs.store.Statut', {
    extend: 'Ext.data.ArrayStore',
    
    model: 'testextjs.model.Statut',
    
    storeId: 'statut',
    
    data: [
        ['enable', 'Active'],
        ['desable', 'Desactive']
    ]
});
