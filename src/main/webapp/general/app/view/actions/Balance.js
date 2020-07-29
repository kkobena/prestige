/* global Ext */



//var store;
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}
function amountformatbis(val) {
    return amountformat(val) + " F CFA";
}
Ext.define('testextjs.view.actions.Balance', {
    extend: 'Ext.grid.Panel',
    xtype: 'ventecaissemanager',
    id: 'ventecaissemanagerID',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.window.Window',
        'Ext.data.*',
        'Ext.util.*',
        'Ext.form.*',
        'Ext.JSON.*',
        'testextjs.model.Balance',
        'Ext.ux.ProgressBarPager',
        'Ext.ux.grid.Printer'

    ],
    title: 'Balance Vente/Caisse',
    closable: false,
    frame: true,
    initComponent: function () {
        var itemsPerPage = 20;
        var store = new Ext.data.Store({
            model: 'testextjs.model.Balance',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../myBean?action=balance',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                },
                timeout: 2400000
            }

        });
        var int_TOTAL_ESPECE = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    //allowBlank: false,
                    flex: 0.7,
                    fieldLabel: 'ESPECES::',
                    fieldWidth: 70,
                    name: 'int_TOTAL_ESPECE',
                    id: 'int_TOTAL_ESPECE',
                    renderer: amountformatbis,
                    fieldStyle: "color:blue;font-weight:800;",
                    // margin: '0 5 15 15',
                    value: "0"


                });

        var int_TOTAL_CHEQUE = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    //allowBlank: false,
                    flex: 0.7,
                    fieldLabel: 'CHEQUES::',
                    fieldWidth: 70,
                    name: 'int_TOTAL_CHEQUE',
                    id: 'int_TOTAL_CHEQUE',
                    renderer: amountformatbis,
                    fieldStyle: "color:blue;font-weight:800;",
                    // margin: '0 5 15 15',
                    value: "0"


                });
        var totalPanierMoyen = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    //allowBlank: false,
                    flex: 0.7,
                    fieldLabel: 'PANIER M::',
                    fieldWidth: 70,
                    name: 'totalPanierMoyen',
                    id: 'totalPanierMoyen',
                    renderer: amountformatbis,
                    fieldStyle: "color:blue;font-weight:800;",
                    // margin: '0 5 15 15',
                    value: "0"


                });

        var totalPayant = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    //allowBlank: false,
                    flex: 0.7,
                    fieldLabel: 'NOMBRE V::',
                    fieldWidth: 70,
                    name: 'totalPayant',
                    id: 'totalPayant',
                    renderer: amountformat,
                    fieldStyle: "color:blue;font-weight:800;",
                    // margin: '0 5 15 15',
                    value: "0"


                });

        var int_TOTAL_OTHER = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    //allowBlank: false,
                    flex: 0.7,
                    fieldLabel: 'AUTRES::',
                    fieldWidth: 70,
                    hidden: true,
                    name: 'int_TOTAL_OTHER',
                    id: 'int_TOTAL_OTHER',
                    renderer: amountformatbis,
                    fieldStyle: "color:blue;font-weight:800;",
                    // margin: '0 5 15 15',
                    value: "0"


                });




        Ext.apply(this, {
            width: '98%',
            height: valheight - 100,

            store: store,
            id: 'gridBalanceVenteCaisse',
            columns: [{
                    header: 'Type vente',
                    dataIndex: 'str_TYPE_VENTE',
                    flex: 0.5

                }, {
                    header: 'Nbre Vente',
                    dataIndex: 'NB',
                    flex: 0.5
                }, {
                    text: 'Montant',
                    columns: [
                        {
                            text: 'Brut(TTC)',
                            renderer: amountformat,
                            dataIndex: 'VENTE_BRUT',
                            align: 'right',
                            flex: 1
                        },
                        {
                            text: 'Remise',
                            renderer: amountformat,
                            dataIndex: 'TOTAL_REMISE',
                            align: 'right',
                            flex: 1
                        },
                        {
                            text: 'Net(TTC)',
                            renderer: amountformat,
                            dataIndex: 'VENTE_NET',
                            align: 'right',
                            flex: 1
                        },
                        {
                            text: '%',
                            dataIndex: 'POURCENTAGE',
                            flex: 0.5
                        }
                    ]
                }, {
                    header: 'Panier.M',
                    dataIndex: 'PANIER_MOYEN',
                    renderer: amountformat,
                    align: 'right',
                    flex: 1
                }, {
                    header: 'Especes',
                    dataIndex: 'TOTAL_ESPECE',
                    renderer: amountformat,
                    align: 'right',
                    flex: 1
                }, {
                    header: 'Cheques',
                    dataIndex: 'TOTAL_CHEQUE',
                    renderer: amountformat,
                    align: 'right',
                    flex: 1
                }, {
                    header: 'Carte.Banc',
                    dataIndex: 'TOTAL_CARTEBANCAIRE',
                    renderer: amountformat,
                    align: 'right',
                    flex: 1
                }, {
                    header: 'Differe',
                    dataIndex: 'TOTAL_DIFFERE',
                    renderer: amountformat,
                    align: 'right',
                    flex: 1
                }, {
                    header: 'Tiers payant',
                    dataIndex: 'PART_TIERSPAYANT',
                    renderer: amountformat,
                    align: 'right',
                    flex: 1
                }],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [
                {
                    xtype: 'datefield',
                    fieldLabel: 'Du',
                    name: 'dt_debut',
                    id: 'dt_debut_journal',
                    allowBlank: false,
                    margin: '0 10 0 0',
                    submitFormat: 'Y-m-d',
                    flex: 1,
                    labelWidth: 50,
                    maxValue: new Date(),
                    format: 'd/m/Y',
                    listeners: {
                        'change': function (me) {

                            Ext.getCmp('dt_fin_journal').setMinValue(me.getValue());

                        }
                    }
                }, {
                    xtype: 'datefield',
                    fieldLabel: 'Au',
                    name: 'dt_fin',
                    id: 'dt_fin_journal',
                    allowBlank: false,
                    labelWidth: 50,
                    flex: 1,
                    maxValue: new Date(),
                    margin: '0 9 0 0',
                    submitFormat: 'Y-m-d',
                    format: 'd/m/Y',
                    listeners: {
                        'change': function (me) {
                            // dt_Date_Fin = me.getSubmitValue();
                            Ext.getCmp('dt_debut_journal').setMaxValue(me.getValue());

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
                            fieldWidth: 80,
                            renderer: amountformatbis,
                            fieldStyle: "color:blue;font-weight:800;",
                            id: 'TOTALVENTE',
                            value: "0"

                        },
                        {
                            xtype: 'displayfield',
                            flex: 1,
                            fieldLabel: 'MONTANT ACHAT::',
                            fieldWidth: 80,
                            renderer: amountformatbis,
                            fieldStyle: "color:blue;font-weight:800;",
                            id: 'TOTALACHAT',
                            value: "0"
                        },
                        {
                            xtype: 'displayfield',
                            flex: 0.7,
                            fieldLabel: 'MARGE::',
                            fieldWidth: 60,
                            renderer: amountformatbis,
                            fieldStyle: "color:blue;font-weight:800;",
                            id: 'TOTALMARGE',
                            value: "0"
                        },
                        {
                            xtype: 'displayfield',
                            flex: 0.7,
                            fieldLabel: 'RATIO V/A::',
                            fieldWidth: 80,
//                            renderer: amountformatbis,
                            fieldStyle: "color:blue;font-weight:800;",
                            id: 'TOTALRATIO',
                            value: "0"
                        }

                    ]
                },
                //fin code ajouté 01/12/2016


                {
                    xtype: "toolbar",
                    dock: 'bottom',
                    items: [
                        {
                            xtype: 'displayfield',

                            flex: 0.7,
                            fieldLabel: 'FOND.CAISSE::',
                            fieldWidth: 70,
                            renderer: amountformatbis,
                            fieldStyle: "color:blue;font-weight:800;",
                            // margin: '0 5 15 15',
                            id: 'TOTALFOND',
                            value: "0"

                        }, {
                            xtype: 'displayfield',

                            flex: 0.7,
                            fieldLabel: 'REGL.DIFFERE::',
                            fieldWidth: 70,
                            id: 'TOTALDIF',
                            renderer: amountformatbis,
                            fieldStyle: "color:blue;font-weight:800;",
                            // margin: '0 5 15 15',
                            value: "0"

                        },
                        {
                            xtype: 'displayfield',

                            flex: 0.7,
                            fieldLabel: 'REGL.TPAYANT::',
                            fieldWidth: 80,
                            renderer: amountformatbis,
                            fieldStyle: "color:blue;font-weight:800;",
                            id: 'TOTALTIERP',
                            value: "0"

                        },
                        {
                            xtype: 'displayfield',

                            flex: 0.7,
                            fieldLabel: 'SORTIE::',
                            labelWidth: 60,
                            /*renderer: amountformatbis,*/
                            id: 'TOTALSORTIE',
                            fieldStyle: "color:red;font-weight:800;",
                            // margin: '0 5 15 15',
                            value: "0"

                        },
                        {
                            xtype: 'displayfield',

                            flex: 0.7,
                            fieldLabel: 'ENTREE::',
                            labelWidth: 60,
                            renderer: amountformatbis,
                            id: 'TOTALENTRE',
                            fieldStyle: "color:green;font-weight:800;",
                            // margin: '0 5 15 15',
                            value: "0"

                        }

                    ]
                },
                {
                    xtype: "toolbar",
                    dock: 'bottom',
                    items: [

                        int_TOTAL_ESPECE,
                        int_TOTAL_CHEQUE,
                        int_TOTAL_OTHER,
                        totalPanierMoyen,
                        totalPayant
                    ]
                }
            ]
        });

        this.callParent();

        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        });
        Ext.Ajax.request({
            url: '../webservices/sm_user/balance/ws_mvts.jsp',
            method: 'POST',

            params: {
                dt_Date_Debut: Ext.getCmp('dt_debut_journal').getSubmitValue(),
                dt_Date_Fin: Ext.getCmp('dt_fin_journal').getSubmitValue()
            },
            success: function (response, options) {

                var object = Ext.JSON.decode(response.responseText, false);
                var TOTALFOND = 0, TOTALDIF = 0, TOTALENTRE = 0, TOTALSORTIE = 0, TOTALTIERP = 0;
                Ext.each(object.data, function (v, i) {
                    if (v.IDMVT == "1") {
                        TOTALFOND = v.MONTANT;
                    } else if (v.IDMVT == "2") {
                        TOTALDIF = v.MONTANT;
                    } else if (v.IDMVT == "3") {
                        TOTALTIERP = v.MONTANT;
                    } else if (v.IDMVT == "4") {
                        TOTALSORTIE = v.MONTANT;
                    } else if (v.IDMVT == "5") {
                        TOTALENTRE = v.MONTANT;
                    }
                });
                Ext.getCmp('TOTALFOND').setValue(TOTALFOND);
                Ext.getCmp('TOTALDIF').setValue(TOTALDIF);
                Ext.getCmp('TOTALENTRE').setValue(TOTALENTRE);
                Ext.getCmp('TOTALTIERP').setValue(TOTALTIERP);
                Ext.getCmp('TOTALSORTIE').setValue(TOTALSORTIE);

            }, failure: function (response, options) {




            }
        });




    },
    loadStore: function () {
        this.getStore().load({
            callback: this.onStoreLoad
        });
    },
    onStoreLoad: function () {

        if (Ext.getCmp('gridBalanceVenteCaisse').getStore().getCount() > 0) {
            var totalEspece = 0;
            var totalPanierMoyen = 0;
            var int_TOTAL_CHEQUE = 0;
            var int_TOTAL_VENTE = 0;
            var totalPayant = 0;
            var TOTALANNULEESP = 0, TOTALACHAT = 0, TOTALANNULE = 0, TOTALRATIO = 0, TOTALMARGE = 0, TOTALFOND = 0, TOTALDIF = 0, TOTALENTRE = 0, TOTALTIERP = 0, TOTALSORTIE = 0, TOTALVENTE = 0, TOTALVENTE = 0;

            Ext.getCmp('gridBalanceVenteCaisse').getStore().each(function (rec) {


                totalEspece += parseInt(rec.get('TOTAL_ESPECE'));
                int_TOTAL_CHEQUE += parseInt(rec.get('TOTAL_CHEQUE'));
                int_TOTAL_VENTE += parseInt(rec.get('NB'));
//                totalPanierMoyen += parseInt(rec.get('PANIER_MOYEN')); //a decommenter en cas de probleme. 28/05/2016
                totalPanierMoyen = rec.get('VENTE_NET_BIS');

                totalPayant += parseInt(rec.get('NB'));
                TOTALACHAT = rec.get('TOTALACHAT');

                TOTALRATIO = rec.get('TOTALRATIO');
                TOTALMARGE = rec.get('TOTALMARGE');

                TOTALVENTE = rec.get('TOTALVENTE');


                //fin code ajouté 01/12/2016
            });



            //code ajouté 01/12/2016
            Ext.getCmp('TOTALVENTE').setValue(TOTALVENTE);
            Ext.getCmp('TOTALACHAT').setValue(TOTALACHAT);
            Ext.getCmp('TOTALMARGE').setValue(TOTALMARGE);
            Ext.getCmp('TOTALRATIO').setValue(TOTALRATIO);

        }



        Ext.getCmp('int_TOTAL_ESPECE').setValue(totalEspece);
        Ext.getCmp('int_TOTAL_CHEQUE').setValue(int_TOTAL_CHEQUE);
        Ext.getCmp('int_TOTAL_OTHER').setValue(totalEspece);

        Ext.getCmp('totalPanierMoyen').setValue(totalPanierMoyen);
//Ext.getCmp('totalPanierMoyen').setValue(totalPanierMoyen / (totalPayant > 0 ? totalPayant : 1));
        Ext.getCmp('totalPayant').setValue(int_TOTAL_VENTE);
        Ext.getCmp('int_TOTAL_OTHER').setValue(totalEspece);
    },
    onRechClick: function () {
        if (new Date(Ext.getCmp('dt_debut_journal').getSubmitValue()) > new Date(Ext.getCmp('dt_fin_journal').getSubmitValue())) {
            Ext.MessageBox.alert('Erreur au niveau date', 'La date de d&eacute;but doit &ecirc;tre inf&eacute;rieur &agrave; la date fin');
            return;
        }
        this.getStore().load({
            params: {
                dt_start: Ext.getCmp('dt_debut_journal').getSubmitValue(),
                dt_end: Ext.getCmp('dt_fin_journal').getSubmitValue()
            }
        });
        Ext.Ajax.request({
            url: '../webservices/sm_user/balance/ws_mvts.jsp',
            method: 'POST',

            params: {
                dt_Date_Debut: Ext.getCmp('dt_debut_journal').getSubmitValue(),
                dt_Date_Fin: Ext.getCmp('dt_fin_journal').getSubmitValue()
            },

            success: function (response, options) {

                var object = Ext.JSON.decode(response.responseText, false);
                var TOTALFOND = 0, TOTALDIF = 0, TOTALENTRE = 0, TOTALSORTIE = 0, TOTALTIERP = 0;
                Ext.each(object.data, function (v, i) {
                    if (v.IDMVT == "1") {
                        TOTALFOND = v.MONTANT;
                    } else if (v.IDMVT == "2") {
                        TOTALDIF = v.MONTANT;
                    } else if (v.IDMVT == "3") {
                        TOTALTIERP = v.MONTANT;
                    } else if (v.IDMVT == "4") {
                        TOTALSORTIE = v.MONTANT;
                    } else if (v.IDMVT == "5") {
                        TOTALENTRE = v.MONTANT;
                    }
                });
                Ext.getCmp('TOTALFOND').setValue(TOTALFOND);
                Ext.getCmp('TOTALDIF').setValue(TOTALDIF);
                Ext.getCmp('TOTALENTRE').setValue(TOTALENTRE);
                Ext.getCmp('TOTALTIERP').setValue(TOTALTIERP);
                Ext.getCmp('TOTALSORTIE').setValue(TOTALSORTIE);

            }, failure: function (response, options) {




            }
        });
    },
    onPdfClick: function () {

        var chaine = location.pathname;
        var reg = new RegExp("[/]+", "g");
        var tableau = chaine.split(reg);
        var sitename = tableau[1];
//        var linkUrl = url_services_data_balancevente_generate_pdf + '?dt_Date_Debut=' + Ext.getCmp('dt_debut_journal').getSubmitValue() + '&dt_Date_Fin=' + Ext.getCmp('dt_fin_journal').getSubmitValue();
        var linkUrl = '../myBean?action=balancepdf&dt_start=' + Ext.getCmp('dt_debut_journal').getSubmitValue() + '&dt_end=' + Ext.getCmp('dt_fin_journal').getSubmitValue();

        window.open(linkUrl);
    }


});