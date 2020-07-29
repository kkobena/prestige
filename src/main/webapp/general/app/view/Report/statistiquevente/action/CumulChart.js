

/* global Ext */

Ext.define('testextjs.view.Report.statistiquevente.action.CumulChart', {
    extend: 'Ext.chart.Chart',
    alias: 'widget.cumul-chart',
    id: 'cumulchartID',
     animate: true,
    initComponent: function() {

        var store = Ext.create('testextjs.store.Statistics.VenteCumuls');

        Ext.apply(this, {
            //  heigh: 500,
           
            style: 'background:#fff',
            store: store,
            legend: {
                position: 'bottom'
            }, axes: [{
                    type: 'Numeric',
                    grid: true,
                    position: 'left', // the axe position
                    fields: [
                        'M BrutTTC Cumul',
                        'Remise Cumul',
                        'M NetTTC Cumul',
                        'Pan MoyOrd Cumul',
                        'Pan MoyNo Cumul',
                        'Vente Ord Cumul',
                        'Vente No Cumul'
                    ],
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
                        'M BrutTTC Cumul',
                        'Remise Cumul',
                        'M NetTTC Cumul',
                        'Pan MoyOrd Cumul',
                        'Pan MoyNo Cumul',
                        'Vente Ord Cumul',
                        'Vente No Cumul'
                    ],
                    style: {
                        opacity: 0.93
                    },
                    tips: {
                        trackMouse: true,
                        style: 'font-size:11px;background: #fff;color: #6D6D6D;',
                        width: 180,
                        renderer: function(storeItem, item) {
                            this.setTitle(storeItem.get('month') + ' </br> ' + item.yField + ': ' + storeItem.get(item.yField)
                                    );
                        }
                    }
                }]
        });

        this.callParent();


    }
});


