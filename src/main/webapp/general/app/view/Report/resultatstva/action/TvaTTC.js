

Ext.define('testextjs.view.Report.resultatstva.action.TvaTTC', {
    extend: 'Ext.chart.Chart',
    alias: 'widget.tvaTTC-chart',
    id: 'tvattcchartID',
    
    animate: true,
    initComponent: function() {

       var store = Ext.create('testextjs.store.Statistics.TVAS');
     
        Ext.apply(this, 
        {
           
            style: 'background:#fff',
            store: store,
            legend: {
                position: 'bottom'
            },
            series: [{
                    type: 'pie',
                    axis: 'left',
                    field: 'Total TTC',
                    showInLegend:true,
                    highlight: {
                        segment: {
                            margin: 20
                        }
                    }, label: {// show the months names inside the pie
                        field: 'TAUX',
                        display: 'rotate',
                        contrast: true,
                        font: '18px Arial'
                    },style: {
                     opacity: 0.93
                     },
                     tips: {
                     trackMouse: true,
                   style: 'font-size:11px;background: #fff;color: #6D6D6D;',
                     width: 150,
                     
                     renderer: function(storeItem, item) {
                     this.setTitle(storeItem.get('TAUX')+'%' + '= Total TTC :' + storeItem.get('Total TTC') );
                     
                     }
                     }
                   
                }]
        });

        this.callParent();


    }
});


