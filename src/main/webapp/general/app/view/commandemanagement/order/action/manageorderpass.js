/* global Ext, rec */



var oGridParen;
var Oview;
var Omode;
var Me;
var ref;
Ext.define('testextjs.view.commandemanagement.order.action.manageorderpass', {
    extend: 'Ext.window.Window',
    xtype: 'manageorderpasse',
    id: 'manageorderpassID',
    requires: [
        'Ext.form.*',
        'Ext.window.Window',
        'testextjs.model.Grossiste',
        'testextjs.model.TypePassation',
        'testextjs.model.TypeReglement'
    ],
    config: {
        odatasource: '',
        parentview: '',
        mode: '',
        titre: ''
    },
    initComponent: function () {

        Oview = this.getParentview();
        Omode = this.getMode();
        ref = this.getOdatasource().lg_ORDER_ID;
        Me = this;

        const itemsPerPageGrid = 10;
        const storetypepassation = new Ext.data.Store({
            model: 'testextjs.model.TypePassation',
            pageSize: itemsPerPageGrid,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../webservices/configmanagement/typepassation/ws_data.jsp',
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                },
                timeout: 240000
            }

        });


        const   store_details_order = new Ext.data.Store({
            model: 'testextjs.model.OrderDetail',
            pageSize: itemsPerPageGrid,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../api/v1/commande/commande-en-cours-items',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                },
                timeout: 180000
            }

        });
        var lg_GROSSISTE_ID_1 = new Ext.form.field.Display({
            xtype: 'displayfield',
            fieldLabel: 'GROSSISTE ::',
            labelWidth: 110,
            name: 'lg_GROSSISTE_ID_1',
            id: 'lg_GROSSISTE_ID_1',
            fieldStyle: "color:blue;",
            margin: '0 30 0 0',
            value: "0"
        });
        var str_REF_ORDER = new Ext.form.field.Display({
            xtype: 'displayfield',
            fieldLabel: 'Ref.COMMANDE ::',
            labelWidth: 130,
            name: 'str_REF_ORDER',
            id: 'str_REF_ORDER',
            fieldStyle: "color:blue;",
            margin: '0 30 0 0',
            value: "0"
        });
        var INFORMATION = new Ext.form.field.Display({
            xtype: 'displayfield',
            fieldLabel: 'INFORMATION ::',
            labelWidth: 130,
            name: 'INFORMATION_ID',
            id: 'INFORMATION_ID',
            fieldStyle: "color:blue;",
            margin: '0 30 0 0'
        });
        var URL = new Ext.form.field.Display({
            xtype: 'displayfield',
            fieldLabel: '',
            labelWidth: 130,
            name: 'URL',
            id: 'URL',
            fieldStyle: "color:blue;",
            margin: '0 30 0 0',
            hidden: true
        });
        var str_GROSSISTE_TELEPHONE = new Ext.form.field.Display({
            xtype: 'displayfield',
            fieldLabel: 'TELEPHONE ::',
            labelWidth: 130,
            name: 'str_GROSSISTE_TELEPHONE',
            id: 'str_GROSSISTE_TELEPHONE',
            fieldStyle: "color:blue;",
            margin: '0 30 0 0',
            value: "0"
        });
        var str_GROSSISTE_MOBILE = new Ext.form.field.Display({
            xtype: 'displayfield',
            fieldLabel: 'MOBILE ::',
            labelWidth: 130,
            name: 'str_GROSSISTE_MOBILE',
            id: 'str_GROSSISTE_MOBILE',
            fieldStyle: "color:blue;",
            margin: '0 30 0 0'
        });
        var form = new Ext.form.Panel({
            bodyPadding: 10,
            fieldDefaults: {
                labelAlign: 'right',
                labelWidth: 160,
                msgTarget: 'side'
            },
            items: [
                // INFOS COMMANDE

                {
                    xtype: 'fieldset',
                    title: 'Information de la commande',
                    defaultType: 'textfield',
                    id: 'orderPassInfosID',
                    collapsible: true,
                    layout: 'anchor',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            xtype: 'container',
                            layout: 'hbox',
                            defaultType: 'textfield',
                            margin: '0 0 5 0',
                            items: [
                                {
                                    allowBlank: false,
                                    fieldLabel: 'Order ID',
                                    name: 'lg_ORDER_ID_i',
                                    id: 'lg_ORDER_ID_i',
                                    hidden: true
                                },
                                lg_GROSSISTE_ID_1, str_REF_ORDER
                            ]
                        },
                        {
                            xtype: 'container',
                            layout: 'hbox',
                            defaultType: 'textfield',
                            margin: '0 0 5 0',
                            items: [
                                {
                                    xtype: 'combobox',
                                    fieldLabel: 'Mode de passation',
                                    allowBlank: false,
                                    name: 'lg_TYPE_PASSATION_ID',
                                    margin: '0 15 0 0',
                                    id: 'lg_TYPE_PASSATION_ID',
                                    store: storetypepassation,
                                    valueField: 'lg_TYPE_PASSATION_ID',
                                    displayField: 'str_LIBELLE',
                                    typeAhead: true,
                                    hidden: true,
                                    queryMode: 'remote',
                                    emptyText: 'Choisir le mode de passation...',
                                    listeners: {
                                        select: function (cmp) {
                                            const OGrid = Ext.getCmp('gridpanelID2');
                                            let  value = cmp.getValue();
                                            if (value === '01') {

                                                Ext.getCmp('orderPassInfos').show();

                                                Ext.getCmp('fielSetDetail').show();

                                                OGrid.getStore().reload();
                                                Ext.getCmp('orderPassInfos').setTitle("PASSATION PAR TELEPHONE");
                                                Ext.getCmp('INFORMATION_ID').setValue("APPELEZ LE GROSSISTE ET PASSEZ VOTRE COMMANDE");
                                                Ext.getCmp('str_GROSSISTE_TELEPHONE').show();
                                                Ext.getCmp('str_GROSSISTE_MOBILE').show();

                                                Ext.getCmp('btnValiderId').enable();

                                                Ext.getCmp('BTN_EXPORT').hide();
                                                Ext.getCmp('URL').hide();
                                            } else if (value === '02') {

                                                OGrid.getStore().reload();
                                                Ext.getCmp('fielSetDetail').show();

                                                Ext.getCmp('orderPassInfos').hide();
                                                Ext.getCmp('orderPassInfos').setTitle("PASSATION PAR PHARMA-ML");
                                                Ext.getCmp('INFORMATION_ID').setValue("APPUYEZ SUR LE BOUTON PASSER");
                                                // Disable
                                                Ext.getCmp('btnValiderId').enable();
                                                //HIDE
                                                Ext.getCmp('str_GROSSISTE_TELEPHONE').hide();
                                                Ext.getCmp('str_GROSSISTE_MOBILE').hide();
                                            } else if (value === '03') {

                                                Ext.getCmp('orderPassInfos').show();
                                                Ext.getCmp('orderPassInfos').setTitle("PASSATION PAR EXTRANET");
                                                Ext.getCmp('INFORMATION_ID').setValue("GENEREZ LE CSV ET CLIQUEZ SUR LE LIENS CI-DESSOUS");
                                                Ext.getCmp('fielSetDetail').show();

                                                OGrid.getStore().reload();
                                                // BTN_EXPORT
                                                Ext.getCmp('BTN_EXPORT').show();
                                                Ext.getCmp('URL').show();
                                                Ext.getCmp('URL').setValue("<a href = " + "'" + Ext.getCmp('str_GROSSISTE_URLEXTRANET').getValue() + "'" + "target='google'>REDIRECTION VERS LE SITE DU GROSSISTE</a>");
                                           
                                                Ext.getCmp('btnValiderId').enable();
                                           
                                                Ext.getCmp('str_GROSSISTE_TELEPHONE').hide();
                                                Ext.getCmp('str_GROSSISTE_MOBILE').hide();
                                            }

                                        }
                                    }
                                }
                            ]
                        }
                    ]
                },
                // RECAP DETAIL COMMANDE

                {
                    xtype: 'fieldset',
                    id: 'fielSetDetail',
                    title: 'Detail(s) Commandes',
                    collapsible: true,
                    defaultType: 'textfield',
                    layout: 'anchor',

                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {

                            xtype: 'gridpanel',
                            id: 'gridpanelID2',
                            store: store_details_order,
                            layout: 'fit',
                            columns: [
                                {
                                    xtype: 'rownumberer',
                                    text: 'LG',
                                    width: 45
                                },
                                {
                                    text: 'CIP',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'lg_FAMILLE_CIP'
                                },
                                {
                                    text: 'DESIGNATION',
                                    flex: 2,
                                    sortable: true,
                                    dataIndex: 'lg_FAMILLE_NAME'
                                },
                                {
                                    text: 'QTE.STOCK',
                                    flex: 0.5,
                                    sortable: true,
                                    align: 'right',
                                    dataIndex: 'lg_FAMILLE_QTE_STOCK'
                                },
                                {
                                    header: 'Q.CDE',
                                    dataIndex: 'int_NUMBER',
                                    align: 'right',
                                    flex: 0.5
                                },
                                {
                                    header: 'Q.A LIVRER',
                                    dataIndex: 'int_QTE_REP_GROSSISTE',
                                    align: 'right',
                                    flex: 0.5
                                },
                                {
                                    xtype: 'actioncolumn',
                                    width: 30,
                                    sortable: false,
                                    menuDisabled: true,
                                    hidden: true,
                                    items: [{
                                            icon: 'resources/images/icons/fam/passed.png',
                                            tooltip: 'Rupture',
                                            scope: this,
                                            handler: this.onRuptureClick
                                        }]
                                }
                            ],
                            bbar: {
                                xtype: 'pagingtoolbar',
                                pageSize: itemsPerPageGrid,
                                store: store_details_order,
                                displayInfo: true,
                                plugins: new Ext.ux.ProgressBarPager(),
                                listeners: {
                                    beforechange: function (page, currentPage) {
                                        const myProxy = this.store.getProxy();
                                        myProxy.params = {
                                            query: null,
                                            filtre: 'ALL',
                                            orderId: ref

                                        };
                                        myProxy.setExtraParam('query', null);
                                        myProxy.setExtraParam('filtre', 'ALL');
                                        myProxy.setExtraParam('orderId', ref);
                                    }

                                }
                            }
                        }
                    ]

                },
                // PASSATION COMMANDE

                {
                    xtype: 'fieldset',
                    title: 'Passation par ',
                    id: 'orderPassInfos',
                    hidden: true,
                    defaultType: 'textfield',
                    collapsible: true,
                    layout: 'anchor',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            xtype: 'container',
                            defaultType: 'textfield',
                            margin: '0 0 5 0',
                            items: [
                                INFORMATION,
                                str_GROSSISTE_TELEPHONE,
                                str_GROSSISTE_MOBILE,

                                {
                                    xtype: 'container',
                                    layout: 'hbox',

                                    margin: '0 0 5 0',
                                    items: [
                                        {
                                            text: 'Exporter',
                                            id: 'BTN_EXPORT',
                                            margins: '0 30 0 0',
                                            xtype: 'button',
                                            handler: this.onbtnexport,
                                            hidden: true
                                        }

                                    ]
                                },
                                URL,
                                {
                                    fieldLabel: 'URL EXTRANET',
                                    name: 'str_GROSSISTE_URLEXTRANET',
                                    id: 'str_GROSSISTE_URLEXTRANET',
                                    hidden: true,
                                    fieldStyle: "color:blue;"
                                }

                            ]
                        }
                    ]
                }


            ]
        });
        //Initialisation des valeur


        if (Omode === "passed") {
            ref = this.getOdatasource().lg_ORDER_ID;
            Ext.getCmp('lg_GROSSISTE_ID_1').setValue(this.getOdatasource().str_GROSSISTE_LIBELLE);
            Ext.getCmp('str_REF_ORDER').setValue(this.getOdatasource().str_REF_ORDER);
            Ext.getCmp('str_GROSSISTE_MOBILE').setValue(this.getOdatasource().str_GROSSISTE_MOBILE);
            Ext.getCmp('str_GROSSISTE_TELEPHONE').setValue(this.getOdatasource().str_GROSSISTE_TELEPHONE);
            Ext.getCmp('str_GROSSISTE_URLEXTRANET').setValue(this.getOdatasource().str_GROSSISTE_URLEXTRANET);
        }



        const win = new Ext.window.Window({
            autoShow: true,
            title: this.getTitre(),
            width: '80%',
            height: 600,
            layout: 'fit',
            plain: true,
            items: form,
            buttons: [{
                    text: 'Valider',
                    id: 'btnValiderId',
                    disabled: true,
                    handler: this.onbtnsave
                }, {
                    text: 'Annuler',
                    handler: function () {
                        win.close();
                    }
                }]
        });
        this.callParent();
        this.loadStore();
    },
    onRuptureClick: function (grid, rowIndex) {
        Ext.MessageBox.confirm('Message',
                'Mettre en rupture?',
                function (btn) {
                    if (btn === 'yes') {
                        let rec = grid.getStore().getAt(rowIndex);
                        Ext.Ajax.request({
                            url: '../webservices/commandemanagement/order/ws_transaction.jsp?mode=rupture',
                            params: {
                                lg_ORDERDETAIL_ID: rec.get('lg_ORDERDETAIL_ID')
                            },
                            success: function (response)
                            {
                                let object = Ext.JSON.decode(response.responseText, false);
                                if (object.success === 0) {
                                    Ext.MessageBox.alert('Error Message', object.errors);
                                    return;
                                }
                                grid.getStore().reload();
                            },
                            failure: function (response)
                            {
                                console.log("Bug " + response.responseText);
                                Ext.MessageBox.alert('Error Message', response.responseText);
                            }
                        });

                    }
                });
    },

    onbtnexport: function () {
        const me = Me;
        Ext.MessageBox.confirm('Message',
                'Voulez-vous generer le fichier CSV de la commande?',
                function (btn) {
                    if (btn === 'yes') {

                        let maref = me.getOdatasource().lg_ORDER_ID;
                        if (maref === null) {
                            maref = "";
                        } else {
                            maref = me.getOdatasource().lg_ORDER_ID;
                        }
                        window.location = '../api/v1/commande/export-csv?id=' + maref;

                    }
                });
    },
    onbtnsave: function () {
        const me = Me;
        Ext.MessageBox.confirm('Message',
                'Confirme la passation',
                function (btn) {
                    if (btn === 'yes') {
                        testextjs.app.getController('App').ShowWaitingProcess();
                        Ext.Ajax.request({
                            method: 'GET',
                            url: '../api/v1/commande/statut/' + me.getOdatasource().lg_ORDER_ID + '/passe',
                            timeout: 2400000,
                            success: function (response)
                            {
                                testextjs.app.getController('App').StopWaitingProcess();
                                let object = Ext.JSON.decode(response.responseText, false);
                                if (object.success === 0) {
                                    Ext.MessageBox.alert('Error Message', object.errors);
                                    return;
                                }
                                Ext.MessageBox.confirm('Message',
                                        'Imprimer le bon de commande?',
                                        function (btn) {
                                            if (btn === 'yes') {
                                                Me.onPdfClick( );
                                            }
                                        });
                            },

                            failure: function (response)
                            {
                                testextjs.app.getController('App').StopWaitingProcess();

                                console.log("Bug " + response.responseText);
                                Ext.MessageBox.alert('Error Message', response.responseText);
                            }
                        });

                    }
                });
        this.up('window').close();
    },
    onSelectionChange: function (model, records) {
        const rec = records[0];
        if (rec) {
            this.getForm().loadRecord(rec);
        }
    },
    loadStore: function () {
        const me = this;
        Ext.getCmp('gridpanelID2').getStore().load({
            params: {
                query: null,
                filtre: 'ALL',
                orderId: me.getOdatasource().lg_ORDER_ID
            }
        });
    },

    onPdfClick: function () {
        const me = this;
        const linkUrl = '../EditionCommandeServlet?orderId=' + me.getOdatasource().lg_ORDER_ID + '&refCommande=' + me.getOdatasource().str_REF_ORDER;
        window.open(linkUrl);
    }
});