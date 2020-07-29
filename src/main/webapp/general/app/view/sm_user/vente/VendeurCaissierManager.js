var url_services_data_utilisateur = '../webservices/sm_user/utilisateur/ws_data.jsp';
var url_services_data_vendeurcaisse = '../webservices/sm_user/vente/ws_data_user_sell.jsp';
var Me;

Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.sm_user.vente.VendeurCaissierManager', {
    extend: 'Ext.grid.Panel',
    xtype: 'vendeurcaissiermanager',
    id: 'vendeurcaissiermanagerID',
    frame: true,
    animCollapse: false,
    title: 'Liste des Ventes',
    plain: true,
    maximizable: true,
//    tools: [{type: "pin"}],
    closable: false,
//    iconCls: 'icon-grid',
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


        Me = this;

        var itemsPerPage = 20;
        var store = new Ext.data.Store({
            model: 'testextjs.model.Preenregistrement',
            pageSize: itemsPerPage,
            autoLoad: true,
            proxy: {
                type: 'ajax',
                url: url_services_data_vendeurcaisse,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                },
                timeout: 240000
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

        var store_type = new Ext.data.Store({
            fields: ['str_TYPE_TRANSACTION', 'str_desc'],
            data: [{str_TYPE_TRANSACTION: 'VNO', str_desc: 'VNO'}, {str_TYPE_TRANSACTION: 'VO', str_desc: 'VO'}]
        });

        var store_vendeur_caissier = new Ext.data.Store({
            fields: ['str_TYPE', 'str_desc'],
            data: [{str_TYPE: 'VENDEUR', str_desc: 'Vendeur(se)'}, {str_TYPE: 'CAISSIER', str_desc: 'Caissier(e)'},
                {str_TYPE: 'VENDEUR_CAISSIER', str_desc: 'Vendeur(se) ou Caissier(e)'}, {str_TYPE: 'ALL', str_desc: 'TOUS'}]
        });
        Ext.apply(this, {
            width: '98%',
            height: valheight,
            id: 'Grid_Prevente_ID',
            //  plugins: [this.cellEditing],
            store: store,
            columns: [{
                    header: 'Reference',
                    dataIndex: 'str_REF',
                    flex: 1
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
                    header: 'Type.vente',
                    dataIndex: 'str_TYPE_VENTE',
                    flex: 0.6,
                    align: 'center'
                }, {
                    header: 'Vendeur',
                    dataIndex: 'lg_USER_VENDEUR_ID',
                    flex: 1
                }, {
                    header: 'Caissier',
                    dataIndex: 'lg_USER_CAISSIER_ID',
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
                    name: 'dt_fin',
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
                    hidden: true,
                    format: 'H:i',
                    listeners: {
                        'change': function(me) {
                            Ext.getCmp('h_fin').setMinValue(me.getValue());
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
                    hidden: true,
                    listeners: {
                        'change': function(me) {
                            Ext.getCmp('h_debut').setMaxValue(me.getValue());
                        }
                    }
                }, '-', {
                    xtype: 'combobox',
                    name: 'str_TYPE',
                    margins: '0 0 0 10',
                    id: 'str_TYPE',
                    store: store_vendeur_caissier,
                    valueField: 'str_TYPE',
                    displayField: 'str_desc',
                    typeAhead: true,
                    queryMode: 'remote',
                    flex: 1,
                    emptyText: 'Filtre Vendeur(se)/ Caissier(s)...',
                    listeners: {
                        select: function(cmp) {
                            Me.onRechClick();
                        }
                    }
                }, '-', {
                    xtype: 'combobox',
                    fieldLabel: 'Utilisateur',
                    name: 'lg_USER_ID',
                    id: 'lg_USER_ID',
                    store: storeUser,
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
                }],
            bbar: {
                xtype: 'pagingtoolbar',
                store: store, // same store GridPanel is using
                dock: 'bottom',
                displayInfo: true,
                listeners: {
                    beforechange: function(page, currentPage) {
                        var myProxy = this.store.getProxy();
                        myProxy.params = {
                            dt_Date_Debut: '',
                            dt_Date_Fin: '',
                            search_value: '',
                            str_TYPE_VENTE: '',
                            str_TYPE: '',
                            lg_USER_SEARCH_ID: ''
                        };
                        var str_TYPE_TRANSACTION = "", lg_USER_ID = "", str_TYPE = "";
                        if (Ext.getCmp('str_TYPE_TRANSACTION').getValue() != null) {
                            str_TYPE_TRANSACTION = Ext.getCmp('str_TYPE_TRANSACTION').getValue();
                        }
                        if (Ext.getCmp('lg_USER_ID').getValue() != null) {
                            lg_USER_ID = Ext.getCmp('lg_USER_ID').getValue();
                        }
                        if (Ext.getCmp('str_TYPE').getValue() != null) {
                            str_TYPE = Ext.getCmp('str_TYPE').getValue();
                        }
                        myProxy.setExtraParam('dt_Date_Debut', Ext.getCmp('dt_Date_Debut').getSubmitValue());
                        myProxy.setExtraParam('dt_Date_Fin', Ext.getCmp('dt_Date_Fin').getSubmitValue());
                        myProxy.setExtraParam('search_value', Ext.getCmp('rechecher').getValue());
                        myProxy.setExtraParam('str_TYPE_VENTE', str_TYPE_TRANSACTION);
                        myProxy.setExtraParam('str_TYPE', str_TYPE);
                        myProxy.setExtraParam('lg_USER_SEARCH_ID', lg_USER_ID);
                    }

                }
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
  },
    onRechClick: function() {

        var str_TYPE_TRANSACTION = "", str_TYPE = "", lg_USER_ID = "";
        if (Ext.getCmp('str_TYPE_TRANSACTION').getValue() != null) {
            str_TYPE_TRANSACTION = Ext.getCmp('str_TYPE_TRANSACTION').getValue();
        }
        if (Ext.getCmp('str_TYPE').getValue() != null) {
            str_TYPE = Ext.getCmp('str_TYPE').getValue();
        }
        if (Ext.getCmp('lg_USER_ID').getValue() != null) {
            lg_USER_ID = Ext.getCmp('lg_USER_ID').getValue();
        }
        if (new Date(Ext.getCmp('dt_Date_Debut').getSubmitValue()) > new Date(Ext.getCmp('dt_Date_Fin').getSubmitValue())) {
            Ext.MessageBox.alert('Erreur au niveau date', 'La date de d&eacute;but doit &ecirc;tre inf&eacute;rieur &agrave; la date fin');
            return;
        }
        var val = Ext.getCmp('rechecher');

        this.getStore().load({
            params: {
                dt_Date_Debut: Ext.getCmp('dt_Date_Debut').getSubmitValue(),
                dt_Date_Fin: Ext.getCmp('dt_Date_Fin').getSubmitValue(),
                search_value: val.getValue(),
                str_TYPE: str_TYPE,
                lg_USER_SEARCH_ID: lg_USER_ID,
                str_TYPE_VENTE: str_TYPE_TRANSACTION
            }
        }, url_services_data_vendeurcaisse);
    }

})