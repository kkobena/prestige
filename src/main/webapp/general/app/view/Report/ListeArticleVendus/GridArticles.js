
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}
Ext.define('testextjs.view.Report.ListeArticleVendus.GridArticles', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.articlevendus-grid',
    initComponent: function() {

        Me = this;


        var itemsPerPage = 20;

        var store = Ext.create('testextjs.store.Statistics.ArticleVendus');

        Ext.apply(this, {
            features: [
                {
                    ftype: 'summary'
                }],
            id: 'Gridarticlevendu_ID',
            //  plugins: [this.cellEditing],
            store: store,
            columns: [
                {
                    header: 'Code CIP',
                    dataIndex: 'str_CODE_CIP',
                    flex:0.8, summaryType: "count",
                    summaryRenderer: function(value) {

                        if (value > 0) {
                            return "<b><span style='color:blue;'>TOTAL: </span></b>";
                        } else {
                            return '';
                        }
                    }
                },
                {
                    text: 'Désignation',
                    dataIndex: 'str_Libelle_Produit',
                    flex:2
                 
                },
                {
                    text: 'Emplacement',
                    dataIndex: 'Emplacement',
                    flex: 1
//                 
                }
                , {
                    text: 'Qte Stock',
                    dataIndex: 'int_QTY',
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
                    text: 'Qte Vendue',
                    dataIndex: 'int_QTE_VENDUE',
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
                    text: 'Prix.Unitaire',
                    dataIndex: 'int_PU',
                    renderer: amountformat,
                    align: 'right',
                    flex: 0.8, summaryType: "sum",
                    summaryRenderer: function(value) {
                        if (value > 0) {
                            return "<b><span style='color:blue;'>" + amountformat(value) + " F</span></b>";
                        } else {
                            return '';
                        }
                    }
                },
                {
                    text: 'Total Brut',
                    dataIndex: 'int_MONTANT_BRUT',
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
                    text: 'Total Remise',
                    dataIndex: 'MONTANREMISE',
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
                },
                {
                    text: 'Total Net',
                    dataIndex: 'int_MONTANT_VENTES',
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


    },
    /* onPrintClick: function () {
     
     
     window.print();
     body :{
     visibility:visible
     }
     print: {
     visibility:visible
     }
     
     
     },*/


    onImprimeClick: function()
    {
        var chaine = location.pathname;
        var reg = new RegExp("[/]+", "g");
        var tableau = chaine.split(reg);
        var sitename = tableau[1];
        var linkUrl = url_services_data_pdf;
        //var linkUrl = url_services_data + '?lg_RETROCESSION_ID=' + ref;
        alert("URL: " + linkUrl);
        //alert("Ok ca marche " + linkUrl);
        window.open(linkUrl);
    },
    onPrint: function() {

        var lg_customer_id = Ext.getCmp('lg_TIERS_PAYANT_ID').getValue(),
                dt_fin = Ext.getCmp('datefin').getSubmitValue(), dt_debut = Ext.getCmp('datedebut').getSubmitValue()
                ;
        if (Ext.getCmp('lg_TIERS_PAYANT_ID').getValue() == null) {
            lg_customer_id = "";
        }

        var linkUrl = "../webservices/sm_user/facturation/ws_data_relever_facture.jsp" + "?lg_customer_id=" + lg_customer_id + "&dt_debut=" + dt_debut + "&dt_fin=" + dt_fin;
        window.open(linkUrl);



    }
})


