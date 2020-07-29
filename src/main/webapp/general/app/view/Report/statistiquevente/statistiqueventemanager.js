
/* global Ext */

var url_services_data_pdf = '../webservices/Report/statistiquevente/ws_statistiquevente_pdf.jsp';

var url_services_transaction_statistiquevente = '../webservices/Report/statistiquevente/ws_transaction.jsp?mode=';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}
;
var Me;
var Periode;
Ext.define('testextjs.view.Report.statistiquevente.statistiqueventemanager', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.ventestatic-grid',
    initComponent: function () {

        Me = this;

        url_services_data_pdf = '../webservices/Report/statistiquevente/ws_statistiquevente_pdf.jsp';

        var itemsPerPage = 20;

        var store = Ext.create('testextjs.store.Statistics.Ventes');

        Ext.apply(this, {
            heigh: 500,
//            minHeight: 570,
//            maxHeight: 800,
//            layout:'card',
            id: 'Grid_venteStatistiquevente_ID',
            //  plugins: [this.cellEditing],
            store: store,
            viewConfig: {
                emptyText: '<h1 style="margin:10px">Pas de donn&eacute;es</h1>'
            },
            columns: [
                {
                    header: 'Mois',
                    dataIndex: 'month',
                    width: 90
                },
                {
                    text: 'N.Clients',
//                    flex: 1,
                    columns: [
                        {
                            text: 'N.Clients',
                            renderer: amountformat,
                            dataIndex: 'NB_CLIENT',
                            align: 'right',
                            flex: 0.5
                        },
                        {
                            text: 'Cumul',
                            renderer: amountformat,
                            dataIndex: 'NB_CLIENTCUMUL',
                            align: 'right',
                            flex: 0.5
                        }
                    ]
                }
                , {
                    text: 'Montant Brut',
//                    flex: 1,
                    columns: [
                        {
                            text: 'Mont.BrutTTC',
                            renderer: amountformat,
                            dataIndex: 'BRUT_TTC',
                            align: 'right',
                            flex: 0.8
                        },
                        {
                            text: 'Cumul',
                            renderer: amountformat,
                            dataIndex: 'MONTANT_BRUTCUMUL',
                            align: 'right',
                            flex: 0.8
                        }
                    ]
                },
                {
                    text: 'Remise',
//                    flex: 1,
                    columns: [
                        {
                            text: 'Remise',
                            renderer: amountformat,
                            dataIndex: 'REMISE',
                            align: 'right',
                            flex: 0.5
                        },
                        {
                            text: 'Cumul',
                            renderer: amountformat,
                            dataIndex: 'MONTANT_REMISECUMUL',
                            align: 'right',
                            flex: 0.5
                        }
                    ]
                },
                {
                    text: 'Mont.NetTTC',
//                    flex: 1,
                    columns: [
                        {
                            text: 'Mont.Net',
                            renderer: amountformat,
                            dataIndex: 'NET_TTC',
                            align: 'right',
                            flex: 0.8
                        },
                        {
                            text: 'Cumul',
                            renderer: amountformat,
                            dataIndex: 'MONTANT_NETCUMUL',
                            align: 'right',
                            flex: 0.8
                        }
                    ]
                },
                {
                    text: 'Pan.Moy.Ord',
//                    flex: 1,
                    columns: [
                        {
                            text: 'Pan.Moy.Ord',
                            renderer: amountformat,
                            dataIndex: 'PANIER_MOYEN_M_VO',
                            align: 'right',
                            flex: 0.8
                        },
                        {
                            text: 'Ann&eacute;e',
                            renderer: amountformat,
                            dataIndex: 'PANIER_MOYEN_M_VO_CUMUL',
                            align: 'right',
                            flex: 0.8
                        }
                    ]
                }
                , {
                    text: 'Pan.Moy.No',
//                    flex: 1,
                    columns: [
                        {
                            text: 'Pan.Moy.No',
                            renderer: amountformat,
                            dataIndex: 'PANIER_MOYEN_M_VNO',
                            align: 'right',
                            flex: 0.8
                        },
                        {
                            text: 'Ann&eacute;e',
                            renderer: amountformat,
                            dataIndex: 'PANIER_MOYEN_M_VNO_CUMUL',
                            align: 'right',
                            flex: 0.8

                        }
                    ]
                },
                {
                    header: 'Ventes.Ord',
//                    flex: 1,
                    columns: [
                        {
                            text: 'Ventes.Ord',
                            renderer: amountformat,
                            dataIndex: 'AMOUT_VO',
                            align: 'right',
                            flex: 0.8
                        },
                        {
                            text: '%Mois',
                            align: 'right',
//                            renderer: amountformat,
                            dataIndex: 'vo_month_percent',
                            renderer: function (value) {
                                return Ext.Number.toFixed(value, 2);
                            },
                            flex: 0.8
                        },
                        {
                            text: 'Cumul',
                            renderer: amountformat,
                            dataIndex: 'MONTANT_VOCUMUL',
                            align: 'right',
                            flex: 0.8

                        },
                        {
                            text: '%Cumul',
//                            renderer: amountformat,
                            dataIndex: 'vo_cumul_percent',
                            align: 'right',
                            renderer: function (value) {
                                return Ext.Number.toFixed(value, 2);
                            },
                            flex: 0.5
                        }
                    ]
                },
                {
                    header: 'Ventes.NO',
//                    flex: 1,
                    columns: [
                        {
                            text: 'Ventes.NO',
                            renderer: amountformat,
                            dataIndex: 'AMOUT_VNO',
                            align: 'right',
                            flex: 0.8
                        },
                        {
                            text: '%Mois',
//                            renderer: amountformat,
                            dataIndex: 'vno_month_percent',
                            align: 'right',
                            renderer: function (value) {
                                return Ext.Number.toFixed(value, 2);
                            },
                            flex: 0.8
                        },
                        {
                            text: 'Cumul',
                            renderer: amountformat,
                            dataIndex: 'MONTANT_VNOCUMUL',
                            align: 'right',
                            flex: 0.8
                        },
                        {
                            text: '%Cumul',
                            align: 'right',
//                            renderer: amountformat,
                            dataIndex: 'vno_cumul_percent',
                            renderer: function (value) {
                                return Ext.Number.toFixed(value, 2);
                            },
                            flex: 0.5
                        }
                    ]
                }



            ],
            selModel: {
                selType: 'cellmodel'
            },
            /* tbar: [
             {
             text: 'Imprimer',
             scope: this,
             handler: this.onImprimeClick
             }
             ],*/
            bbar: {
                xtype: 'pagingtoolbar',
                store: store, // same store GridPanel is using
                dock: 'bottom',
                displayInfo: true,
                listeners: {
                    beforechange: function (page, currentPage) {
                        var myProxy = this.store.getProxy();
                        myProxy.params = {
                            dt_start_vente: '',
                            dt_end_vente: ''
                        };
                        var dt_start = Ext.getCmp('dt_start_vente').getSubmitValue();
                        var dt_end = Ext.getCmp('dt_end_vente').getSubmitValue();
                        myProxy.setExtraParam('dt_start_vente', dt_start);
                        myProxy.setExtraParam('dt_end_vente', dt_end);

                    }

                }
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


    onImprimeClick: function ()
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
    onPrint: function () {

        var lg_customer_id = Ext.getCmp('lg_TIERS_PAYANT_ID').getValue(),
                dt_fin = Ext.getCmp('datefin').getSubmitValue(), dt_debut = Ext.getCmp('datedebut').getSubmitValue()
                ;
        if (Ext.getCmp('lg_TIERS_PAYANT_ID').getValue() === null) {
            lg_customer_id = "";
        }

        var linkUrl = "../webservices/sm_user/facturation/ws_data_relever_facture.jsp" + "?lg_customer_id=" + lg_customer_id + "&dt_debut=" + dt_debut + "&dt_fin=" + dt_fin;
        window.open(linkUrl);



    }
});


