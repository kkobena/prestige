
/* global Ext */

function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
};
Ext.define('testextjs.view.Report.analyseventestock.analyseCharts', {
    extend: 'Ext.chart.Chart',
    alias: 'widget.analysestock-chart',
    id: 'analyseventechartID',
    animate: true,
    initComponent: function() {
        Me = this;
        var store = Ext.create('testextjs.store.Statistics.AnalyseVente');

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
                        'MONTANT VENTES'
                   ],
//                    title: 'Number of Invoices',
                    minimum: 0
                }, {
                    type: 'Category',
                    position: 'bottom', // the axe position
                    fields: ['str_FAmille'] // the mapping data for this axe
//                    title: 'Month of the Year'
                    ,
                    label: {display: 'insideStart',
                        font: '10px Arial',
                        rotate: {
                            degrees: -340
                        }}
                }],
            series: [{
                    type: 'column',
                    axis: 'left',
                    xField: 'str_FAmille',
                    yField: [
                        'MONTANT VENTES'
                        

                    ],
                    style: {
                        opacity: 0.93
                    },
                    highlight: true,
                    tips: {
                        trackMouse: true,
                        style: 'font-size:11px;background: #fff;color: #6D6D6D;',
                        width: 220,
                        renderer: function(storeItem, item) {
                            this.setTitle(storeItem.get('str_FAmille') + ' </br> ' + item.yField + ': ' + amountformat(storeItem.get(item.yField))+" F"
                                    );
                        }

                    }
                }]
        });

        this.callParent();


    }
});


