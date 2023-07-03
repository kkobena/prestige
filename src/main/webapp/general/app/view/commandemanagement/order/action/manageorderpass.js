/* global Ext, rec */


var url_services_data_passation = '../webservices/configmanagement/typepassation/ws_data.jsp';
var url_services_transaction_order = '../webservices/commandemanagement/order/ws_transaction.jsp?mode=';
var url_services_data_orderdet = '../webservices/commandemanagement/orderdetail/ws_data.jsp?lg_ORDER_ID=';
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
        var itemsPerPage = 20;
        var itemsPerPageGrid = 10;
        var storetypepassation = new Ext.data.Store({
            model: 'testextjs.model.TypePassation',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_passation,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                },
                timeout: 240000
            }

        });
        url_services_data_orderdet = '../webservices/commandemanagement/orderdetail/ws_data.jsp?lg_ORDER_ID=' + ref;

        store_details_order = new Ext.data.Store({
            model: 'testextjs.model.OrderDetail',
            pageSize: itemsPerPageGrid,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_orderdet,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                },
                timeout: 240000
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
                                    queryMode: 'remote',
                                    emptyText: 'Choisir le mode de passation...',
                                    listeners: {
                                        select: function (cmp) {

                                            var value = cmp.getValue();
                                            if (value === '01') {

                                                Ext.getCmp('orderPassInfos').show();
                                                //fielSetDetail
                                                Ext.getCmp('fielSetDetail').show();
                                                var OGrid = Ext.getCmp('gridpanelID2');
                                                OGrid.getStore().reload();
                                                Ext.getCmp('orderPassInfos').setTitle("PASSATION PAR TELEPHONE");
                                                Ext.getCmp('INFORMATION_ID').setValue("APPELEZ LE GROSSISTE ET PASSEZ VOTRE COMMANDE");
                                                Ext.getCmp('str_GROSSISTE_TELEPHONE').show();
                                                Ext.getCmp('str_GROSSISTE_MOBILE').show();
                                                // ENABLE
                                                Ext.getCmp('btnValiderId').enable();
                                                // HIDE
                                                Ext.getCmp('BTN_EXPORT').hide();
                                                Ext.getCmp('URL').hide();
                                            } else if (value === '02') {
                                                var OGrid = Ext.getCmp('gridpanelID2');
                                                OGrid.getStore().reload();
                                                Ext.getCmp('fielSetDetail').show();
                                                //var OGrid = Ext.getCmp('gridpanelID2');
                                                //alert('Pas encore implemente');
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
                                                var OGrid = Ext.getCmp('gridpanelID2');
                                                OGrid.getStore().reload();
                                                // BTN_EXPORT
                                                Ext.getCmp('BTN_EXPORT').show();
                                                Ext.getCmp('URL').show();
                                                Ext.getCmp('URL').setValue("<a href = " + "'" + Ext.getCmp('str_GROSSISTE_URLEXTRANET').getValue() + "'" + "target='google'>REDIRECTION VERS LE SITE DU GROSSISTE</a>");
                                                // ENABLE
                                                Ext.getCmp('btnValiderId').enable();
                                                //HIDE
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
                    hidden: true,
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            columnWidth: 0.65,
                            xtype: 'gridpanel',
                            id: 'gridpanelID2',
                            store: store_details_order,
                            height: 300,
                            columns: [{
                                    text: 'Details Suggestion Id',
                                    flex: 1,
                                    sortable: true,
                                    hidden: true,
                                    dataIndex: 'lg_ORDERDETAIL_ID',
                                    id: 'lg_ORDERDETAIL_ID'
                                }, {
                                    text: 'Famille',
                                    flex: 1,
                                    hidden: true,
                                    dataIndex: 'lg_FAMILLE_ID'
                                },
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
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'lg_FAMILLE_QTE_STOCK'
                                },
                                {
                                    header: 'Q.CDE',
                                    dataIndex: 'int_NUMBER',
                                    flex: 1
                                },
                                {
                                    header: 'Q.A LIVRER',
                                    dataIndex: 'int_QTE_REP_GROSSISTE',
                                    flex: 1
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
                                plugins: new Ext.ux.ProgressBarPager()
                            },
                            listeners: {
                                scope: this,
                                selectionchange: this.onSelectionChange
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

        oGridParent = Ext.getCmp('OderGrid');
        if (Omode === "passed") {

            ref = this.getOdatasource().lg_ORDER_ID;
            Ext.getCmp('lg_GROSSISTE_ID_1').setValue(this.getOdatasource().str_GROSSISTE_LIBELLE);
            Ext.getCmp('str_REF_ORDER').setValue(this.getOdatasource().str_REF_ORDER);
            //str_GROSSISTE_MOBILE str_GROSSISTE_TELEPHONE
            Ext.getCmp('str_GROSSISTE_MOBILE').setValue(this.getOdatasource().str_GROSSISTE_MOBILE);
            Ext.getCmp('str_GROSSISTE_TELEPHONE').setValue(this.getOdatasource().str_GROSSISTE_TELEPHONE);
            // str_GROSSISTE_URLEXTRANET
            Ext.getCmp('str_GROSSISTE_URLEXTRANET').setValue(this.getOdatasource().str_GROSSISTE_URLEXTRANET);
        }



        var win = new Ext.window.Window({
            autoShow: true,
            title: this.getTitre(),
            width: 800,
            height: 600,
            minWidth: 300,
            minHeight: 200,
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
    },
    onRuptureClick: function (grid, rowIndex) {
        Ext.MessageBox.confirm('Message',
                'Mettre en rupture?',
                function (btn) {
                    if (btn === 'yes') {
                        let rec = grid.getStore().getAt(rowIndex);
                        Ext.Ajax.request({
                            url: url_services_transaction_order + 'rupture',
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

        let commandetype = "";
        if (Ext.getCmp('lg_TYPE_PASSATION_ID').getValue() != null) {
            commandetype = Ext.getCmp('lg_TYPE_PASSATION_ID').getValue();
        }
        Ext.MessageBox.confirm('Message',
                'Confirme la passation',
                function (btn) {

                    if (btn === 'yes') {
                        testextjs.app.getController('App').ShowWaitingProcess();

                        Ext.Ajax.request({
                            url: url_services_transaction_order + 'passeorder',
                            params: {
                                lg_ORDER_ID: me.getOdatasource().lg_ORDER_ID,
                                str_STATUT: me.getOdatasource().lg_ORDER_IDstr_STATUT/*,
                                 lg_TYPE_PASSATION_ID: Ext.getCmp('lg_TYPE_PASSATION_ID').getValue()*/
                            },
                            timeout: 2400000,
                            success: function (response)
                            {
                                testextjs.app.getController('App').StopWaitingProcess();
                                let object = Ext.JSON.decode(response.responseText, false);
                                if (object.success === 0) {
                                    Ext.MessageBox.alert('Error Message', object.errors);
                                    return;
                                }

                                oGridParent.getStore().reload();

                                Ext.MessageBox.confirm('Message',
                                        'Imprimer le bon de commande?',
                                        function (btn) {
                                            if (btn === 'yes') {
                                                Me.onPdfClick(rec.get('lg_ORDER_ID'));
                                              
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
        this.getStore().load({
            callback: this.onStoreLoad
        });
    },
    onStoreLoad: function () {


    },
    oncancel: function () {
        const xtype = "i_order_manager";
        testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "", "", "");
    },
    onPdfClick: function (lg_ORDER_ID) {
        const linkUrl = '../webservices/commandemanagement/order/ws_generate_pdf.jsp?lg_ORDER_ID=' + lg_ORDER_ID;
        window.open(linkUrl);
    }
});