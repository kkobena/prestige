

Ext.define('testextjs.view.Report.statistiquefamillearticle.action.FamillerCharts', {
    extend: 'Ext.chart.Chart',
    alias: 'widget.famille-chart',
    id: 'famillechartID',
    animate: true,
    initComponent: function() {
        Me = this;
        var store = Ext.create('testextjs.store.Statistics.FamilleArticles');

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
                        'MONTANT NET TTC',
                        'MONTANT NET HT',
                        'VALEUR ACHAT',
                        'MARGE NET'
                    ],
//                    title: 'Number of Invoices',
                    minimum: 0
                }, {
                    type: 'Category',
                    position: 'bottom', // the axe position
                    fields: ['str_Libelle_Produit'] // the mapping data for this axe
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
                    xField: 'str_Libelle_Produit',
                    yField: [
                        'MONTANT NET TTC',
                        'MONTANT NET HT',
                        'VALEUR ACHAT',
                        'MARGE NET'

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
                            this.setTitle(storeItem.get('str_Libelle_Produit') + ' </br> ' + item.yField + ': ' + storeItem.get(item.yField)
                                    );
                        }

                    }
                }]
        });

        this.callParent();


    }
});


