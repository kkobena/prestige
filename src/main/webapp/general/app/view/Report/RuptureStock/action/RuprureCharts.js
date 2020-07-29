

/* global Ext */

Ext.define('testextjs.view.Report.RuptureStock.action.RuprureCharts', {
    extend: 'Ext.chart.Chart',
    alias: 'widget.rupture-chart',
    id: 'rupturechartID',
    animate: true,
    initComponent: function () {
        Me = this;
        var store = Ext.create('testextjs.store.Statistics.RuptureStoks');
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
                        'Nombre Fois',
                        'Quantite'
                        
                    ],
//                    title: 'Number of Invoices',
                    minimum: 0
                }, {
                    type: 'Category',
                    position: 'bottom', // the axe position
                    fields: ['CODECIP'] // the mapping data for this axe
//                    title: 'Month of the Year'
                    /*,
                    label: {display: 'insideStart',
                        font: '10px Arial',
                        rotate: {
                            degrees: -340
                        }}*/
                }],
            series: [{
                    type: 'column',
                    axis: 'left',
                    xField: 'CODECIP',
                    yField: [
                        'Nombre Fois',
                        'Quantite'
                        

                    ],
                    style: {
                        opacity: 0.93
                    },
                    highlight: true,
                    tips: {
                        trackMouse: true,
                        style: 'font-size:11px;background: #fff;color: #6D6D6D;',
                        width: 220,
                        renderer: function (storeItem, item) {
                            this.setTitle(storeItem.get('CODECIP') + ' </br> ' + item.yField + ': ' + storeItem.get(item.yField)
                                    );
                        }

                    }
                }]
        });

        this.callParent();


    }
});


