

/* global Ext */

function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}
Ext.define('testextjs.view.Report.resultatstva.tvaGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.tva-grid',
    initComponent: function() {
        var store = Ext.create('testextjs.store.Statistics.TVAS');

        Ext.apply(this, {
            features: [
                {
                    ftype: 'summary'
                }],
            id: 'tvaGrid',
            store: store,
            viewConfig: {
              
                    emptyText: '<h1 style="margin:10px 10px 10px 100px;">Pas de donn&eacute;es</h1>'
                },
            columns: [
                {
                    header: 'id',
                    dataIndex: 'id',
                    hidden: true
                },
                {
                    header: 'TAUX',
                    dataIndex: 'TAUX',
                    flex: 1,
                     align: 'right',
                    summaryType: "count",
                    renderer: amountformat,
                    summaryRenderer: function(value) {

                        if (value > 0) {
                            return "<b><span style='color:blue;'>TOTAL: </span></b>";
                        } else {
                            return '';
                        }
                    }
                },
                {
                    header: 'Total HT',
                    dataIndex: 'Total HT',
                    summaryType: "sum",
                     align: 'right',
                    renderer: amountformat,
                    flex: 1,
                    summaryRenderer: function(value) {

                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F</span></b>";
                        } else {
                            return '';
                        }
                    }
                }
                , {
                    text: 'Total TVA',
                    dataIndex: 'Total TVA',
                    flex: 1,
                     align: 'right',
                    renderer: amountformat,
                    summaryType: "sum",
                    summaryRenderer: function(value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F </span></b>";
                        } else {
                            return '';
                        }
                    }


                },
                {
                    text: 'Total TTC',
                    dataIndex: 'Total TTC',
                    summaryType: "sum",
                    renderer: amountformat,
                    flex: 1,
                     align: 'right',
                    summaryRenderer: function(value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + "  F</span></b>";
                        } else {
                            return '';
                        }
                    }

                }


            ],
            selModel: {
                selType: 'cellmodel'
            },
           
            bbar: {
                xtype: 'pagingtoolbar',
                store: store, // same store GridPanel is using
                dock: 'bottom',
                displayInfo: true
            }
        });

        this.callParent();


    }


});


