

Ext.define('testextjs.view.Report.resultatstva.action.TvaHT', {
    extend: 'Ext.chart.Chart',
    alias: 'widget.tvaHT-chart',
    id: 'tvahtchartID',
    animate: true,
    initComponent: function() {

       var store = Ext.create('testextjs.store.Statistics.TVAS');
      /* var store = Ext.create("Ext.data.Store", {
            model: "StatisticTVA",
            autoLoad: true
        });*/
        Ext.apply(this, {
            //  heigh: 500,
            style: 'background:#fff',
            store: store,
            legend: {
                position: 'bottom'
            }, 
            series: [{
                    type: 'pie',
                    axis: 'left',
                    field: 'Total HT',
                    showInLegend: true,
                    highlight: {
                        segment: {
                            margin: 20
                        }
                    }, label: {// show the months names inside the pie
                        field: 'TAUX',
                        display: 'insideStart',
                        contrast:true ,
                        font: '18px Arial'
                    }
                    ,style: {
                     opacity: 0.93
                     },
                     tips: {
                     trackMouse: true,
                   style: 'font-size:11px;background: #fff;color: #6D6D6D;',
                     width: 150,
                     
                     renderer: function(storeItem, item) {
                     this.setTitle(storeItem.get('TAUX')+'%' + '= Total HT:' + storeItem.get('Total HT') );
                     
                     }
                     }
                }]
        });

        this.callParent();


    }
});


