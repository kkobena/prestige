/* global Ext */
var Me;
var url_services_data_ventedepot = "";
Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.sm_user.vente.VenteHistoriqueDepotManager', {
    extend: 'Ext.grid.Panel',
    xtype: 'ventehistoriquedepotmanager',
    id: 'ventehistoriquedepotmanagerID',
    frame: true,
    animCollapse: false,
    title: 'Statitisques des ventes d&eacute;p&ocirc;ts',
    plain: true,
    maximizable: true,
    closable: false,
    plugins: [{
            ptype: 'rowexpander',
            rowBodyTpl: new Ext.XTemplate(
                    '<p> {str_FAMILLE_ITEM}</p>',
                    {
                        formatChange: function(v) {
                            var color = v >= 0 ? 'green' : 'red';
                            return '<span style="color: ' + color + ';">' + Ext.util.Format.usMoney(v) + '</span>';
                        }
                    })
        }],
    initComponent: function() {
        url_services_data_ventedepot = '../webservices/sm_user/vente/ws_data_depot.jsp';

        Me = this;
        var itemsPerPage = 20;
        var store = new Ext.data.Store({
            model: 'testextjs.model.Preenregistrement',
            pageSize: itemsPerPage,
            autoLoad: true,
            proxy: {
                type: 'ajax',
                url: url_services_data_ventedepot,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                },
                timeout: 240000
            }
        });

        var store_type = new Ext.data.Store({
            fields: ['str_TYPE_TRANSACTION', 'str_desc'],
            data: [{str_TYPE_TRANSACTION: 'VNO', str_desc: 'DEPOT AGRE'}, {str_TYPE_TRANSACTION: 'VO', str_desc: 'DEPOT EXTENSION'}]
        });
        Ext.apply(this, {
            width: '98%',
            height: valheight,
            id: 'Grid_Prevente_ID',
            store: store,
            columns: [
                {
                    header: 'Reference',
                    dataIndex: 'str_REF',
                    flex: 0.7
                }, {
                    header: 'MONTANT',
                    dataIndex: 'int_PRICE_FORMAT',
                    flex: 1,
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
                    header: 'Client',
                    dataIndex: 'str_FIRST_LAST_NAME_CLIENT',
                    flex: 1.5,
                }, {
                    header: 'Type D&eacute;p&ocirc;t',
                    dataIndex: 'str_TYPE_VENTE',
                    flex: 1
                }, {
                    header: 'Vendeur',
                    dataIndex: 'lg_USER_VENDEUR_ID',
                    flex: 1
                }, {
                    header: 'Caissier',
                    dataIndex: 'lg_USER_CAISSIER_ID',
                    flex: 1
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
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    sortable: false,
                    menuDisabled: true,
                    items: [{
                            icon: 'resources/images/icons/fam/page_copy.png',
                            tooltip: 'Re-imprimer la facture',
                            scope: this,
                            handler: this.onPdfClickFacture,
                            getClass: function(value, metadata, record) {
                                if (record.get('b_IS_CANCEL') == "1") {
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
                        'change': function(me) {
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
                        'change': function(me) {
                            Ext.getCmp('dt_debut_journal').setMaxValue(me.getValue());   
                            if(Ext.getCmp('dt_debut_journal').getSubmitValue() != null && Ext.getCmp('dt_debut_journal').getSubmitValue() != null && Ext.getCmp('dt_debut_journal').getSubmitValue() != Ext.getCmp('dt_fin_journal').getSubmitValue()) {
                                /*Ext.getCmp('h_fin').reset();
                                Ext.getCmp('h_debut').reset();*/
                            }
                        }
                    }
                }, {
                    xtype: 'timefield',
                    fieldLabel: 'De',
                    name: 'h_debut',
                    id: 'h_debut',
                    emptyText: 'Heure debut(HH:mm)',
                    allowBlank: false,
                    flex: 1,
                    labelWidth: 50,
                    increment: 30,
                    format: 'H:i',
                    listeners: {
                        'change': function(me) {
                            if(Ext.getCmp('dt_fin_journal').getSubmitValue() == Ext.getCmp('dt_debut_journal').getSubmitValue()) {
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
                    listeners: {
                        'change': function(me) {
                            if(Ext.getCmp('dt_fin_journal').getSubmitValue() == Ext.getCmp('dt_debut_journal').getSubmitValue()) {
//                                Ext.getCmp('h_debut').setMaxValue(me.getValue());
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
                        select: function(cmp) {
                            Me.onRechClick();
                        }
                    }
                },
                {
                    xtype: 'textfield',
                    id: 'rechecher',
                    name: 'user',
                    emptyText: 'Recherche',
                    flex: 1,
                    listeners: {
                        'render': function(cmp) {
                            cmp.getEl().on('keypress', function(e) {
                                if (e.getKey() === e.ENTER) {
                                    Me.onRechClick();
                                }
                            });
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
                    tooltip: 'imprimer',
                    scope: this,
                    iconCls: 'printable',
                    hidden: true, //a retirer demain
                    handler: this.onPdfListVenteClick
                }],
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
                            beforechange: function(page, currentPage) {
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
        })

    },
    loadStore: function() {
        this.getStore().load({
            callback: this.onStoreLoad
        });
    },
    onStoreLoad: function() {
        if (Ext.getCmp('Grid_Prevente_ID').getStore().getCount() > 0) {
            var int_PRICE = 0;
            Ext.getCmp('Grid_Prevente_ID').getStore().each(function(rec) {
                int_PRICE = rec.get('dbl_AMOUNT');
            });
            Ext.getCmp('int_PRICE').setValue(Ext.util.Format.number(int_PRICE, '0,000.') + " CFA");
        }

    },
    onPdfClick: function(grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        linkUrl =  '../webservices/sm_user/detailsvente/ws_generate_pdf.jsp?lg_PREENREGISTREMENT_ID=' + rec.get('lg_PREENREGISTREMENT_ID');
        Me.lunchPrinter(linkUrl);

    },
    onPdfClickFacture: function(grid, rowIndex) {
        var rec = grid.getStore().getAt(rowIndex);
        linkUrl = '../webservices/sm_user/detailsvente/ws_generate_facture_pdf.jsp?lg_PREENREGISTREMENT_ID=' + rec.get('lg_PREENREGISTREMENT_ID');
        window.open(linkUrl);
    },
    onPdfListVenteClick: function() {

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
    lunchPrinter: function(url) {

        Ext.Ajax.request({
            url: url,
            timeout: 2400000,
            success: function(response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                if (object.success == "0") {
                    Ext.MessageBox.alert('Error Message', object.errors);
                    return;
                }

            },
            failure: function(response)
            {

                var object = Ext.JSON.decode(response.responseText, false);
                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);

            }
        });
    },
    onRechClick: function() {
        this.getStore().load({
            params: {
                dt_Date_Debut: Ext.getCmp('dt_debut_journal').getSubmitValue(),
                dt_Date_Fin: Ext.getCmp('dt_fin_journal').getSubmitValue(),
                search_value: Ext.getCmp('rechecher').getValue(),
                h_debut: Ext.getCmp('h_debut').getSubmitValue(),
                h_fin: Ext.getCmp('h_fin').getSubmitValue(),
                str_TYPE_VENTE: (Ext.getCmp('str_TYPE_TRANSACTION').getValue() != null ? Ext.getCmp('str_TYPE_TRANSACTION').getValue() : "")
            }
        }, url_services_data_ventedepot);
    }
})