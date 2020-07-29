/* global Ext */

var url_services_data_etats_list = '../webservices/commandemanagement/etats/ws_data.jsp';
var url_services_data_grossiste = '../webservices/configmanagement/grossiste/ws_data.jsp';
var url_services_transaction_etats = '../webservices/commandemanagement/etats/ws_transaction.jsp?mode=';
var url_services_pdf_bonlivraison = '../webservices/commandemanagement/bonlivraison/ws_generate_pdf.jsp';

var url_services_etatcontrole_pdf = '../webservices/commandemanagement/etats/ws_generate_pdf.jsp';
var Me;
var val;
var str_REF_ORDER = "";
var str_REF_LIVRAISON = "";
var selectedBLs = [];
var selectedBL;
var lg_BON_LIVRAISON_ID;
var int_MHT;
var btnUpdate=false;
Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.commandemanagement.etats.EtatControleManager', {
    extend: 'Ext.grid.Panel',
    xtype: 'etatscontrolemanager',
    id: 'etatscontrolemanagerID',
    frame: true,
    requires: ['testextjs.view.commandemanagement.cmde_passees.action.edit'],

    title: 'Etat de controle des achats',
//    btnUpdate: false,
    plugins: [{
            ptype: 'rowexpander',
            rowBodyTpl: new Ext.XTemplate(
                    '<p> {str_FAMILLE_ITEM}</p>',
                    {
                        formatChange: function (v) {
                            var color = v >= 0 ? 'green' : 'red';
                            return '<span style="color: ' + color + ';">' + Ext.util.Format.usMoney(v) + '</span>';
                        }
                    })
        }],
