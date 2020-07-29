/* global Ext, valheight */

var url_services_data_articlevendu = '../webservices/sm_user/famille/ws_data_article_vendu.jsp';
var url_services_data_articlevendu_generate_pdf = '../webservices/sm_user/famille/ws_generate_articlevendu_pdf.jsp';
var url_services_transaction_suggerercde = '../webservices/sm_user/suggerercde/ws_transaction.jsp?mode=';
var url_services_data_utilisateur = '../webservices/sm_user/utilisateur/ws_data.jsp';
var dt_Date_Debut;
var dt_Date_Fin;
var h_debut;
var h_fin;
var Me;
var lg_USER_ID;
var str_TYPE_TRANSACTION;
var int_NUMBER;
var record;
var lg_FAMILLE_ID;

Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

function amountformatbis(val) {
    return amountformat(val) + " F CFA";
}

Ext.define('testextjs.view.configmanagement.famille.action.detailArticleVendus', {
    extend: 'Ext.window.Window',
    xtype: 'detailArticleVendus',
    id: 'detailArticleVendus_ID',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.window.Window',
        'Ext.data.*',
        'Ext.util.*',
        'Ext.form.*',
        'Ext.JSON.*',
        'testextjs.model.Famille',
        'Ext.ux.ProgressBarPager'

    ],
    config: {
        odatasource: '',
        parentview: '',
        mode: '',
        titre: '',
        obtntext: '',
        nameintern: '',
        dt_Date_Debut: '',
        dt_Date_Fin: '',
        h_debut: '',
        h_fin: '',
        type_transaction: '',
        int_NUMBER: '',
        record: '',
        lg_FAMILLE_ID: ''

    },
    plain: true,
    maximizable: true,
    //tools: [{type: "pin"}],
    closable: false,
    frame: true,
    initComponent: function () {

        Me = this;
        lg_USER_ID = "";

        dt_Date_Debut = this.getDt_Date_Debut();
        dt_Date_Fin = this.getDt_Date_Fin();
        h_debut = this.getH_debut();
        h_fin = this.getH_fin();
        int_NUMBER = this.getInt_NUMBER();
        str_TYPE_TRANSACTION = this.getType_transaction();
        lg_FAMILLE_ID = this.getLg_FAMILLE_ID();
        var itemsPerPage = 20;
        console.log(lg_FAMILLE_ID);

        console.log(dt_Date_Debut);
        console.log(dt_Date_Fin);
        console.log(int_NUMBER);
        console.log(str_TYPE_TRANSACTION);
        /*
         var int_TOTAL = new Ext.form.field.Display({
         xtype: 'displayfield',
         flex: 0.7,
         fieldLabel: 'TOTAL::',
         fieldWidth: 70,
         name: 'int_TOTAL_DETAIL',
         id: 'int_TOTAL_DETAIL_ID',
         renderer: amountformatbis,
         fieldStyle: "color:blue;",
         value: 0
         });
         */
        var storeUser = new Ext.data.Store({
            model: 'testextjs.model.Utilisateur',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_utilisateur,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }
        });
        var store = new Ext.data.Store({
            model: 'testextjs.model.Famille',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_articlevendu, //+ '?lg_FAMILLE_ID='+lg_FAMILLE_ID+'&h_debut='+h_debut+'&h_fin='+h_fin+'&dt_Date_Debut='+dt_Date_Debut+'&dt_Date_Fin='+dt_Date_Fin+'&int_NUMBER='+int_NUMBER+'&str_TYPE_TRANSACTION='+str_TYPE_TRANSACTION,

                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });

        store.load({
            params: {
                lg_FAMILLE_ID: lg_FAMILLE_ID,
                h_debut: h_debut,
                h_fin: h_fin,
                dt_Date_Debut: dt_Date_Debut,
                dt_Date_Fin: dt_Date_Fin,
                int_NUMBER: int_NUMBER,
                str_TYPE_TRANSACTION: str_TYPE_TRANSACTION
                        //str_TYPE_TRANSACTION:'ALL'
            }
        });



        var store_type = new Ext.data.Store({
            fields: ['str_TYPE_TRANSACTION', 'str_desc'],
            data: [{str_TYPE_TRANSACTION: 'LESS', str_desc: 'Inferieur a'}, {str_TYPE_TRANSACTION: 'MORE', str_desc: 'Superieur a'}, {str_TYPE_TRANSACTION: 'EQUAL', str_desc: 'Egal a'},
                {str_TYPE_TRANSACTION: 'LESSOREQUAL', str_desc: 'Inferieur ou egal a'}, {str_TYPE_TRANSACTION: 'MOREOREQUAL', str_desc: 'Superieur ou egal a'}]
        });

        this.cellEditing = new Ext.grid.plugin.CellEditing({
            clicksToEdit: 1
        });


        var form = new Ext.form.Panel({
            width: 1050,
            layout: {
                type: 'hbox'
            },
            defaults: {
                flex: 1
            },
            autoHeight: true,
            fieldDefaults: {
                labelAlign: 'left',
                labelWidth: 90,
                anchor: '100%',
                msgTarget: 'side'
            },
            items: [{
                    columnWidth: 0.65,
                    xtype: 'gridpanel',
                    id: 'DetailArticleVendusPanelID',
                    store: store,
                    height: 400,
                    columns: [
                        {
                            header: 'lg_FAMILLE_ID',
                            dataIndex: 'lg_FAMILLE_ID',
                            hidden: true,
                            flex: 1
                        },
                        {
                            xtype: 'rownumberer',
                            text: 'Num',
                            width: 45,
                            sortable: true
                        },
                        /*{
                            header: 'CIP',
                            dataIndex: 'int_CIP',
                            flex: 0.8
                        },
                        {
                            header: 'Designation',
                            dataIndex: 'str_DESCRIPTION',
                            flex: 2.5
                        },*/
                        {
                            header: 'Date',
                            dataIndex: 'dt_UPDATED',
                            flex: 0.8
                        },
                        {
                            header: 'Heure',
                            dataIndex: 'lg_ETAT_ARTICLE_ID',
                            flex: 0.7
                        },
                        {
                            header: 'Qte Vd',
                            dataIndex: 'int_NUMBER_AVAILABLE',
                            flex: 0.6,
                            align: 'center'
                        },
                        {
                            header: 'Prix',
                            dataIndex: 'int_PRICE',
                            renderer: amountformat,
                            align: 'right',
                            flex: 0.8
                        }, {
                            header: 'Stock',
                            dataIndex: 'int_NUMBER',
                            flex: 0.6,
                            align: 'center'
                        }, {
                            header: 'Ticket',
                            dataIndex: 'int_T',
                            flex: 1
                        }, {
                            header: 'Type.Vente',
                            dataIndex: 'str_NAME',
                            flex: 1,
                            align: 'center'
                        },
                        {
                            header: 'Operateur',
                            dataIndex: 'lg_AJUSTEMENTDETAIL_ID',
                            flex: 1.5
                        }
                    ],
                    tbar: [
                        {
                            xtype: 'textfield',
                            id: 'rechercher',
//                           flex: 0.4,
                            width: 300,
                            emptyText: 'Rech',
                            enableKeyEvents: true,
                            listeners: {
                                specialKey: function (field, e, options) {
                                    if (e.getKey() === e.ENTER) {

                                        Me.onRechClick();
                                    }
                                }


                            }


                        }, {
                            text: 'rechercher',
                            tooltip: 'rechercher',
                            iconCls: 'searchicon',
                            scope: this,
                            handler: this.onRechClick
                        }],
                    listeners: {
                        scope: this},
                    bbar: {
                        xtype: 'pagingtoolbar',
                        store: store,
                        dock: 'bottom',
                        displayInfo: true, // same store GridPanel is using
                        listeners: {
                            beforechange: function (page, currentPage) {
                                var myProxy = this.store.getProxy();
                                myProxy.params = {
                                    search_value: '',
                                    lg_FAMILLE_ID: lg_FAMILLE_ID,
                                    h_debut: h_debut,
                                    h_fin: h_fin,
                                    dt_Date_Debut: dt_Date_Debut,
                                    dt_Date_Fin: dt_Date_Fin,
                                    int_NUMBER: int_NUMBER,
                                    str_TYPE_TRANSACTION: str_TYPE_TRANSACTION
                                };
                                myProxy.setExtraParam('search_value', Ext.getCmp('rechecher').getValue());
                                myProxy.setExtraParam('lg_FAMILLE_ID', lg_FAMILLE_ID);
                                myProxy.setExtraParam('h_debut', h_debut);
                                myProxy.setExtraParam('h_fin', h_fin);
                                myProxy.setExtraParam('dt_Date_Debut', dt_Date_Debut);
                                myProxy.setExtraParam('dt_Date_Fin', dt_Date_Fin);
                                myProxy.setExtraParam('int_NUMBER', int_NUMBER);
                                myProxy.setExtraParam('str_TYPE_TRANSACTION', str_TYPE_TRANSACTION);
                                
                            }

                        }
                    }
                }]
        });

        this.callParent();

        var win = new Ext.window.Window({
            autoShow: true,
            id: 'detailArticle_Vendus_ID',
            title: this.getTitre(),
            width: 1200,
            Height: 500,
            layout: 'fit',
            plain: true,
            items: form,
            buttons: [{
                    text: 'Fermer',
                    handler: function () {
                        win.close();
                    }
                }]
        });

    },
    loadStore: function () {
        this.getStore().load({
            callback: this.onStoreLoad
        });
    },
    onStoreLoad: function () {
        var int_TOTAL = 0;
        if (this.getStore().getCount() > 0) {
            this.getStore().each(function (rec) {
                int_TOTAL += parseInt(rec.get('int_PRICE'));
            });
        }
        Ext.getCmp('int_TOTAL').setValue(int_TOTAL);
    },
    onRechClick: function () {
        var val = Ext.getCmp('rechecher');
        if (Ext.getCmp('int_NUMBER').getValue() !== null) {
            int_NUMBER = Ext.getCmp('int_NUMBER').getValue();
        }
        if (new Date(dt_Date_Debut) > new Date(dt_Date_Fin)) {
            Ext.MessageBox.alert('Erreur au niveau date', 'La date de d&eacute;but doit &ecirc;tre inf&eacute;rieur &agrave; la date fin');
            return;
        }

        this.getStore().load({
            params: {
                dt_Date_Debut: dt_Date_Debut,
                dt_Date_Fin: dt_Date_Fin,
                lg_USER_ID: lg_USER_ID,
                search_value: val.getValue(),
                str_TYPE_TRANSACTION: str_TYPE_TRANSACTION,
                int_NUMBER: int_NUMBER

            }
        }, url_services_data_articlevendu);
    },
    onPdfClick: function () {

        var chaine = location.pathname;
        var reg = new RegExp("[/]+", "g");
        var tableau = chaine.split(reg);
        var sitename = tableau[1];
        var linkUrl = url_services_data_articlevendu_generate_pdf + '?dt_Date_Debut=' + dt_Date_Debut + '&dt_Date_Fin=' + dt_Date_Fin + "&h_debut=" + h_debut + "&h_fin=" + h_fin + '&search_value=' + Ext.getCmp('rechecher').getValue() + "&str_TYPE_TRANSACTION=" + str_TYPE_TRANSACTION + '&int_NUMBER=' + Ext.getCmp('int_NUMBER').getValue();


        window.open(linkUrl);
    },
    onSuggereClick: function () {
        var val = Ext.getCmp('rechecher');
        testextjs.app.getController('App').ShowWaitingProcess();
        Ext.Ajax.request({
            url: url_services_transaction_suggerercde + 'sendProductSellToSuggestion',
            params: {
                dt_Date_Debut: dt_Date_Debut,
                dt_Date_Fin: dt_Date_Fin,
                h_debut: h_debut,
                h_fin: h_fin,
                lg_USER_ID: lg_USER_ID,
                search_value: val.getValue(),
                str_TYPE_TRANSACTION: str_TYPE_TRANSACTION,
                int_NUMBER: int_NUMBER
            },
            timeout: 1800000,
            success: function (response)
            {
                testextjs.app.getController('App').StopWaitingProcess();
                var object = Ext.JSON.decode(response.responseText, false);

                if (object.success === "0") {
                    Ext.MessageBox.alert('Error Message', object.errors);
                    return;
                } else {
                    Ext.MessageBox.alert('confirmation', object.errors);
                    var OGrid = Ext.getCmp('gridID');
                    OGrid.getStore().reload();
                }
            },
            failure: function (response)
            {
                testextjs.app.getController('App').StopWaitingProcess();
                var object = Ext.JSON.decode(response.responseText, false);
                Ext.MessageBox.alert('Error Message', response.responseText);
            }
        });
    },
    onbtnexportCsv: function () {
        var liste_param = 'dt_Date_Debut:' + dt_Date_Debut + ';dt_Date_Fin:' + dt_Date_Fin + ';h_debut:' + h_debut + ';h_fin:' + h_fin + ';search_value:' + Ext.getCmp('rechecher').getValue() + ';str_TYPE_TRANSACTION:' + str_TYPE_TRANSACTION + ';int_NUMBER:' + Ext.getCmp('int_NUMBER').getValue();
        var extension = 'csv';
//        alert(liste_param);
        window.location = '../MigrationServlet?table_name=TABLE_ORDER_DEPOT' + "&extension=" + extension + "&liste_param=" + liste_param;
    }
});