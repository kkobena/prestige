

/* global Ext */
Ext.define('testextjs.view.Report.analyseFrequentationOff.action.VisitorCharts', {
    extend: 'Ext.chart.Chart',
    alias: 'widget.visitor-chart',
    id: 'visitorchartID',
    animate: true,
    initComponent: function () {
        Me = this;
        var store = Ext.create('testextjs.store.Statistics.Tranches');

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
                        'Montant',
                        'Nbre Vente',
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
                            //degrees: -340
                        }}
                }],
            series: [{
                    type: 'column',
                    axis: 'left',
                    xField: 'TRANCHEHORAIRE',
                    yField: [
                        'Montant',
                        'Nbre Vente',
                        'Pan Moy'
                       

                    ],
                    style: {
                        opacity: 0.93
                    },
                    highlight: true,
                    tips: {
                        trackMouse: true,
                        style: 'font-size:11px;background: #fff;color: #6D6D6D;',
                        width: 150,
                        renderer: function (storeItem, item) {
                            this.setTitle(storeItem.get('TRANCHEHORAIRE') + ' </br> ' + item.yField + ': ' + storeItem.get(item.yField)
                                    );
                        }

                    }
                }]
        });

        this.callParent();


    }
});