//    iconCls: 'icon-grid',
    initComponent: function () {
        url_services_data_etats_list = '../webservices/commandemanagement/etats/ws_data.jsp';

        var store_grossiste = new Ext.data.Store({
            model: 'testextjs.model.Grossiste',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_grossiste,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                },
                timeout: 240000
            }

        });
        Me = this;
        Ext.Ajax.request({
            url: '../webservices/commandemanagement/etats/ws_action.jsp',
            params: {
            },
            success: function (response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                btnUpdate = object.BTNUPDATE;
               
            },
            failure: function (response)
            {

                console.log("Bug   AIE ");

            }
        });




        var itemsPerPage = 20;
        var store_etats = new Ext.data.Store({
            model: 'testextjs.model.EtatControle',
            pageSize: itemsPerPage,
            autoLoad: false,
            groupField: 'str_LIBELLE',
            proxy: {
                type: 'ajax',
                url: url_services_data_etats_list,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                },
                timeout: 240000
            }

        });

        Ext.apply(this, {
            width: '98%',
            height: 580,
//            features: [{ftype: 'grouping'}],
            store: store_etats,
            id: 'gridID',
            columns: [
                {
                    xtype: 'rownumberer',
                    text: 'LG',
                    width: 45,
                    sortable: true
                },
                {
                    header: 'GROSSISTE',
                    dataIndex: 'str_LIBELLE',
                    flex: 1.5
                },
                {
                    header: 'NO CMD',
                    dataIndex: 'str_ORDER_REF',
                    flex: 1
                },
                {
                    header: 'REF BL',
                    dataIndex: 'str_BL_REF',
                    align: 'right',
                    flex: 1
                },
                {
                    header: 'Montant HT',
                    dataIndex: 'int_ORDER_PRICE',
                    renderer: amountformat,
                    align: 'right',
                    flex: 1
                },
                {
                    header: 'Montant TVA',
                    dataIndex: 'int_TVA',
                    renderer: amountformat,
                    align: 'right',
                    flex: 1
                },
                {
                    header: 'Montant TTC',
                    dataIndex: 'int_BL_PRICE',
                    renderer: amountformat,
                    align: 'right',
                    flex: 1
                },
                {
                    header: 'QTE CMD',
                    dataIndex: 'int_QTE_CMDE',
                    align: 'center',
                    hidden: true,
                    flex: 1
                },
                {
                    header: 'DATE LIVR',
                    dataIndex: 'dt_DATE_LIVRAISON',
                    flex: 1
                },
                {
                    header: 'DATE ENTREE',
                    dataIndex: 'dt_UPDATED',
                    flex: 1
                },
                {
                    header: 'QTE ENTREE',
                    dataIndex: 'int_NUMBER',
                    hidden: true,
                    flex: 1
                },
                {
                    header: 'MTN AVOIR',
                    dataIndex: 'int_AMOUNT_AVOIR',
                    renderer: amountformat,
                    align: 'right',
                    flex: 1
                },
                {
                    header: 'Operateur',
                    dataIndex: 'lg_USER_ID',
                    flex: 1.5
                },
                {
//                    text: 'S&eacute;lectionner',
                    text: '',
                    width: 30,
                    dataIndex: 'bl_SELECTED',
                    xtype: 'checkcolumn',
                    listeners: {
                        checkChange: this.onCheckChange
                    }

                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/page_white_edit.png',
                            tooltip: 'Modifier les informations du bon de livraison',
                            scope: this,
                            getClass: function (value, metadata, record) {
                                console.log(btnUpdate, 'getClass');
                                if (btnUpdate) {
                                    
                                    return 'x-display-hide';


                                } else {
                                   
                                   return 'x-hide-display';
                                }
                            },

                            handler: this.updateInfoBonlivraison
                        }]
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/excel_csv.png',
                            tooltip: 'Generer le fichier CSV pour les etiquettes',
                            scope: this,
                            handler: this.onbtnexport
                        }]
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/printer.png',
                            tooltip: 'Edition des entrees de reapprovisionnement',
                            scope: this,
                            handler: this.onPdfDetailClick
                        }]
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/paste_plain.png',
                            tooltip: 'Reediter les etiquettes de cette entree en stock',
                            scope: this,
                            handler: this.onPdfEtiquetteClick
                        }]
                }
            ],
            selModel: {
                selType: 'cellmodel'
            },
            tbar: [
                {
                    xtype: 'combobox',
                    name: 'lg_GROSSISTE_ID',
                    margins: '0 0 0 10',
                    id: 'lg_GROSSISTE_ID',
                    store: store_grossiste,
                    //disabled: true,
                    valueField: 'lg_GROSSISTE_ID',
                    displayField: 'str_LIBELLE',
                    typeAhead: true,
                    queryMode: 'remote',
                    flex: 1,
                    emptyText: 'Sectionner grossiste...',
                    listeners: {
                        select: function (cmp) {
                            Me.onRechClick();
                        }
                    }

                },
                {
                    xtype: 'datefield',
                    id: 'datedebut',
                    name: 'datedebut',
                    emptyText: 'Date debut',
                    submitFormat: 'Y-m-d',
                    maxValue: new Date(),
                    flex: 0.7,
                    format: 'd/m/Y',
                    listeners: {
                        'change': function (me) {
                            Ext.getCmp('datefin').setMinValue(me.getValue());
                        }
                    }
                }, {
                    xtype: 'datefield',
                    id: 'datefin',
                    name: 'datefin',
                    emptyText: 'Date fin',
                    maxValue: new Date(),
                    flex: 0.7,
                    submitFormat: 'Y-m-d',
                    format: 'd/m/Y',
                    listeners: {
                        'change': function (me) {
                            Ext.getCmp('datedebut').setMaxValue(me.getValue());
                        }
                    }
                }, '-',
                {
                    xtype: 'textfield',
                    id: 'rechecher',
                    name: 'rechecher',
                    emptyText: 'Rechercher un BL ou une commande',
                    listeners: {
                        'render': function (cmp) {
                            cmp.getEl().on('keypress', function (e) {
                                if (e.getKey() === e.ENTER) {
                                    Me.onRechClick();
                                }
                            });
                        }
                    }
                },
                {
                    text: 'rechercher',
                    tooltip: 'rechercher',
                    iconCls: 'searchicon',
                    scope: this,
                    handler: this.onRechClick
                }, '-',
                {
                    text: 'Imprimer',
                    tooltip: 'Imprimer',
                    iconCls: 'printable',
                    scope: this,
                    handler: this.onPrintClick
                }, '-',
                {
                    text: 'GESTION DES QUINZAINES',
                    scope: this,
                    handler: this.onGestionQuinzaine
                }, '-',
                {
                    text: 'REGLER UNE SELECTION DE BL',
                    scope: this,
                    handler: this.onReglerSelectionBL
                }
            ],
            bbar: {
                xtype: 'pagingtoolbar',
                pageSize: 10,
                store: store_etats,
                displayInfo: true,
                plugins: new Ext.ux.ProgressBarPager(), // same store GridPanel is using
                listeners: {
                    beforechange: function (page, currentPage) {
                        var myProxy = this.store.getProxy();
                        myProxy.params = {
                            datedebut: '',
                            datefin: '',
                            search_value: '',
                            lg_GROSSISTE_ID: ''
                        };
                        var lg_GROSSISTE_ID = "";
                        if (Ext.getCmp('lg_GROSSISTE_ID').getValue()) {
                            lg_GROSSISTE_ID = Ext.getCmp('lg_GROSSISTE_ID').getValue();
                        }
                        myProxy.setExtraParam('datedebut', Ext.getCmp('datedebut').getSubmitValue());
                        myProxy.setExtraParam('datefin', Ext.getCmp('datefin').getSubmitValue());
                        myProxy.setExtraParam('search_value', Ext.getCmp('rechecher').getValue());
                        myProxy.setExtraParam('lg_GROSSISTE_ID', lg_GROSSISTE_ID);
                    }

                }
            }
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


    },
    onbtnexport: function (grid, rowIndex) {

        Ext.MessageBox.confirm('Message',
                'Voulez-vous generer le fichier CSV pour les etiquettes de cette commande?',
                function (btn) {
                    if (btn === 'yes') {
                        var rec = grid.getStore().getAt(rowIndex);
                        window.location = '../DownloadFileServlet?lg_ORDER_ID=' + rec.get('lg_BON_LIVRAISON_ID') + '&str_TYPE_ACTION=ETATCONTROLE';
                    }
                });
    },
    onPdfDetailClick: function (grid, rowIndex) {

        var rec = grid.getStore().getAt(rowIndex);
        //var str_REF_LIVRAISON = Ext.getCmp('str_REF_LIVRAISON').getValue();
        var chaine = location.pathname;
        var reg = new RegExp("[/]+", "g");
        var tableau = chaine.split(reg);
        var sitename = tableau[1];
        var linkUrl = url_services_pdf_bonlivraison + '?lg_BON_LIVRAISON_ID=' + rec.get('str_BL_REF');
        // testextjs.app.getController('App').onLunchPrinter(linkUrl);
        //alert("Ok ca marche " + linkUrl);
        window.open(linkUrl);
        // Me.onbtncancel();
    },
    onPdfEtiquetteClick: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        new testextjs.view.stockmanagement.etiquette.action.add({
            odatasource: rec.get('lg_BON_LIVRAISON_ID'),
            parentview: this,
            mode: "printer",
            titre: "Edition d'etiquette des produits du bon de livraison [" + rec.get('str_BL_REF') + "]"
        });
        /*var rec = grid.getStore().getAt(rowIndex);
         var linkUrl = url_services_pdf_fiche_etiquette + '?lg_BON_LIVRAISON_ID=' + rec.get('lg_BON_LIVRAISON_ID');
         testextjs.app.getController('App').onLunchPrinterBis(linkUrl);*/
    },
    onPrintClick: function () {

        Ext.MessageBox.confirm('Message',
                'Imprimer l\'etat de controle des achats?',
                function (btn) {
                    if (btn === 'yes') {
                        Me.onPdfClick();
                        return;
                    }
                });



    },
    onRechClick: function () {
        var lg_GROSSISTE_ID = "";
        if (Ext.getCmp('lg_GROSSISTE_ID').getValue() != null) {
            lg_GROSSISTE_ID = Ext.getCmp('lg_GROSSISTE_ID').getValue();
        }

        if (new Date(Ext.getCmp('datedebut').getSubmitValue()) > new Date(Ext.getCmp('datefin').getSubmitValue())) {
            Ext.MessageBox.alert('Erreur au niveau date', 'La date de d&eacute;but doit &ecirc;tre inf&eacute;rieur &agrave; la date fin');
            return;
        }
        this.getStore().load({
            params: {
                search_value: Ext.getCmp('rechecher').getValue(),
                lg_GROSSISTE_ID: lg_GROSSISTE_ID,
                datedebut: Ext.getCmp('datedebut').getSubmitValue(),
                datefin: Ext.getCmp('datefin').getSubmitValue()
            }
        }, url_services_data_etats_list);
    },
    onPdfClick: function () {
        var valeur = Ext.getCmp('rechecher').getValue();
        var lg_GROSSISTE_ID = "";
        if (Ext.getCmp('lg_GROSSISTE_ID').getValue() != null) {
            lg_GROSSISTE_ID = Ext.getCmp('lg_GROSSISTE_ID').getValue();
        }

        var linkUrl = url_services_etatcontrole_pdf + "?lg_GROSSISTE_ID=" + lg_GROSSISTE_ID + "&search_value=" + valeur + "&datedebut=" + Ext.getCmp('datedebut').getSubmitValue() + "&datefin=" + Ext.getCmp('datefin').getSubmitValue();

        window.open(linkUrl);

    },
    onGestionQuinzaine: function () {
        var xtype = "quinzaineManager";
        testextjs.app.getController('App').onLoadNewComponent(xtype, "Gestion des quinzaines", "0");
    },
    onCheckChange: function (column, rowIndex, checked, eOpts) {
        var store = Ext.getCmp('gridID').getStore(), rec = store.getAt(rowIndex);
        if (checked) {
            selectedBL = rec;
            selectedBLs.push(rec);
        } else {
            selectedBL = {};
            if (selectedBLs.indexOf(rec) !== -1) {
                selectedBLs.splice(selectedBLs.indexOf(rec), 1);
            }
        }
        console.log(selectedBL);
        console.log(selectedBLs);
        lg_BON_LIVRAISON_ID = selectedBL.get('lg_BON_LIVRAISON_ID');
        int_MHT = selectedBL.get('int_MHT');

        rec.commit();
    },
    onReglerSelectionBL: function () {
        if (selectedBLs.length === 0) {
            Ext.MessageBox.show({
                title: 'Règlement des Bons de Livraison',
                width: 320,
                msg: 'Aucun bon de Livraison sélectionné.',
                buttons: Ext.MessageBox.OK,
                icon: Ext.MessageBox.ERROR,
                fn: function (btn) {
                    btn.up('window').close();
                }
            });

            return;
        } else {

            new testextjs.view.commandemanagement.etats.action.paybls({
                selectedBLs: selectedBLs,
                selectedBL: selectedBL,
                titre: 'Règlement de Bons de Livraison[ ' + selectedBL.get('lg_BON_LIVRAISON_ID') + ' : ' + selectedBL.get('int_MHT') + ' ]'
            });
        }

    },
    updateInfoBonlivraison: function (grid, rowIndex) {
        if (!btnUpdate) {
            return;
        }
        var rec = grid.getStore().getAt(rowIndex);
        new testextjs.view.commandemanagement.cmde_passees.action.edit({
            gridToLoad: 'etatscontrolemanagerID',
            odatasource: rec.data,
            parentview: this,
            mode: "update",
            titre: "Mise &agrave; jour des informations du Bon de livraison N&deg;" + rec.get('str_BL_REF')
        });
    }
});