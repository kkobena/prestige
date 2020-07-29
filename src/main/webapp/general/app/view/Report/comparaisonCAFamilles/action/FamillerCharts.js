

/* global Ext */ 
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}
Ext.define('testextjs.view.Report.comparaisonCAFamilles.action.FamillerCharts', {
    extend: 'Ext.chart.Chart',
    alias: 'widget.familleca-chart',
    id: 'famillecalignechartID',
    animate: true,
    initComponent: function () {

        //var store = Ext.create('testextjs.store.Statistics.FamillesCA');
       // var emptyData = {"data": []};
        var store = Ext.create('Ext.data.Store', {
           
            fields: ['periode', 'CA', 'CAP'],
            data:[]
        });

        Ext.apply(this, {
            //  heigh: 500,
            style: 'background:#fff',
            store: store,
            insetPadding: 30,
            legend: {
                position: 'bottom'
            }, axes: [{
                    type: 'Numeric',
                    minimum: 0,
                    position: 'left',
                    fields: ['CAP', 'CA'],
                    title: false,
                    grid: true,
                    label: {
                        renderer: Ext.util.Format.numberRenderer('0,0')

                    }
                }, {
                    type: 'Category',
                    position: 'bottom',
                    fields: ['periode'],
                    title: false

                }],
            series: [{
                    type: 'line',
                    axis: 'left',
                    xField: 'periode',
                    yField: 'CA',
                    tips: {
                        trackMouse: true,
                        width: 350,
                        renderer: function (storeItem, item) {
                            this.setTitle("Chiffre d'Affaires en " + storeItem.get('periode') + " est de : " + amountformat(storeItem.get('CA')));
                            // this.update(storeItem.get('CA'));

                        }
                    },
                    style: {
                        fill: '#38B8BF',
                        stroke: '#38B8BF',
                        'stroke-width': 3
                    },
                    markerConfig: {
                        type: 'circle',
                        size: 4,
                        radius: 4,
                        'stroke-width': 0,
                        fill: '#38B8BF',
                        stroke: '#38B8BF'
                    }
                },
                {
                    type: 'line',
                    axis: 'left',
                    xField: 'periode',
                    yField: 'CAP',
                    tips: {
                        trackMouse: true,
                        width: 350,
                        renderer: function (storeItem, item) {
                            this.setTitle("Chiffre d'Affaires en " + storeItem.get('periode') + " est de : " +amountformat(storeItem.get('CAP')));
                            // this.update(storeItem.get('CA'));

                        }
                    },
                    style: {
                        fill: '#FFDA64',
                        stroke: '#FFDA64',
                        'stroke-width': 3
                    },
                    markerConfig: {
                        type: 'circle',
                        size: 4,
                        radius: 4,
                        'stroke-width': 0,
                        fill: '#FF9F3A ',
                        stroke: '#FF9F3A'
                    }
                }]
        });

        this.callParent();


    }
});


