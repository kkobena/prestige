Ext.define('testextjs.view.Report.statistiquevente.action.SalesChart', {
    extend: 'Ext.Panel',
    alias: 'widget.sales-chart',
    initComponent: function() {
        var me = this;

        this.myDataStore = Ext.create('Ext.data.JsonStore', {
            fields: ['month', 'data1' ],
            data: [
                { month: 'Jan', data1: 20 },
                { month: 'Feb', data1: 20 },
                { month: 'Mar', data1: 19 },
                { month: 'Apr', data1: 18 },
                { month: 'May', data1: 18 },
                { month: 'Jun', data1: 17 },
                { month: 'Jul', data1: 16 },
                { month: 'Aug', data1: 16 },
                { month: 'Sep', data1: 16 },
                { month: 'Oct', data1: 16 },
                { month: 'Nov', data1: 15 },
                { month: 'Dec', data1: 15 }
            ]
        });


        me.items = [{
            xtype: 'chart',
            height: 410,
            style: 'background: #fff',
            padding: '10 0 0 0',
            insetPadding: 40,
            animate: true,
            shadow: false,
            store: this.myDataStore,
            items: [{
                type  : 'text',
                text  : 'Column Charts - Basic Column',
                font  : '22px Helvetica',
                width : 500,
                height: 30,
                x : 40, //the sprite x position
                y : 12  //the sprite y position
            }/*, {
                type: 'text',
                text: 'Data: Browser Stats 2012',
                font: '10px Helvetica',
                x: 12,
                y: 380
            }, {
                type: 'text',
                text: 'Source: http://www.w3schools.com/',
                font: '10px Helvetica',
                x: 12,
                y: 390
            }*/],
            axes: [{
                type: 'Numeric',
                position: 'left',
                fields: ['data1'],
                label: {
                    renderer: function(v) { return v + '%'; }
                },
                grid: true,
                minimum: 0
            }, {
                type: 'Category',
                position: 'bottom',
                fields: ['month'],
                grid: true,
                label: {
                    rotate: {
                        degrees: -45
                    }
                }
            }],
            series: [{
                type: 'column',
                axis: 'left',
                xField: 'month',
                yField: 'data1',
                style: {
                    opacity: 0.80
                },
                highlight: {
                    fill: '#000',
                    'stroke-width': 20,
                    stroke: '#fff'
                },
                tips: {
                    trackMouse: true,
                    style: 'background: #FFF',
                    height: 20,
                    renderer: function(storeItem, item) {
                        this.setTitle(storeItem.get('month') + ': ' + storeItem.get('data1') + '%');
                    }
                }
            }]
        }];

        this.callParent();
    }
});