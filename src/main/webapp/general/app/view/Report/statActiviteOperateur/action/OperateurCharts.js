

Ext.define('testextjs.view.Report.statActiviteOperateur.action.OperateurCharts', {
    extend: 'Ext.chart.Chart',
    alias: 'widget.op-chart',
    id: 'opchartID',
    animate: true,
    initComponent: function () {
        Me = this;
        var store = Ext.create('testextjs.store.Statistics.Operateur');

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
                        'BRUT TTC',
                        'Remise',
                        'NET TTC',
                        'PANIER MOYEN',
                        'M Ord',
                        'M Non Ord'
                    ],
//                    title: 'Number of Invoices',
                    minimum: 0
                }, {
                    type: 'Category',
                    position: 'bottom', // the axe position
                    fields: ['Operateur'] // the mapping data for this axe
//                    title: 'Month of the Year'
                }],
            series: [{
                    type: 'column',
                    axis: 'left',
                    xField: 'Operateur',
                    yField: [
                        'BRUT TTC',
                        'REMISE',
                        'NET TTC',
                        'PANIER MOYEN',
                        'M Ord',
                        'M Non Ord'
                    ],
                    style: {
                        opacity: 0.93
                    },
                    highlight: true,
                    tips: {
                        trackMouse: true,
                        style: 'font-size:11px;background: #fff;color: #6D6D6D;',
                        width: 180,
                        renderer: function (storeItem, item) {
                            this.setTitle(storeItem.get('Operateur') + ' </br> ' + item.yField + ': ' + storeItem.get(item.yField)
                                    );
                        }

                    }
                }]
        });

        this.callParent();


    }
});


