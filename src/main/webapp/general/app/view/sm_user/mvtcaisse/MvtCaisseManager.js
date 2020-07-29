var url_services_data_mvtcaisse = '../webservices/sm_user/mvtcaisse/ws_data.jsp';
var url_services_data_utilisateur = '../webservices/sm_user/utilisateur/ws_data.jsp';
var url_services_data_listeCaisse_generate_pdf = '../webservices/sm_user/listacaisse/ws_generate_pdf.jsp';

var dt_Date_Debut = "";
var dt_Date_Fin = "";
var Me;

//var url_services_data_mvtcaisse = '../webservices/sm_user/cashtransactiondata/ws_data.jsp';
Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

function amountformatbis(val) {
    return amountformat(val) + " F CFA";
}

Ext.define('testextjs.view.sm_user.mvtcaisse.MvtCaisseManager', {
    extend: 'Ext.grid.Panel',
    xtype: 'mvtcaissemanager',
    id: 'mvtcaissemanagerID',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.window.Window',
        'Ext.data.*',
        'Ext.util.*',
        'Ext.form.*',
        'Ext.JSON.*',
        'testextjs.model.Cashtransactiondata',
        'Ext.ux.ProgressBarPager',
        'Ext.ux.grid.Printer'

    ],
    title: 'Liste Des Mouvements De Caisse',
    closable: false,
    frame: true,
    initComponent: function() {

        var itemsPerPage = 20;
        Me = this;
        dt_Date_Debut = "";
        dt_Date_Fin = "";
        var store = new Ext.data.Store({
            model: 'testextjs.model.Cashtransactiondata',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_mvtcaisse,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });

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
                    fieldStyle: "color:blue;",
                    // margin: '0 5 15 15',
                    value: "0"


                });
        var int_TOTAL = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    //allowBlank: false,
                    flex: 0.7,
                    fieldLabel: 'TOTAL::',
                    fieldWidth: 70,
                    name: 'int_TOTAL',
                    id: 'int_TOTAL',
                    renderer: amountformatbis,
                    fieldStyle: "color:blue;",
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
                    fieldStyle: "color:blue;",
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
                    name: 'int_TOTAL_OTHER',
                    id: 'int_TOTAL_OTHER',
                    renderer: amountformatbis,
                    fieldStyle: "color:blue;",
                    // margin: '0 5 15 15',
                    value: "0"


                });


        this.cellEditing = new Ext.grid.plugin.CellEditing({
            clicksToEdit: 1
        });

        Ext.apply(this, {
            width: '98%',
            height: 580,
            id: 'gridmvtcaisseid',
            plugins: [this.cellEditing],
            store: store,
            columns: [{
                    header: 'Type Mouvement',
                    dataIndex: 'str_TRANSACTION_REF',
                    flex: 1
                }, {
                    header: 'Num&eacute;ro Comptable',
                    dataIndex: 'str_mt_clt',
                    flex: 1
                }, {
                    header: 'Reference',
                    dataIndex: 'str_ref',
                    flex: 1
               },
                {
                    header: 'Op&eacute;rateur',
                    dataIndex: 'str_vendeur',
                    flex: 1
                }, {
                    header: 'Date',
                    dataIndex: 'str_date',
                    flex: 0.7
                }, {
                    header: 'Heure',
                    dataIndex: 'str_hour',
                    flex: 0.7

                }, {
                    header: 'Mode.R&egrave;glement',
                    dataIndex: 'str_FAMILLE_ITEM',
                    flex: 1
                }, {
                    header: 'Montant',
                    dataIndex: 'str_mt_vente',
                    align: 'right',
//                    renderer: amountformat,
                    flex: 1
                }, {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
//                    hidden: true,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/paste_plain.png',
                            tooltip: 'Voir le detail',
                            scope: this,
                            
                            handler: this.ModifyClick,
                            getClass: function (value, metadata, record) {
                                if (record.get('lg_MVT_CAISSE_ID') != "") {  //read your condition from the record
                                    return 'x-display-hide'; //affiche l'icone
                                } else {
                                    return 'x-hide-display'; //cache l'icone
                                }
                            }
                        }]
                }


            ],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [
                /*{
                 xtype: 'datefield',
                 fieldLabel: 'Du',
                 name: 'dt_debut',
                 id: 'dt_debut',
                 allowBlank: false,
                 submitFormat: 'Y-m-d',
                 maxValue: new Date(),
                 format: 'd/m/Y',
                 listeners: {
                 'change': function (me) {
                 // alert(me.getSubmitValue());
                 dt_Date_Debut = me.getSubmitValue();
                 Ext.getCmp('dt_fin').setMinValue(me.getValue());
                 }
                 }
                 }, {
                 xtype: 'datefield',
                 fieldLabel: 'Au',
                 name: 'dt_fin',
                 id: 'dt_fin',
                 allowBlank: false,
                 maxValue: new Date(),
                 submitFormat: 'Y-m-d',
                 format: 'd/m/Y',
                 listeners: {
                 'change': function (me) {
                 dt_Date_Fin = me.getSubmitValue();
                 Ext.getCmp('dt_debut').setMaxValue(me.getValue());
                 }
                 }
                 }*/

                {
                    text: 'Creer',
                    tooltip: 'Cr&eacute;er',
                    scope: this,
                    iconCls: 'addicon',
                    handler: this.onAddClick
                },
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
                        'change': function(me) {
                            dt_Date_Debut = me.getSubmitValue();
                            Ext.getCmp('dt_fin_journal').setMinValue(me.getValue());
                            Ext.getCmp('gridmvtcaisseid').getStore().getProxy().url = url_services_data_mvtcaisse + "?dt_Date_Debut=" + dt_Date_Debut;

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
                        'change': function(me) {
                            dt_Date_Fin = me.getSubmitValue();
                            Ext.getCmp('dt_debut_journal').setMaxValue(me.getValue());
                            Ext.getCmp('gridmvtcaisseid').getStore().getProxy().url = url_services_data_mvtcaisse + "?dt_Date_Debut=" + dt_Date_Debut + "&dt_Date_Fin=" + dt_Date_Fin;

                        }
                    }
                }, {
                    xtype: 'combobox',
                    fieldLabel: 'Utilisateur',
                    name: 'lg_USER_ID',
                    id: 'lg_USER_ID',
                    hidden: true,
                    store: storeUser,
                    pageSize: 20, //ajout la barre de pagination
                    valueField: 'lg_USER_ID',
                    displayField: 'str_FIRST_NAME',
                    typeAhead: true,
                    queryMode: 'remote',
                    emptyText: 'Choisir un utilisateur...',
                    listeners: {
                        select: function(cmp) {
                            Me.onRechClick();
                        }
                    }
                }, {
                    text: 'rechercher',
                    tooltip: 'rechercher',
                    scope: this,
                    iconCls: 'searchicon',
                    handler: this.onRechClick
                },
            
            , {
                    xtype: 'tbseparator'
                }
               

                ,
                {
                    width: 100,
                    xtype: 'button',
                    text: 'Imprimer',
                    iconCls: 'printable',
//                    glyph: 0xf1c1,
                    listeners: {
                        click: function () {

                            var dt_start_vente = Ext.getCmp('dt_debut_journal').getSubmitValue();
                            var dt_end_vente = Ext.getCmp('dt_fin_journal').getSubmitValue();
                           var linkUrl = "../webservices/sm_user/mvtcaisse/ws_generate_mvt_pdf.jsp" + "?dt_start=" + dt_start_vente + "&dt_end=" + dt_end_vente ;
                            window.open(linkUrl);

                        }
                    }


                }
            
            
            
            
            ],
            bbar: {
                /*xtype: 'pagingtoolbar',
                 store: store, // same store GridPanel is using*/
                dock: 'bottom',
                items: [
                    {
                        xtype: 'pagingtoolbar',
                        displayInfo: true,
                        flex: 2,
                        pageSize: itemsPerPage,
                        store: store // same store GridPanel is using
                    },
                    {
                        xtype: 'tbseparator'
                    },
                    int_TOTAL_ESPECE,
                    int_TOTAL_CHEQUE,
                    int_TOTAL_OTHER,
                    int_TOTAL
                ]
            }
        });

        this.callParent();

        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        });



        this.on('edit', function(editor, e) {

        });


    },
    loadStore: function() {
        this.getStore().load({
            callback: this.onStoreLoad
        });
    },
    onStoreLoad: function() {
        if (this.getStore().getCount() > 0) {
            var int_TOTAL = 0;
            var int_TOTAL_CHEQUE = 0;
            var int_TOTAL_OTHER = 0;
            var int_TOTAL_ESPECE = 0;
            this.getStore().each(function(rec) {

                if (rec.get('str_FAMILLE_ITEM') == "Especes") {

                    int_TOTAL_ESPECE += rec.get('int_PRICE');
                } else if (rec.get('str_FAMILLE_ITEM') == "Cheques") {
                    int_TOTAL_CHEQUE += rec.get('int_PRICE');
                } else {

                    int_TOTAL_OTHER += rec.get('int_PRICE');
                }
                int_TOTAL = rec.get('int_PRICE_TOTAL');

            });


        }

        Ext.getCmp('int_TOTAL_ESPECE').setValue(int_TOTAL_ESPECE);
        Ext.getCmp('int_TOTAL_CHEQUE').setValue(int_TOTAL_CHEQUE);
        Ext.getCmp('int_TOTAL_OTHER').setValue(int_TOTAL_OTHER);
        Ext.getCmp('int_TOTAL').setValue(int_TOTAL);
    },
    onRechClick: function() {
//        var dt_Date_Debut = "";
//        var dt_Date_Fin = "";
//        var lg_USER_ID = "";
//        if (Ext.getCmp('dt_debut_journal').getSubmitValue() != null) {
//            dt_Date_Debut = Ext.getCmp('dt_debut_journal').getSubmitValue();
//        }
//
//        if (Ext.getCmp('dt_fin_journal').getSubmitValue() != null) {
//            dt_Date_Fin = Ext.getCmp('dt_fin_journal').getSubmitValue();
//        }
        if (new Date(dt_Date_Debut) > new Date(dt_Date_Fin)) {
            Ext.MessageBox.alert('Erreur au niveau date', 'La date de d&eacute;but doit &ecirc;tre inf&eacute;rieur &agrave; la date fin');
            return;
        }


//        if (Ext.getCmp('lg_USER_ID').getValue() != null) {
//            lg_USER_ID = Ext.getCmp('lg_USER_ID').getValue();
//        }

        this.getStore().load({
            params: {
                dt_Date_Debut: dt_Date_Debut,
                dt_Date_Fin: dt_Date_Fin
            }
        }, url_services_data_mvtcaisse);
    },
    onPdfClick: function() {
        var dt_Date_Debut_search_val = Ext.Date.format(Ext.getCmp('dt_debut').getValue(), 'Y-m-d');
        var dt_Date_Fin_search_val = Ext.Date.format(Ext.getCmp('dt_fin').getValue(), 'Y-m-d');
        //var h_debut_search_val = Ext.getCmp('h_debut').getValue();
        // var h_fin_search_val = Ext.getCmp('h_fin').getValue();

        /*  alert('dt_Date_Debut_search_val ' + dt_Date_Debut_search_val);
         alert('dt_Date_Fin_search_val ' + dt_Date_Fin_search_val);
         alert('h_debut_search_val ' + h_debut_search_val);*/

        // alert(onPdfClick);
        var chaine = location.pathname;
        var reg = new RegExp("[/]+", "g");
        var tableau = chaine.split(reg);
        var sitename = tableau[1];
        var linkUrl = url_services_data_listeCaisse_generate_pdf + '?dt_Date_Debut=' + dt_Date_Debut_search_val + '&dt_Date_Fin=' + dt_Date_Fin_search_val;


        window.open(linkUrl);
    },
    onAddClick: function() {
        new testextjs.view.sm_user.mvtcaisse.action.add({
            odatasource: "",
            parentview: this,
            mode: "create",
            titre: "Effectuer Mouvement de Caisse"
        });
    },
    ModifyClick: function(grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        new testextjs.view.sm_user.mvtcaisse.action.add({
            odatasource: rec.data,
            parentview: this,
            mode: "update",
            titre: "Modification Mouvement  [" + rec.get('str_ref') + "]"
            //titre: "Modification Mouvement  [" + rec.get('str_NUM_PIECE_COMPTABLE') + "]"
        });



    },
    onRemoveClick: function(grid, rowIndex) {
        Ext.MessageBox.confirm('Message',
                'confirm la suppresssion',
                function(btn) {
                    if (btn === 'yes') {
                        var rec = grid.getStore().getAt(rowIndex);
                        Ext.Ajax.request({
                            url: url_services_transaction_preenregistrement + 'delete',
                            params: {
                                lg_PREENREGISTREMENT_ID: rec.get('lg_PREENREGISTREMENT_ID')
                            },
                            success: function(response)
                            {
                                var object = Ext.JSON.decode(response.responseText, false);
                                if (object.success === 0) {
                                    Ext.MessageBox.alert('Error Message', object.errors);
                                    return;
                                }
                                grid.getStore().reload();
                            },
                            failure: function(response)
                            {

                                var object = Ext.JSON.decode(response.responseText, false);
                                //  alert(object);

                                console.log("Bug " + response.responseText);
                                Ext.MessageBox.alert('Error Message', response.responseText);

                            }
                        });
                        return;
                    }
                });


    },
    onEditClick: function(grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        new testextjs.view.sm_user.preenregistrement.action.add({
            odatasource: rec.data,
            parentview: this,
            mode: "update",
            titre: "Modification Preenregistrement  [" + rec.get('str_REF') + "]"
        });



    },
});