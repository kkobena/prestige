/* global Ext */
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}
function amountformatbis(val) {
    return amountformat(val) + " F CFA";
}

Ext.define('testextjs.view.sm_user.balancevente.Balanceventemanager', {
    extend: 'Ext.grid.Panel',
    xtype: 'balancepara',
    id: 'balanceparaID',

    title: 'Balance Vente/Caisse',
    frame: true,
    requires: [

        'testextjs.model.Balance'


    ],
    initComponent: function () {

//'../webservices/sm_user/balance/ws_datas_paras.jsp'

        var itemsPerPage = 20;


        var store = new Ext.data.Store({
            model: 'testextjs.model.Balance',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../myBean?action=para',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                },
                timeout: 240000
            }

        });


        Ext.apply(this, {
            width: '98%',
            height: valheight - 100,

            store: store,
            id: 'gridBalanceVenteCaissepara',
            columns: [{
                    header: 'Type vente',
                    dataIndex: 'str_TYPE_VENTE',
                    flex: 0.5

                }, {
                    header: 'Nbre Vente',
                    dataIndex: 'NB',
                    flex: 0.5
                },
                {
                    text: 'Brut(TTC)',
                    renderer: amountformat,
                    dataIndex: 'VENTE_BRUT',
                    align: 'right',
                    flex: 1
                },

                {
                    text: 'Net(TTC)',
                    renderer: amountformat,
                    dataIndex: 'VENTE_NET',
                    align: 'right',
                    flex: 1
                }

            ],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [
                {
                    xtype: 'datefield',
                    fieldLabel: 'Du',
                    name: 'dt_start',
                    id: 'dt_debut_journalpara',
                    allowBlank: false,
                    margin: '0 10 0 0',
                    submitFormat: 'Y-m-d',
                    flex: 1,
                    labelWidth: 50,
                    value: new Date(),
                    format: 'd/m/Y',
                    listeners: {
                        'change': function (me) {

                            Ext.getCmp('dt_fin_journalpara').setMinValue(me.getValue());
                            // Ext.getCmp('gridBalanceVenteCaissepara').getStore().getProxy().url = url_services_data_balance + "?dt_Date_Debut=" + dt_Date_Debut;

                        }
                    }
                }, {
                    xtype: 'datefield',
                    fieldLabel: 'Au',
                    name: 'dt_end',
                    id: 'dt_fin_journalpara',
                    allowBlank: false,
                    labelWidth: 50,
                    flex: 1,
                    value: new Date(),
                    margin: '0 9 0 0',
                    submitFormat: 'Y-m-d',
                    format: 'd/m/Y',
                    listeners: {
                        'change': function (me) {
                            // dt_Date_Fin = me.getSubmitValue();
                            Ext.getCmp('dt_debut_journalpara').setMaxValue(me.getValue());
                            //  Ext.getCmp('gridBalanceVenteCaissepara').getStore().getProxy().url = url_services_data_balance + "?dt_Date_Debut=" + dt_Date_Debut + "&dt_Date_Fin=" + dt_Date_Fin;

                        }
                    }
                }, {
                    text: 'Rechercher',
                    tooltip: 'rechercher',
                    scope: this,
                    iconCls: 'searchicon',
//                    hidden: true,
                    handler: this.onRechClick
                }, '-', {
                    text: 'Imprimer',
                    tooltip: 'Imprimer la balance vente/caisse',
                    scope: this,
                    iconCls: 'printable',
                    handler: this.onPdfClick
                }],
            dockedItems: [
                // code ajouté 01/12/2016
                {
                    xtype: "toolbar",
                    dock: 'bottom',
                    items: [
                        {
                            xtype: 'displayfield',
                            flex: 1,
                            fieldLabel: 'MONTANT VENTE::',
                            fieldWidth: 110,
                            renderer: amountformatbis,
                            fieldStyle: "color:blue;font-weight:800;",
                            id: 'TOTALVENTEpara',
                            value: "0"

                        },
                        {
                            xtype: 'displayfield',
                            //allowBlank: false,
                            flex: 0.7,
                            fieldLabel: 'NOMBRE V::',
                            fieldWidth: 70,
                            name: 'totalPayantpara',
                            id: 'totalPayantpara',
                            renderer: amountformat,
                            fieldStyle: "color:blue;font-weight:800;",
                            // margin: '0 5 15 15',
                            value: "0"


                        }
                        ,
                        {
                            xtype: 'displayfield',
                            flex: 1,
                            fieldLabel: 'MONTANT ACHAT::',
                            fieldWidth: 110,
                            renderer: amountformatbis,
                            fieldStyle: "color:blue;font-weight:800;",
                            id: 'TOTALACHATpara',
                            value: "0"
                        },
                        {
                            xtype: 'displayfield',
                            flex: 0.7,
                            fieldLabel: 'MARGE::',
                            fieldWidth: 60,
                            renderer: amountformatbis,
                            fieldStyle: "color:blue;font-weight:800;",
                            id: 'TOTALMARGEpara',
                            value: "0"
                        }

                    ]
                }/*,
                 
                 {
                 xtype: "toolbar",
                 dock: 'bottom',
                 items: [
                 
                 totalPanierMoyenpara,
                 totalPayantpara
                 ]
                 }*/
            ]
        });

        this.callParent();

        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        });



    },
    loadStore: function () {
        this.getStore().load({
            callback: this.onStoreLoad
        });
    },
    onStoreLoad: function () {

        if (Ext.getCmp('gridBalanceVenteCaissepara').getStore().getCount() > 0) {

            var totalPanierMoyenpara = 0;

            var int_TOTAL_VENTE = 0;
            var totalPayantpara = 0;
            var TOTALANNULEESP = 0, TOTALACHAT = 0, TOTALANNULE = 0, TOTALRATIO = 0, TOTALMARGE = 0, TOTALFOND = 0, TOTALDIF = 0, TOTALENTRE = 0, TOTALTIERP = 0, TOTALSORTIE = 0, TOTALVENTE = 0, TOTALVENTE = 0;

            Ext.getCmp('gridBalanceVenteCaissepara').getStore().each(function (rec) {


                int_TOTAL_VENTE += parseInt(rec.get('NB'));
//                totalPanierMoyenpara += parseInt(rec.get('PANIER_MOYEN')); //a decommenter en cas de probleme. 28/05/2016
                totalPanierMoyenpara = rec.get('VENTE_NET_BIS');

                totalPayantpara += parseInt(rec.get('NB'));
                TOTALACHAT = rec.get('TOTALACHAT');


                TOTALMARGE = rec.get('TOTALMARGE');

                TOTALVENTE = rec.get('TOTALVENTE');


                //fin code ajouté 01/12/2016
            });



            //code ajouté 01/12/2016
            Ext.getCmp('TOTALVENTEpara').setValue(TOTALVENTE);
            Ext.getCmp('TOTALACHATpara').setValue(TOTALACHAT);
            Ext.getCmp('TOTALMARGEpara').setValue(TOTALMARGE);


        }


//        Ext.getCmp('totalPanierMoyenpara').setValue(totalPanierMoyenpara);
//Ext.getCmp('totalPanierMoyenpara').setValue(totalPanierMoyenpara / (totalPayantpara > 0 ? totalPayantpara : 1));
        Ext.getCmp('totalPayantpara').setValue(int_TOTAL_VENTE);

    },
    onRechClick: function () {
        if (new Date(Ext.getCmp('dt_debut_journalpara').getSubmitValue()) > new Date(Ext.getCmp('dt_fin_journalpara').getSubmitValue())) {
            Ext.MessageBox.alert('Erreur au niveau date', 'La date de d&eacute;but doit &ecirc;tre inf&eacute;rieur &agrave; la date fin');
            return;
        }
        this.getStore().load({
            params: {
                dt_Date_Debut: Ext.getCmp('dt_debut_journalpara').getSubmitValue(),
                dt_Date_Fin: Ext.getCmp('dt_fin_journalpara').getSubmitValue()
            }
        });

    },
    onPdfClick: function () {

        var chaine = location.pathname;
        var reg = new RegExp("[/]+", "g");
        var tableau = chaine.split(reg);
        var sitename = tableau[1];
//        var linkUrl = '../webservices/sm_user/balance/ws_generatepara_pdf.jsp?dt_Date_Debut=' + Ext.getCmp('dt_debut_journalpara').getSubmitValue() + '&dt_Date_Fin=' + Ext.getCmp('dt_fin_journalpara').getSubmitValue();
        var linkUrl = 'myBean?action=parapdf&dt_start=' + Ext.getCmp('dt_debut_journalpara').getSubmitValue() + '&dt_end=' + Ext.getCmp('dt_fin_journalpara').getSubmitValue();
        window.open(linkUrl);
    }


});