

/* global Ext */

Ext.define('testextjs.view.Report.analyseFrequentationOff.action.AreaVisitorCharts', {
    extend: 'Ext.chart.Chart',
    alias: 'widget.visitorarea-chart',
    id: 'visitorareaID',
//    animate: true,
    initComponent: function () {

        var store = Ext.create('testextjs.store.Statistics.Tranches');

        Ext.apply(this, {
            //  heigh: 500,
            style: 'background:#fff',
            animate: true,
            store: store,
            legend: {
                position: 'bottom'
            }, axes: [{
                    type: 'Numeric',
                    grid: true,
                    position: 'left', // the axe position
                    fields: [
                        'Montant',
                        'Pan Moy'

                    ],
//                    title: 'Number of Invoices',
                    minimum: 0
                }, {
                    type: 'Category',
                    position: 'bottom', // the axe position
                    fields: ['TRANCHEHORAIRE'] // the mapping data for this axe
//                    title: 'Month of the Year'
                    ,
                    label: {display: 'insideStart',
                        font: '10px Arial',
                        rotate: {
                            // degrees: -340
                        }}
                }],
            series: [{
                    type: 'area',
                    axis: 'left',
                    xField: 'TRANCHEHORAIRE',
                    yField: [
                        'Montant',
                        'Pan Moy'


                    ],
                    style: {
                        opacity: 0.93
                    },
                    highlight: true

                }]
        });

        this.callParent();


    }
});


