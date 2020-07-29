/* global Ext */

var url_services_data_vente = '../webservices/sm_user/vente/ws_data.jsp';
var url_services_transaction_vente = '../webservices/sm_user/vente/ws_transaction.jsp?mode=';
var url_services_pdf_ticket;
var url_services_pdf_liste_vente = '../webservices/sm_user/vente/ws_generate_pdf.jsp';
var url_services_transaction_detailsvente_ventecloturee = '../webservices/sm_user/detailsvente/ws_transaction.jsp?mode=';

var Me;

Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.sm_user.vente.VenteManager', {
    extend: 'Ext.grid.Panel',
    xtype: 'ventemanager',
    id: 'ventemanagerID',
    frame: true,
//    collapsible: true,
    animCollapse: false,
    title: 'Liste des Ventes',
    plain: true,
    urlSuggession: '../webservices/sm_user/detailsvente/suggestion.jsp?mode=generate_suggestion',
    maximizable: true,
//    tools: [{type: "pin"}],
    closable: false,
//    iconCls: 'icon-grid',
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
    initComponent: function () {
        url_services_data_vente = '../webservices/sm_user/vente/ws_data.jsp';
        url_services_transaction_vente = '../webservices/sm_user/vente/ws_transaction.jsp?mode=';
        url_services_pdf_ticket = '../webservices/sm_user/detailsvente/ws_generate_pdf.jsp';


        Me = this;
        dt_Date_Debut = "";
        dt_Date_Fin = "";
        str_TYPE_VENTE = "";
        h_debut = "";
        h_fin = "";

        var linkUrl;
        var itemsPerPage = 20;
        var store = new Ext.data.Store({
            model: 'testextjs.model.Preenregistrement',
            pageSize: itemsPerPage,
            autoLoad: true,
            proxy: {
                type: 'ajax',
                url: '../webservices/sm_user/vente/datas.jsp',
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                },
                timeout: 240000
            }/*,
             sorters: [{
             property: 'str_REF',
             direction: 'DESC'
             }],
             sortRoot: 'str_REF',
             sortOnLoad: true,
             remoteSort: false*/

        });

        var store_type = new Ext.data.Store({
            fields: ['str_TYPE_TRANSACTION', 'str_desc'],
            data: [{str_TYPE_TRANSACTION: 'VNO', str_desc: 'VNO'}, {str_TYPE_TRANSACTION: 'VO', str_desc: 'VO'}]
        });
        Ext.apply(this, {
            width: '98%',
            height: valheight,
            id: 'Grid_Prevente_ID',
            //  plugins: [this.cellEditing],
            store: store,
            columns: [{
                    header: 'lg_PREENREGISTREMENT_ID',
                    dataIndex: 'lg_PREENREGISTREMENT_ID',
                    hidden: true,
                    flex: 1,
                    editor: {
                        allowBlank: false
                    }
                }, {
                    header: 'Reference',
                    dataIndex: 'str_REF',
                    flex: 1/*,
                     editor: {
                     allowBlank: false
                     }*/
                }, {
                    header: 'MONTANT',
                    dataIndex: 'int_PRICE_FORMAT',
                    flex: 1,
                    /* renderer: amountformat,*/
                    align: 'right'

                }, {
                    header: 'Date',
                    dataIndex: 'dt_CREATED',
                    flex: 0.6,
                    align: 'center'
                }, {
                    header: 'Heure',
                    dataIndex: 'str_hour',
                    flex: 0.6,
                    align: 'center'
                }, {
                    header: 'Type.vente',
                    dataIndex: 'str_TYPE_VENTE',
                    flex: 0.6,
                    align: 'center'
                }, {
                    header: 'Vendeur',
                    dataIndex: 'lg_USER_VENDEUR_ID',
                    flex: 1/*,
                     editor: {
                     allowBlank: false
                     }*/
                }, {
                    header: 'Caissier',
                    dataIndex: 'lg_USER_CAISSIER_ID',
                    flex: 1/*,
                     editor: {
                     allowBlank: false
                     }*/
                }, {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/printer.png',
                            tooltip: 'Re -imprimer le ticket',
                            scope: this,
                            handler: this.onPdfClick

                        }]
                }, {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/delete.gif',
                            tooltip: 'Annuler.Vente',
                            scope: this,
                            handler: this.onRemoveClick,
                            getClass: function (value, metadata, record) {
                                if (record.get('BTN_ANNULATION') === 'false') {
                                    return 'x-hide-display';
                                } else {
                                    if (record.get('bISCANCEL') == true) {
                                        return 'x-hide-display';
                                    } else {
                                        if (record.get('int_PRICE') <= 0) {
                                            return 'x-hide-display';
                                        } else {
                                            return 'x-display-hide';
                                        }
                                    }
                                }


                            }
                        }]
                }, {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/printer.png',
                            tooltip: 'Re-imprimer la facture',
                            scope: this,
                            handler: this.onPdfClickFacture,
                            getClass: function (value, metadata, record) {

                                if (record.get('bISCANCEL') == true) {
                                    return 'x-hide-display';
                                } else {
                                    if (record.get('int_PRICE') <= 0) {
                                        return 'x-hide-display';
                                    } else {
                                        return 'x-display-hide';
                                    }
                                }
                            }

                        }]
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/folder_go.png',
                            tooltip: 'Generer une suggestion pour la vente',
                            scope: this,
                            handler: this.onGenerateSuggestionClick,
                            getClass: function (value, metadata, record) {
                                if (record.get('int_PRICE') <= 0 || record.get('lg_EMPLACEMENT_ID') != "1") {
                                    return 'x-hide-display';
                                } else {
                                    if (record.get('bISCANCEL') == true) {
                                        return 'x-hide-display';
                                    } else {
                                        if (record.get('int_SENDTOSUGGESTION') == "1") {
                                            return 'x-hide-display';
                                        }
                                    }

                                    //  return 'x-display-hide';
                                }

                            }
                        }]
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/excel_csv.png',
                            tooltip: 'Exporter en csv les produits vendus',
                            scope: this,
                            handler: this.onbtnexportCsv,
                            getClass: function (value, metadata, record) {
                                // alert(record.get('lg_TYPE_VENTE_ID') + " " + record.get('str_statut'));
                                if (record.get('lg_TYPE_VENTE_ID') == "5") {  //read your condition from the record
                                    return 'x-display-hide'; //affiche l'icone
                                } else {
                                    return 'x-hide-display'; //cache l'icone
                                }
                            }
                        }
                    ]
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
                            Ext.getCmp('dt_debut_journal').setMaxValue(me.getValue());
                            if (Ext.getCmp('dt_debut_journal').getSubmitValue() != null && Ext.getCmp('dt_debut_journal').getSubmitValue() != null && Ext.getCmp('dt_debut_journal').getSubmitValue() != Ext.getCmp('dt_fin_journal').getSubmitValue()) {
                                /*Ext.getCmp('h_debut').setValue('00:00');
                                 Ext.getCmp('h_fin').setValue('23:30');
                                 Ext.getCmp('h_fin').reset();
                                 Ext.getCmp('h_debut').reset();*/

                            }
                        }
                    }
                }, {
                    xtype: 'timefield',
                    fieldLabel: 'De',
                    // margin: '0 7 0 0',
                    name: 'h_debut',
                    id: 'h_debut',
                    emptyText: 'Heure debut(HH:mm)',
                    allowBlank: false,
                    flex: 1,
                    labelWidth: 50,
                    increment: 30,
                    //maxValue: new Date(),
                    //submitFormat: 'Y-m-d',
                    format: 'H:i',
                    listeners: {
                        'change': function (me) {
                            if (Ext.getCmp('dt_fin_journal').getSubmitValue() == Ext.getCmp('dt_debut_journal').getSubmitValue()) {
                                //Ext.getCmp('h_fin').setMinValue(me.getValue());
                            }
                        }
                    }
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
                    format: 'H:i',
                    // margin: '0 7 0 0',
                    listeners: {
                        'change': function (me) {
                            if (Ext.getCmp('dt_fin_journal').getSubmitValue() == Ext.getCmp('dt_debut_journal').getSubmitValue()) {
                                //Ext.getCmp('h_debut').setMaxValue(me.getValue());
                            }
                        }
                    }
                }, '-', {
                    xtype: 'combobox',
                    name: 'str_TYPE_TRANSACTION',
                    margins: '0 0 0 10',
                    id: 'str_TYPE_TRANSACTION',
                    store: store_type,
                    valueField: 'str_TYPE_TRANSACTION',
                    displayField: 'str_desc',
                    typeAhead: true,
                    queryMode: 'remote',
                    flex: 1,
                    emptyText: 'Type de vente...',
                    listeners: {
                        select: function (cmp) {
                            Me.onRechClick();
                        }
                    }
                }, '-',
                {
                    xtype: 'textfield',
                    id: 'rechecher',
                    name: 'user',
                    emptyText: 'Recherche',
                    flex: 1,
                    listeners: {
                        'render': function (cmp) {
                            cmp.getEl().on('keypress', function (e) {
                                if (e.getKey() === e.ENTER) {
                                    Me.onRechClick();
                                }
                            });
                        }
                    }
                }, '-', {
                    text: 'rechercher',
                    tooltip: 'rechercher',
                    iconCls: 'searchicon',
                    scope: this,
                    handler: this.onRechClick
                }, '-', {
                    text: 'Imprimer',
                    tooltip: 'imprimer',
                    scope: this,
                    iconCls: 'printable',
                    hidden: true, //a retirer demain
                    handler: this.onPdfListVenteClick
                }
                

            ],
            bbar: {
                dock: 'bottom',
                items: [
                    {
                        xtype: 'pagingtoolbar',
                        displayInfo: true,
                        flex: 2,
                        pageSize: itemsPerPage,
                        store: store, // same store GridPanel is using
                        listeners: {
                            beforechange: function (page, currentPage) {
                                var myProxy = this.store.getProxy();
                                myProxy.params = {
                                    dt_Date_Debut: '',
                                    dt_Date_Fin: '',
                                    search_value: '',
                                    h_debut: '',
                                    h_fin: '',
                                    str_TYPE_VENTE: ''
                                };
                                var str_TYPE_TRANSACTION = "";
                                if (Ext.getCmp('str_TYPE_TRANSACTION').getValue()) {
                                    str_TYPE_TRANSACTION = Ext.getCmp('str_TYPE_TRANSACTION').getValue();
                                }
                                myProxy.setExtraParam('dt_Date_Debut', Ext.getCmp('dt_debut_journal').getSubmitValue());
                                myProxy.setExtraParam('dt_Date_Fin', Ext.getCmp('dt_fin_journal').getSubmitValue());
                                myProxy.setExtraParam('search_value', Ext.getCmp('rechecher').getValue());
                                myProxy.setExtraParam('str_TYPE_VENTE', str_TYPE_TRANSACTION);
                                myProxy.setExtraParam('h_debut', Ext.getCmp('h_debut').getSubmitValue());
                                myProxy.setExtraParam('h_fin', Ext.getCmp('h_fin').getSubmitValue());
                            }

                        }
                    },
                    {
                        xtype: 'tbseparator'
                    },
                    {
                        xtype: 'displayfield',
                        flex: 0.7,
                        fieldLabel: 'Montant Total::',
                        fieldWidth: 150,
                        name: 'int_PRICE',
                        hidden: true,
//                        renderer: amountformatbis,
                        id: 'int_PRICE',
                        fieldStyle: "color:blue;font-size:1.5em;font-weight:bold;",
                        value: 0 + " CFA"


                    }
                ]
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
        if (Ext.getCmp('Grid_Prevente_ID').getStore().getCount() > 0) {
            var int_PRICE = 0;
            Ext.getCmp('Grid_Prevente_ID').getStore().each(function (rec) {
                int_PRICE = rec.get('dbl_AMOUNT');
            });
            Ext.getCmp('int_PRICE').setValue(Ext.util.Format.number(int_PRICE, '0,000.') + " CFA");
        }

    },
    onPdfClick: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        linkUrl = url_services_pdf_ticket + '?lg_PREENREGISTREMENT_ID=' + rec.get('lg_PREENREGISTREMENT_ID');
        Me.lunchPrinter(linkUrl);
