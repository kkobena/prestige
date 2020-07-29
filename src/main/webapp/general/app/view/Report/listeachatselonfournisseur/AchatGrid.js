
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}
Ext.define('testextjs.view.Report.listeachatselonfournisseur.AchatGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.achatgrid-grid',
    initComponent: function() {


        var store = Ext.create('testextjs.store.Statistics.AchatGrossistes');

        Ext.apply(this, {
            features: [
                {
                    ftype: 'summary'
                }],
            id: 'achatgridID',
           
            store: store,
            columns: [
                {
                    text: 'Date',
                    dataIndex: 'DATEACHAT',
                    flex:0.6, summaryType: "count",
                    summaryRenderer: function(value) {

                        if (value > 0) {
                            return "<b><span style='color:blue;'>TOTAL: </span></b>";
                        } else {
                            return '';
                        }
                    }
                 
                },
                {
                    header: 'Grossiste',
                    dataIndex: 'str_LIBELLE',
                    flex:1.5
                },
                {
                    text: 'Désignation',
                    dataIndex: 'str_Libelle_Produit',
                    flex:2
                 
                }
                , {
                    text: 'Qt&eacute; Cmd&eacute;',
                    dataIndex: 'QTECMD',
                    flex:0.5, 
                    renderer: amountformat,
                    align: 'right',
                    summaryType: "sum",
                    summaryRenderer: function(value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + value + "</span></b>";
                        } else {
                            return '';
                        }
                    }
                },
                {
                    text: 'Qt&eacute; UG',
                    dataIndex: 'QTEUG',
                    flex: 0.5, summaryType: "sum",
                    renderer: amountformat,
                    align: 'right',
                    summaryRenderer: function(value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + "</span></b>";
                        } else {
                            return '';
                        }
                    }
                },{
                    text: 'Qt&eacute;Manquant',
                    dataIndex: 'QTEMANQUANT',
                    flex: 0.5, summaryType: "sum",
                    renderer: amountformat,
                    align: 'right',
                    summaryRenderer: function(value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + "</span></b>";
                        } else {
                            return '';
                        }
                    }
                },
                {
                    text: 'Qt&eacute; Re&ccedil;ue',
                    dataIndex: 'QTERECU',
                    renderer: amountformat,
                    align: 'right',
                    flex: 0.5, summaryType: "sum",
                    summaryRenderer: function(value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " </span></b>";
                        } else {
                            return '';
                        }
                    }
                },
                {
                    text: 'Montant',
                    dataIndex: 'MONTANT',
                    flex: 1, summaryType: "sum",
                    renderer: amountformat,
                    align: 'right',
                    summaryRenderer: function(value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F</span></b>";
                        } else {
                            return '';
                        }
                    }
                }
                , {
                    text: 'Op&eacute;rateur',
                    dataIndex: 'OPERATEUR',
                    flex: 1}
                
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


