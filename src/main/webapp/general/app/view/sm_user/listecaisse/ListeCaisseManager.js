var url_services_data_listecaisse = '../webservices/sm_user/listacaisse/ws_data.jsp';
var url_services_data_utilisateur = '../webservices/sm_user/utilisateur/ws_data.jsp';
var url_services_data_listeCaisse_generate_pdf = '../webservices/sm_user/listacaisse/ws_generate_pdf.jsp';

var Me;

Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

function amountformatbis(val) {
    return amountformat(val) + " F CFA";
}

Ext.define('testextjs.view.sm_user.listecaisse.ListeCaisseManager', {
    extend: 'Ext.grid.Panel',
    xtype: 'listecaissemanager',
    id: 'listecaissemanagerID',
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
    title: 'Liste de Caisse',
    closable: false,
    frame: true,
    initComponent: function() {

        var itemsPerPage = 20;
      
        Me = this;

        var store = new Ext.data.Store({
            model: 'testextjs.model.Cashtransactiondata',
            pageSize: itemsPerPage,
            autoLoad: true,
            timeout: 2460000,
            proxy: {
                type: 'ajax',
                url: url_services_data_listecaisse,
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
            height: valheight,
            id: 'gridlistecaisseid',
            plugins: [this.cellEditing],
            store: store,
            columns: [{
                    header: 'Type Mouvement',
                    dataIndex: 'str_TRANSACTION_REF',
                    flex: 1
                }, {
                    header: 'Reference',
                    dataIndex: 'str_ref',
                    flex: 1
                }, {
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
//                    renderer: amountformat,
                    flex: 1
                }],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [
                {
                    xtype: 'datefield',
                    fieldLabel: 'Du',
                    name: 'dt_Date_Debut',
                    id: 'dt_Date_Debut',
                    allowBlank: false,
                    margin: '0 10 0 0',
                    submitFormat: 'Y-m-d',
                    flex: 1,
                    labelWidth: 50,
                    maxValue: new Date(),
                    format: 'd/m/Y',
                    listeners: {
                        'change': function(me) {
                            Ext.getCmp('dt_Date_Fin').setMinValue(me.getValue());
                        }
                    }
                }, {
                    xtype: 'datefield',
                    fieldLabel: 'Au',
                    name: 'dt_Date_Fin',
                    id: 'dt_Date_Fin',
                    allowBlank: false,
                    labelWidth: 50,
                    flex: 1,
                    maxValue: new Date(),
                    margin: '0 9 0 0',
                    submitFormat: 'Y-m-d',
                    format: 'd/m/Y',
                    listeners: {
                        'change': function(me) {
                            Ext.getCmp('dt_Date_Debut').setMaxValue(me.getValue());
                        }
                    }
                },'-', {
                    xtype: 'timefield',
                    fieldLabel: 'De',
                    name: 'h_debut',
                    id: 'h_debut',
                    emptyText: 'Heure debut(HH:mm)',
                    allowBlank: false,
                    flex: 1,
                    labelWidth: 50,
                    increment: 30,
                    format: 'H:i'
                }, {
                    xtype: 'timefield',
                    fieldLabel: 'A',
                    name: 'h_fin',
                    id: 'h_fin',
                    emptyText: 'Heure fin(HH:mm)',
                    allowBlank: false,
                    labelWidth: 50,
                    increment: 30,
                    flex: 1,
                    format: 'H:i'
                }, '-', {
                    xtype: 'combobox',
                    fieldLabel: 'Type.Reglement',
                    name: 'lg_TYPE_REGLEMENT_ID',
                    id: 'lg_TYPE_REGLEMENT_ID',
                    store: store_typereglement,
                    flex: 2,
                    valueField: 'lg_TYPE_REGLEMENT_ID',
                    displayField: 'str_NAME',
                    typeAhead: true,
                    queryMode: 'remote',
                    allowBlank: false,
                    emptyText: 'Selectionner un type de reglement...',
                    listeners: {
                        select: function(cmp) {
                            Me.onRechClick();                            
                        }
                    }


                }, {
                    xtype: 'combobox',
                    fieldLabel: 'Utilisateur',
                    name: 'lg_USER_ID',
                    id: 'lg_USER_ID',
                    store: storeUser,
//                    width: 300,
flex: 2,
                    pageSize: 20, //ajout la barre de pagination
                    valueField: 'lg_USER_ID',
                    displayField: 'str_FIRST_LAST_NAME',
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
                    iconCls: 'searchicon',
                    scope: this,
                    handler: this.onRechClick
                }, {
                    text: 'Imprimer',
                    iconCls: 'printable',
                    tooltip: 'Imprimer',
                    scope: this,
                    handler: this.onPdfClick
                }],
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
                        store: store , // same store GridPanel is using
                        listeners: {
                            beforechange: function(page, currentPage) {
                                var myProxy = this.store.getProxy();
                                myProxy.params = {
                                    dt_Date_Debut: '',
                                    dt_Date_Fin: '',
                                    h_debut: '',
                                    h_fin: '',
                                    lg_USER_ID: '',
                                    lg_TYPE_REGLEMENT_ID: ''
                                };

                                myProxy.setExtraParam('lg_TYPE_REGLEMENT_ID', Ext.getCmp('lg_TYPE_REGLEMENT_ID').getValue());
                                myProxy.setExtraParam('dt_Date_Debut', Ext.getCmp('dt_Date_Debut').getSubmitValue());
                                myProxy.setExtraParam('dt_Date_Fin', Ext.getCmp('dt_Date_Fin').getSubmitValue());
                                myProxy.setExtraParam('h_debut', Ext.getCmp('h_debut').getSubmitValue());
                                myProxy.setExtraParam('h_fin', Ext.getCmp('h_fin').getSubmitValue());
                                myProxy.setExtraParam('lg_USER_ID', Ext.getCmp('lg_USER_ID').getValue());
                            }

                        }
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
        })



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

        if (new Date(Ext.getCmp('dt_Date_Debut').getSubmitValue()) > new Date(Ext.getCmp('dt_Date_Fin').getSubmitValue())) {
            Ext.MessageBox.alert('Erreur au niveau date', 'La date de d&eacute;but doit &ecirc;tre inf&eacute;rieur &agrave; la date fin');
            return;
        }

        this.getStore().load({
            params: {
                dt_Date_Debut: Ext.getCmp('dt_Date_Debut').getSubmitValue(),
                dt_Date_Fin: Ext.getCmp('dt_Date_Fin').getSubmitValue(),
                h_debut: (Ext.getCmp('h_debut').getSubmitValue() != null ? Ext.getCmp('h_debut').getSubmitValue() : ""),
                h_fin: (Ext.getCmp('h_fin').getSubmitValue() != null ? Ext.getCmp('h_fin').getSubmitValue() : ""),
                lg_USER_ID: (Ext.getCmp('lg_USER_ID').getValue() != null ? Ext.getCmp('lg_USER_ID').getValue() : ""),
                lg_TYPE_REGLEMENT_ID: (Ext.getCmp('lg_TYPE_REGLEMENT_ID').getValue() != null ? Ext.getCmp('lg_TYPE_REGLEMENT_ID').getValue() : "")
            }
        }, url_services_data_listecaisse);
    },
    onPdfClick: function() {
       // var linkUrl = url_services_data_listeCaisse_generate_pdf + '?dt_Date_Debut=' + dt_Date_Debut + '&dt_Date_Fin=' + dt_Date_Fin + "&lg_USER_ID=" + lg_USER_ID + "&h_debut="+h_debut + "&h_fin="+h_fin+"&lg_TYPE_REGLEMENT_ID="+lg_TYPE_REGLEMENT_ID;
         var linkUrl = url_services_data_listeCaisse_generate_pdf + '?dt_Date_Debut=' + Ext.getCmp('dt_Date_Debut').getSubmitValue()
                + '&dt_Date_Fin=' + Ext.getCmp('dt_Date_Fin').getSubmitValue()
                + "&h_debut=" + (Ext.getCmp('h_debut').getSubmitValue() != null ? Ext.getCmp('h_debut').getSubmitValue() : "")
                + "&h_fin=" + (Ext.getCmp('h_fin').getSubmitValue() != null ? Ext.getCmp('h_fin').getSubmitValue() : "")
                + "&lg_TYPE_REGLEMENT_ID=" + (Ext.getCmp('lg_TYPE_REGLEMENT_ID').getValue() != null ? Ext.getCmp('lg_TYPE_REGLEMENT_ID').getValue() : "")+"&lg_USER_ID="+(Ext.getCmp('lg_USER_ID').getValue() != null ? Ext.getCmp('lg_USER_ID').getValue() : "");

        window.open(linkUrl);
    }


});