//        window.open(linkUrl);

    },
    onPdfClickFacture: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);

        if (rec.get('str_FIRST_LAST_NAME_CLIENT') === " ") {
            var win = Ext.create("Ext.window.Window", {
                title: "Ajout le nom et p&eacute;nom",
                height: 230,
                width: 450,
                id: 'customerinfo',
                closable: true,
                modal: true,
                layout: {
                    type: 'fit'
                },
                items: [{
                        xtype: 'form',
                        id: 'customerinfoform',
                        bodyPadding: 5,
                        modelValidation: true,
                        layout: {
                            type: 'fit',
                            align: 'middle'
                        },
                        items: [
                            {
                                xtype: 'fieldset',
                                layout: 'anchor',
                                defaults: {
                                    anchor: '100%',
                                    xtype: 'textfield',
                                    msgTarget: 'side',
                                    labelAlign: 'right',
                                    labelWidth: 115
                                },
                                items: [
                                    {
                                        fieldLabel: 'Nom',
                                        emptyText: 'Nom',
                                        name: 'str_FIRST_NAME',
                                        padding: '20 0 0 0',
                                        allowBlank: false
                                    }, {
                                        fieldLabel: 'Pr&eacute;nom(s)',
                                        emptyText: 'Prenom',
                                        name: 'str_LAST_NAME',
                                        allowBlank: false,
                                        enableKeyEvents: true,
                                        listeners: {
                                            specialKey: function (field, e) {
                                                if (e.getKey() === e.ENTER) {
                                                    var form = Ext.getCmp('customerinfoform');
                                                    if (form && form.isValid()) {
                                                        form.submit({
                                                            url: '../webservices/sm_user/vente/ws_transaction.jsp?mode=updatecustomerinfos&lg_PREENREGISTREMENT_ID=' + rec.get('lg_PREENREGISTREMENT_ID'),
                                                            type: 'ajax',
                                                            success: function (form, action) {
                                                                var object = Ext.JSON.decode(action.response.responseText);

                                                                if (object.success === "1") {
                                                                    linkUrl = '../webservices/sm_user/detailsvente/ws_generate_facture_pdf.jsp?lg_PREENREGISTREMENT_ID=' + rec.get('lg_PREENREGISTREMENT_ID');
                                                                    window.open(linkUrl);
                                                                    var cmp = Ext.getCmp('customerinfo');
                                                                    cmp.close();
                                                                } else {
                                                                    Ext.Msg.alert('Error', "Echec de cr&eacute;ation de l'utilisateur");

                                                                }

                                                            },
                                                            failure: function (form, action) {

                                                            }
                                                        });
                                                    }
                                                }
                                            }

                                        }
                                    }, {
                                        fieldLabel: 'T&eacute;l&eacute;phone',
                                        emptyText: 'Telephone',
                                        padding: '20 0 0 0',
                                        name: 'str_ADRESSE',
                                        id: 'userphone',
                                        regex: /[0-9.]/,
                                        allowBlank: false,
                                        enableKeyEvents: true,
                                        listeners: {
                                            specialKey: function (field, e) {
                                                if (e.getKey() === e.ENTER) {
                                                    var form = Ext.getCmp('customerinfoform');
                                                    if (form && form.isValid()) {

                                                        if (this.getValue().length >= 8) {
                                                            form.submit({
                                                                url: '../webservices/sm_user/vente/ws_transaction.jsp?mode=updatecustomerinfos&lg_PREENREGISTREMENT_ID=' + rec.get('lg_PREENREGISTREMENT_ID'),
                                                                type: 'ajax',
                                                                success: function (form, action) {
                                                                    var object = Ext.JSON.decode(action.response.responseText);

                                                                    if (object.success === "1") {
                                                                        linkUrl = '../webservices/sm_user/detailsvente/ws_generate_facture_pdf.jsp?lg_PREENREGISTREMENT_ID=' + rec.get('lg_PREENREGISTREMENT_ID');
                                                                        window.open(linkUrl);
                                                                        var cmp = Ext.getCmp('customerinfo');
                                                                        cmp.close();
                                                                    } else {
                                                                        Ext.Msg.alert('Error', "Echec de cr&eacute;ation de l'utilisateur");

                                                                    }

                                                                },
                                                                failure: function (form, action) {

                                                                }
                                                            });
                                                        } else {
                                                            Ext.Msg.alert('Error', "Le num&eacute;ro n'est pas valide");
                                                        }
                                                    }
                                                }
                                            }

                                        }
                                    }

                                ]
                            }

                        ]
                    }]

                ,
                dockedItems: [
                    {
                        xtype: 'toolbar',
                        dock: 'bottom',
                        ui: 'footer',
                        layout: {
                            pack: 'end',
                            type: 'hbox'
                        },
                        items: [
                            {
                                xtype: 'button',
                                text: 'Enregistrer',
                                handler: function () {
                                    var form = Ext.getCmp('customerinfoform');
                                    if (form && form.isValid()) {
                                        if (Ext.getCmp('userphone').getValue().length >= 8) {
                                            form.submit({
                                                url: '../webservices/sm_user/vente/ws_transaction.jsp?mode=updatecustomerinfos&lg_PREENREGISTREMENT_ID=' + rec.get('lg_PREENREGISTREMENT_ID'),
                                                type: 'ajax',
                                                success: function (form, action) {
                                                    var object = Ext.JSON.decode(action.response.responseText);
                                                    if (object.success === "1") {

                                                        linkUrl = '../webservices/sm_user/detailsvente/ws_generate_facture_pdf.jsp?lg_PREENREGISTREMENT_ID=' + rec.get('lg_PREENREGISTREMENT_ID');
                                                        window.open(linkUrl);
                                                        var cmp = Ext.getCmp('customerinfo');
                                                        cmp.close();
                                                    } else {
                                                        Ext.Msg.alert('Error', "Echec de cr&eacute;ation de l'utilisateur");

                                                    }

                                                },
                                                failure: function (form, action) {

                                                }
                                            });
                                        } else {
                                            Ext.Msg.alert('Error', "Le num&eacute;ro n'est pas valide");
                                        }
                                    }
                                }
                            },
                            {
                                xtype: 'button',
                                text: 'Annuler',
                                listeners: {
                                    click: function () {
                                        var cmp = Ext.getCmp('customerinfo');
                                        cmp.close();

                                    }
                                }
                            }
                        ]
                    }
                ]

            });
            win.show();

        } else {
            linkUrl = '../webservices/sm_user/detailsvente/ws_generate_facture_pdf.jsp?lg_PREENREGISTREMENT_ID=' + rec.get('lg_PREENREGISTREMENT_ID');
            window.open(linkUrl);
        }
        //
        // 
        //   Me.lunchPrinter(linkUrl);
    },
    onPdfListVenteClick: function () {

        var search_value = Ext.getCmp('rechecher').getValue();


        if (new Date(dt_Date_Debut) > new Date(dt_Date_Fin)) {
            Ext.MessageBox.alert('Erreur au niveau date', 'La date de d&eacute;but doit &ecirc;tre inf&eacute;rieur &agrave; la date fin');
            return;
        }

        linkUrl = url_services_pdf_liste_vente + '?dt_Date_Debut=' + dt_Date_Debut + "&dt_Date_Fin=" + dt_Date_Fin + "&search_value=" + search_value + "&h_debut=" + h_debut + "&h_fin=" + h_fin + "&str_TYPE_VENTE=" + str_TYPE_VENTE + "&title=LISTE DES VENTES";
//        alert("linkUrl " + linkUrl);
        /*Me.lunchPrinter(linkUrl);*/
        var chaine = location.pathname;
        var reg = new RegExp("[/]+", "g");
        var tableau = chaine.split(reg);
        var sitename = tableau[1];


        window.open(linkUrl);

    },
    onManageDetailsClick: function (grid, rowIndex) {

        var rec = grid.getStore().getAt(rowIndex);
        var xtype = "doventemanager";
        var alias = 'widget.' + xtype;
        testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "Edition d'une preevente  Pour La Vente  Au Comptant", rec.get('str_REF'), rec.data);

    },
    onAddClick: function () {

        //  var rec = grid.getStore().getAt(rowIndex);
        var xtype = "doventemanager";
        var alias = 'widget.' + xtype;
        testextjs.app.getController('App').onLoadNewComponent(xtype, "Ajout De Produit(s)  Pour La Vente  Au Comptant", "0");


    },

    onRemoveClick: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);


        Ext.MessageBox.confirm('Message',
                'Voulez-Vous Annuler La Vente',
                function (btn) {
                    if (btn === 'yes') {
                        var rec = grid.getStore().getAt(rowIndex);
                        testextjs.app.getController('App').ShowWaitingProcess();
                        Ext.Ajax.request({
                            timeout: 240000,
                            method:'GET',
                            url:'../api/v1/vente/annulation/'+rec.get('lg_PREENREGISTREMENT_ID'),
//                            url: url_services_transaction_detailsvente_ventecloturee + 'annulervente',
                          /*  params: {
                                lg_PREENREGISTREMENT_ID: rec.get('lg_PREENREGISTREMENT_ID')
                            },*/
                            success: function (response)
                            {
                                testextjs.app.getController('App').StopWaitingProcess();
                                var object = Ext.JSON.decode(response.responseText, false);
                                
                                  if (object.success) {

                                    Ext.MessageBox.confirm('Message',
                                            'Confirmer l\'impression du ticket',
                                            function (btn) {
                                                if (btn === 'yes') {
                                                    var url_services_pdf_ticket = "";
                                                    url_services_pdf_ticket = '../webservices/sm_user/detailsvente/ws_generate_pdf.jsp';
                                                    var linkUrl = url_services_pdf_ticket + '?lg_PREENREGISTREMENT_ID=' + object.ref;
                                                    // alert(linkUrl);
                                                    Me.lunchPrinter(linkUrl);
                                                    //window.open(linkUrl);
                                                    var OGrid = Ext.getCmp('Grid_Prevente_ID');
                                                    OGrid.getStore().reload();
                                                    return;
                                                } else {
                                                    var OGrid = Ext.getCmp('Grid_Prevente_ID');
                                                    OGrid.getStore().reload();
                                                }
                                            });



                                } else {
                                    Ext.MessageBox.alert('Error Message', object.msg);
                                    return null;
                                }
                                
                                
                                

                              /*  if (object.errors_code === "1") {

                                    Ext.MessageBox.confirm('Message',
                                            'Confirmer l\'impression du ticket',
                                            function (btn) {
                                                if (btn === 'yes') {
                                                    var url_services_pdf_ticket = "";
                                                    url_services_pdf_ticket = '../webservices/sm_user/detailsvente/ws_generate_pdf.jsp';
                                                    var linkUrl = url_services_pdf_ticket + '?lg_PREENREGISTREMENT_ID=' + object.results[0].lg_PREENREGISTREMENT_ID;
                                                    // alert(linkUrl);
                                                    Me.lunchPrinter(linkUrl);
                                                    //window.open(linkUrl);
                                                    var OGrid = Ext.getCmp('Grid_Prevente_ID');
                                                    OGrid.getStore().reload();
                                                    return;
                                                } else {
                                                    var OGrid = Ext.getCmp('Grid_Prevente_ID');
                                                    OGrid.getStore().reload();
                                                }
                                            });



                                } else {
                                    Ext.MessageBox.alert('Error Message', object.errors);
                                    return null;
                                }*/
                            },
                            failure: function (response)
                            {
                                testextjs.app.getController('App').StopWaitingProcess();
//                                var object = Ext.JSON.decode(response.responseText, false);
                                //  alert(object);

//                                console.log("Bug " + response.responseText);
                                Ext.MessageBox.alert('Error Message',"L'opération a échoué");

                            }
                        });
                        return;
                    }
                });


    },
    lunchPrinter: function (url) {

        Ext.Ajax.request({
            url: url,
            timeout: 2400000,
            success: function (response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                if (object.success == "0") {
                    Ext.MessageBox.alert('Error Message', object.errors);
                    return;
                }

            },
            failure: function (response)
            {

                var object = Ext.JSON.decode(response.responseText, false);
                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);

            }
        });
    },
    onGenerateSuggestionClick: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        Ext.MessageBox.confirm('Message',
                'Voulez-vous envoyer la vente ' + rec.get('str_REF') + ' en suggestion',
                function (btn) {
                    if (btn === 'yes') {

                        Ext.Ajax.request({
                            timeout: 240000,
                            url: Me.urlSuggession,
                            params: {
                                lg_PREENREGISTREMENT_ID: rec.get('lg_PREENREGISTREMENT_ID')
                            },
                            success: function (response)
                            {
                                var object = Ext.JSON.decode(response.responseText, false);
                                // Ext.MessageBox.alert('Error Message', object.errors);
                                if (object.success == "0") {
                                    Ext.MessageBox.alert('Error Message', object.errors);
                                    return;
                                } else {
                                    Ext.MessageBox.alert('confirmation', object.errors);
                                    grid.getStore().reload();
                                }
                            },
                            failure: function (response)
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
    onEditClick: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        new testextjs.view.sm_user.preenregistrement.action.add({
            odatasource: rec.data,
            parentview: this,
            mode: "update",
            titre: "Modification Preenregistrement  [" + rec.get('str_REF') + "]"
        });



    },
    onRechClick: function () {
        this.getStore().load({
            params: {
                dt_Date_Debut: Ext.getCmp('dt_debut_journal').getSubmitValue(),
                dt_Date_Fin: Ext.getCmp('dt_fin_journal').getSubmitValue(),
                search_value: Ext.getCmp('rechecher').getValue(),
                h_debut: Ext.getCmp('h_debut').getSubmitValue(),
                h_fin: Ext.getCmp('h_fin').getSubmitValue(),
                str_TYPE_VENTE: (Ext.getCmp('str_TYPE_TRANSACTION').getValue() != null ? Ext.getCmp('str_TYPE_TRANSACTION').getValue() : "")
            }
        }, url_services_data_vente);
    },
    onbtnexportCsv: function (grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);

        var liste_param = "search_value:" + rec.get('lg_PREENREGISTREMENT_ID');
        var extension = "csv";
        window.location = '../MigrationServlet?table_name=TABLE_MISEAJOUR_STOCKDEPOT' + "&extension=" + extension + "&liste_param=" + liste_param;
    }

})