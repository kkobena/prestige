var url_services_data_famille_select = '../webservices/sm_user/famille/ws_data.jsp';
var url_services_transaction_famille_select = '../webservices/sm_user/preenregistrement/ws_transaction.jsp';
var Me;
var OgridpanelID;
var int_AMOUNT_RESUME;
var int_PRICE_RESUME;
var btn_select_prod;
var str_NAME_RESUME;
var Omode;
Ext.define('testextjs.view.sm_user.selectproduct.SelectproductManager', {
    extend: 'Ext.form.Panel',
    requires: [
        'Ext.grid.*',
        'Ext.form.*',
        'Ext.layout.container.Column',
        'testextjs.model.Famille'
    ],
    xtype: 'selectproductmanager',
    frame: true,
    config: {
        odatasource: '',
        odatatasourceparent: '',
        myods: '',
        parentview: '',
        mode: '',
        titre: '',
        nameintern: ''
    },
    title: 'Choix Produit',
    bodyPadding: 5,
    layout: 'column',
    initComponent: function() {

        int_AMOUNT_RESUME = "";
        int_PRICE_RESUME = "";
        str_NAME_RESUME = "";
        btn_select_prod;
        var int_product_price;
        var int_qte;
        url_services_data_famille_select = '../webservices/sm_user/famille/ws_data.jsp';
        //  var Prevente_id = this.getOdatasource().str_REF;
        //alert("Prevente_id  "+Prevente_id);

        // this.setTitle("Choix de produit(s) Prevente N ::  "+Prevente_id);
        Me = this;
        str_NAME_RESUME = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    //allowBlank: false,
                    fieldLabel: 'Nom',
                    name: 'str_NAME_RESUME',
                    id: str_NAME_RESUME,
                    emptyText: 'str_NAME_RESUME'
                });
        int_PRICE_RESUME = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    //allowBlank: false,
                    fieldLabel: 'Prix Unitaire',
                    name: 'int_PRICE_RESUME',
                    id: 'int_PRICE_RESUME',
                    emptyText: 'int_PRICE_RESUME'
                });
        int_AMOUNT_RESUME = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    //allowBlank: false,
                    fieldLabel: 'Total',
                    name: 'int_AMOUNT_RESUME',
                    id: 'int_AMOUNT_RESUME',
                    emptyText: 'int_AMOUNT_RESUME'
                });
        btn_select_prod = new Ext.Button(
                {
                    text: 'Valider',
                    buttonAlign: 'right',
                    id: 'btn_select_prod',
                    //renderTo: Ext.getBody(),
                    handler: this.onEndChooseClick/*,
                     hidden: true*/
                });
        var itemsPerPage = 20;
        var store = new Ext.data.Store({
            model: 'testextjs.model.Famille',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_famille_select,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            },
            autoLoad: true

        });
        Ext.apply(this, {
            width: 950,
            fieldDefaults: {
                labelAlign: 'left',
                labelWidth: 90,
                anchor: '100%',
                msgTarget: 'side'
            },
            items: [{
                    columnWidth: 0.65,
                    xtype: 'gridpanel',
                    id: 'gridpanelID',
                    store: store,
                    height: 400,
                    columns: [{
                            text: 'lg_FAMILLE_ID',
                            flex: 1,
                            sortable: true,
                            hidden: true,
                            dataIndex: 'lg_FAMILLE_ID'
                        }, {
                            header: 'Groupe Famille',
                            dataIndex: 'lg_GROUPE_FAMILLE_ID',
                            flex: 1

                        }, {
                            text: 'Nom',
                            flex: 1,
                            dataIndex: 'str_NAME'
                        }, {
                            text: 'Description',
                            flex: 1,
                            dataIndex: 'str_DESCRIPTION'
                        }, {
                            text: 'Prix',
                            flex: 1,
                            sortable: true,
                            // renderer: Ext.util.Format.dateRenderer('m/d/Y'),
                            dataIndex: 'int_PRICE'
                        }, {
                            xtype: 'actioncolumn',
                            width: 30,
                            sortable: false,
                            menuDisabled: true,
                            items: [{
                                    icon: 'resources/images/icons/fam/add.gif',
                                    tooltip: 'Choisir Produit',
                                    scope: this,
                                    handler: this.onChooseProductClick
                                }]
                        }], tbar: [{
                            text: 'Retour',
                            scope: this,
                            id: 'mytb',
                            handler: this.onRetourClick
                        }, '-',
                        {
                            xtype: 'textfield',
                            id: 'rechecher',
                            name: 'user',
                            emptyText: 'Rech'
                        }, {
                            text: 'rechercher',
                            tooltip: 'rechercher',
                            scope: this,
                            handler: this.onRechClick
                        }],
                    listeners: {
                        scope: this/*,
                         selectionchange: this.onSelectionChange*/
                    }
                }, {
                    columnWidth: 0.35,
                    margin: '0 0 0 10',
                    xtype: 'fieldset',
                    title: 'Resume Choix',
                    layout: 'anchor',
                    defaultType: 'textfield',
                    items: [
                        str_NAME_RESUME,
                        int_PRICE_RESUME,
                        {
                            fieldLabel: 'Quantite',
                            name: 'int_QUANTITY',
                            maskRe:/[0-9.]/,
                            id: 'int_QUANTITY',
                            listeners: {
                                change: function(f, e) {
                                    var in_QUANTITY = Ext.getCmp('int_QUANTITY').getValue();
                                    var int_PRICE_RESUME = Ext.getCmp('int_PRICE_RESUME').getValue();
                                    int_qte = Number(in_QUANTITY);
                                    var int_total_temp = Me.DisplayTotal(int_PRICE_RESUME, int_qte);
                                    var int_total = parseInt(int_total_temp);
                                    int_AMOUNT_RESUME.setValue(int_total);
                                }
                                //action_form_update_brdoutput(null, frmBRDOutput.getForm().getValues());
                            }
                          
                        },
                        int_AMOUNT_RESUME,
                        btn_select_prod
                    ]

                }]
        });
        this.callParent();
        OgridpanelID = Ext.getCmp('gridpanelID');
        


    }, onChooseProductClick: function(grid, rowIndex) {
        var rec = OgridpanelID.getStore().getAt(rowIndex);
        str_NAME_RESUME.setValue(rec.get('str_NAME'));
        int_PRICE_RESUME.setValue(rec.get('int_PRICE'));
        int_product_price = rec.get('int_PRICE');
        // alert("int_product_price  " + int_product_price);

    },
    DisplayTotal: function(int_price, int_qte) {
        var TotalAmount = 0;
        TotalAmount = int_qte * int_price;
        return TotalAmount;
    },
    onEndChooseClick: function() {
        alert("cucu");
        var strTabFamilleId = "";
        var internal_url = "";
        var ref = "";
        if (Omode === "create") {
            internal_url = url_services_transaction_famille_select + 'create';
        } else {
            internal_url = url_services_transaction_famille_select + 'update&lg_PREENREGISTREMENT_ID=' + ref;
        }

        //  alert(Ext.getCmp('P_KEY').getValue());

        Ext.Ajax.request({
            url: internal_url,
            params: {
                lg_FAMILLE_ID: Ext.getCmp('lg_FAMILLE_ID').getValue(),
                int_PRICE_RESUME: Ext.getCmp('int_PRICE_RESUME').getValue(),
                int_QUANTITY: Ext.getCmp('int_QUANTITY').getValue()

            },
            success: function(response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                if (object.success == 0) {
                    Ext.MessageBox.alert('Error Message', object.errors);
                    return;
                }
                //  Oview.getStore().reload();

            },
            failure: function(response)
            {

                var object = Ext.JSON.decode(response.responseText, false);
                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);
            }
        });
        this.up('window').close();
    },
    onRetourClick: function() {
        var xtype = "preenregistrementmanager";
        testextjs.app.getController('App').onLoadNewComponent(xtype, "", "", "");
    },
    onRechClick: function() {

        var val = Ext.getCmp('rechecher');
        OgridpanelID.getStore().load({
            params: {
                search_value: val.value
            }
        }, url_services_data_famille_select);
    }

});
