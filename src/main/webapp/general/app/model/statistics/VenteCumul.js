Ext.define('testextjs.model.statistics.VenteCumul', {
    extend: 'Ext.data.Model',
    idProperty: 'id',
    fields: [
        {name: 'id'},
        {name: 'month'},
        {name: 'N Clients Cumul'},
        {name: 'M BrutTTC Cumul'},
        {name: 'Remise Cumul'},
        {name: 'M NetTTC Cumul'},
        {name: 'Pan MoyOrd Cumul'},
        {name: 'Pan MoyNo Cumul'},
        {name: 'Vente Ord Cumul'},
        {name: 'Vente No Cumul'}

    ]

         

});

