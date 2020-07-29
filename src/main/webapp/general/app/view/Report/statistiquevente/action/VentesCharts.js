

/* global Ext */

Ext.define('testextjs.view.Report.statistiquevente.action.VentesCharts', {
    extend: 'Ext.chart.Chart',
    alias: 'widget.ventes-chart',
    id: 'venteschartID',
    
    initComponent: function () {
        Me = this;
        var store = Ext.create('testextjs.store.Statistics.VenteChart');

        Ext.apply(this, {
            //  heigh: 500,
            animate: true,
            style: 'background:#fff',
            store: store,
            legend: {
                position: 'bottom'
            }, axes: [{
                    type: 'Numeric',
                    grid: true,
                    position: 'left',
                    fields: [
                        'M BrutTTC',
                        'Remise',
                        'M NetTTC',
                        'Pan MoyOrd',
                        'Pan MoyNo',
                        'Vente Ord',
                        'Vente No'
                    ],
//                    fields: ['N Clients', 'N Clients cumul', 'M BrutTTC','M BrutTTC cumul','Remise','Remise cumul','M NetTTC','M NetTTC cumul','Pan MoyOrd','Pan MoyOrd Annee','Pan MoyNo','Pan MoyNo Annee','Vente Ord','% Ord Mois','Vente Ord cumul','% Ord cumul','Vente No','% No Mois','Vente No cumul','% No cumul'], 
//                    title: 'Number of Invoices',
                    minimum: 0
                }, {
                    type: 'Category',
                    position: 'bottom', // the axe position
                    fields: ['month'] // the mapping data for this axe
//                    title: 'Month of the Year'
                }],
            series: [{
                    type: 'column',
                    axis: 'left',
                    xField: 'month',
                    yField: [
                        'M BrutTTC',
                        'Remise',
                        'M NetTTC',
                        'Pan MoyOrd',
                        'Pan MoyNo',
                        'Vente Ord',
                        'Vente No'
                    ],
//                   
                    style: {
                        opacity: 0.93
                    },
                    tips: {
                        trackMouse: true,
                        style: 'font-size:11px;background: #fff;color: #6D6D6D;',
                        width: 180,
                        renderer: function (storeItem, item) {
                            this.setTitle(storeItem.get('month') + ' </br> ' + item.yField + ': ' + storeItem.get(item.yField)
                                    );
                        }
                    }

                }]
        });

        this.callParent();


    }
});


