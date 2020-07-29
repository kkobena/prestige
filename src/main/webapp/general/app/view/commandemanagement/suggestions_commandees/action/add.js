var url_services_data_famille_select_dovente_suggerer = '../webservices/sm_user/famille/ws_data.jsp';
var url_services_transaction_suggerercde = '../webservices/sm_user/suggerercde/ws_transaction.jsp?mode=';
var url_services_data_grossiste_suggerer = '../webservices/configmanagement/grossiste/ws_data.jsp';

var Me;
var Omode;
var ref;
var famille_id_search;
var in_total_vente;
var int_total_formated;


Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}


var type;
//            testextjs.view.commandemanagement.suggestion.action.add
Ext.define('testextjs.view.commandemanagement.suggestion.action.add', {
    extend: 'Ext.window.Window',
    xtype: 'addsuggestion',
    id: 'addsuggestionID1',
    requires: [
        'Ext.form.*',
        'Ext.window.Window',
        'testextjs.model.Suggestion',
        'testextjs.model.TypeSuggestion',
        'testextjs.model.Grossiste',
        'testextjs.model.TSuggestionOrderDetails',
        'testextjs.view.configmanagement.famille.*'

    ],
    config: {
        odatasource: '',
        parentview: '',
        mode: '',
        titre: '',
        type: ''
    },
    initComponent: function () {

        Oview = this.getParentview();
        Omode = this.getMode();
        Me = this;
        Me = this;
        var itemsPerPage = 20;
        famille_id_search = "";
        in_total_vente = 0;
        int_total_formated = 0;

        url_services_data_detailssuggerer = '../webservices/sm_user/suggerercde/ws_data.jsp?lg_SUGGESTION_ORDER_ID=' + ref;


        var storerepartiteur = new Ext.data.Store({
            model: 'testextjs.model.Grossiste',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_grossiste_suggerer,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            },
            autoLoad: true

        });

        var store_famille_sug = new Ext.data.Store({
            model: 'testextjs.model.Famille',
            pageSize: itemsPerPage,
            remoteFilter: true,
            proxy: {
                type: 'ajax',
                url: url_services_data_famille_select_dovente,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                },
                timeout: 240000
            },
            autoLoad: true

        });


        store_details_sugg = new Ext.data.Store({
            model: 'testextjs.model.TSuggestionOrderDetails',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_detailssuggerer,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            },
            autoLoad: true

        });
        alert("3");

        var int_BUTOIR = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    fieldLabel: 'Butoir ::',
                    labelWidth: 50,
                    name: 'int_DATE_BUTOIR_ARTICLE',
                    id: 'int_DATE_BUTOIR_ARTICLE',
                    fieldStyle: "color:blue;",
                    margin: '0 15 0 0',
                    value: "0"
                });


        var int_VENTE = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    fieldLabel: 'Valeur Vente ::',
                    labelWidth: 95,
                    name: 'int_VENTE',
                    id: 'int_VENTE',
                    fieldStyle: "color:blue;",
                    margin: '0 15 0 0',
                    value: "0"
                });

        var int_ACHAT = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    fieldLabel: 'Valeur Achat ::',
                    labelWidth: 95,
                    name: 'int_ACHAT',
                    id: 'int_ACHAT',
                    fieldStyle: "color:blue;",
                    margin: '0 15 0 0',
                    value: "0"
                });




        alert("4");

        this.cellEditing = new Ext.grid.plugin.CellEditing({
            clicksToEdit: 1
        });





        var form = new Ext.form.Panel({
            bodyPadding: 10,
            fieldDefaults: {
                labelAlign: 'right',
                labelWidth: 160,
                msgTarget: 'side'
            },
            layout: {
                type: 'vbox',
                align: 'stretch',
                padding: 10
            },
            defaults: {
                flex: 1
            },
            id: 'panelID',
            items: ['rech_prod', 'gridpanelID'],
            items: [{
                    items: [{
                            xtype: 'fieldset',
                            title: 'Infos Generales',
                            collapsible: true,
                            defaultType: 'textfield',
                            layout: 'anchor',
                            defaults: {
                                anchor: '100%'
                            },
                            items: [
                                {
                                    xtype: 'fieldcontainer',
                                    layout: 'hbox',
                                    combineErrors: true,
                                    defaultType: 'textfield',
                                    defaults: {
                                        hideLabel: 'true'
                                    },
                                    items: [{
                                            xtype: 'combobox',
                                            fieldLabel: 'Repartiteur',
                                            allowBlank: false,
                                            name: 'Code.Rep',
                                            margin: '0 15 0 0',
                                            id: 'lg_GROSSISTE_ID',
                                            store: storerepartiteur,
                                            valueField: 'lg_GROSSISTE_ID',
                                            displayField: 'str_LIBELLE',
                                            typeAhead: true,
                                            queryMode: 'remote',
                                            emptyText: 'Choisir un repartiteur...'
                                        },
                                        int_BUTOIR,
                                        int_ACHAT,
                                        int_VENTE]
                                }]
                        }
                    ]

                },
                {
                    items: [{
                            xtype: 'fieldset',
                            title: 'Ajout Produit',
                            collapsible: true,
                            defaultType: 'textfield',
                            layout: 'anchor',
                            defaults: {
                                anchor: '100%'
                            },
                            items: [
                                {
                                    xtype: 'fieldcontainer',
                                    fieldLabel: 'Produit',
                                    layout: 'hbox',
                                    combineErrors: true,
                                    defaultType: 'textfield',
                                    defaults: {
                                        hideLabel: 'true'
                                    },
                                    items: [{
                                            name: 'CIP',
                                            id: 'int_CIP',
                                            fieldLabel: 'int_CIP',
                                            flex: 1,
                                            emptyText: 'CIP',
                                            maskRe: /[0-9.]/,
                                            allowBlank: false,
                                            listeners: {
                                                'render': function (cmp) {
                                                    cmp.getEl().on('keypress', function (e) {
                                                        if (e.getKey() === e.ENTER) {
                                                            var search_cip_val = Ext.getCmp('int_CIP').getValue();
                                                            Ext.getCmp('btn_add').enable();
                                                            Me.getFamilleByCip(search_cip_val);
                                                        }

                                                    });

                                                }

                                            }
                                        }, {
                                            name: 'EAN 13',
                                            id: 'int_EAN13',
                                            fieldLabel: 'int_EAN13',
                                            flex: 1,
                                            margins: '0 0 0 6',
                                            emptyText: 'EAN 13',
                                            maskRe: /[0-9.]/,
                                            maxLength: 13,
                                            allowBlank: false
                                        }
                                        , {
                                            xtype: 'combobox',
                                            flex: 1,
                                            margins: '0 0 0 6',
                                            pageSize: 20,
                                            fieldLabel: '',
                                            hideTrigger: true,
                                            displayField: 'str_NAME',
                                            id: 'str_NAME',
                                            minChars: 4,
                                            queryDelay: 250,
                                            store: store_famille_sug,
                                            queryParam: 'str_NAME',
                                            typeAhead: true,
                                            typeAheadDelay: 200,
                                            itemId: 'str_NAME',
                                            queryMode: 'local',
                                            lastQuery: '',
                                            valueField: 'str_NAME',
                                            listeners: {
                                                select: function () {
                                                    Ext.getCmp('btn_add').enable();
                                                    var search_val = Ext.getCmp('str_NAME').getValue();
                                                    Me.getFamilleByName(search_val);
                                                },
                                                change: function () {
                                                    Me.onfiltercheck();
                                                    Ext.getCmp('int_CIP').setValue("");
                                                    Ext.getCmp('int_EAN13').setValue("");

                                                }
                                            }
                                        }, {
                                            text: 'Edit',
                                            margins: '0 0 0 6',
                                            hidden: true,
                                            xtype: 'button'
                                        }, {
                                            text: 'Ajouter',
                                            id: 'btn_add',
                                            margins: '0 0 0 6',
                                            //  flex: 1,
                                            xtype: 'button',
                                            handler: this.onbtnadd,
                                            disabled: true
                                        }]
                                }
                            ]
                        }
                    ]
                }, {
                    xtype: 'fieldset',
                    title: 'Detail(s) Suggestion',
                    collapsible: true,
                    defaultType: 'textfield',
                    layout: 'anchor',
                    defaults: {
                        anchor: '100%'
                    },
                    items: [
                        {
                            columnWidth: 0.65,
                            xtype: 'gridpanel',
                            id: 'gridpanelID',
                            plugins: [this.cellEditing],
                            store: store_details_sugg,
                            height: 400,
                            columns: [{
                                    text: 'Details Suggestion Id',
                                    flex: 1,
                                    sortable: true,
                                    hidden: true,
                                    dataIndex: 'lg_SUGGESTION_ORDER_DETAILS_ID',
                                    id: 'lg_SUGGESTION_ORDER_DETAILS_ID'
                                }, {
                                    text: 'Famille',
                                    flex: 1,
                                    sortable: true,
                                    hidden: true,
                                    dataIndex: 'lg_FAMILLE_ID'
                                }, {
                                    xtype: 'rownumberer',
                                    text: 'LG',
                                    width: 45,
                                    sortable: true/*,
                                     locked: true*/
                                }, {
                                    text: 'CIP',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'str_FAMILLE_CIP'
                                }, {
                                    text: 'DESIGNATION',
                                    flex: 2,
                                    sortable: true,
                                    dataIndex: 'str_FAMILLE_NAME'
                                }, {
                                    text: 'STOCK',
                                    flex: 2,
                                    sortable: true,
                                    dataIndex: 'int_STOCK'
                                }, {
                                    header: 'Q.CDE',
                                    dataIndex: 'int_NUMBER',
                                    flex: 1,
                                    editor: {
                                        xtype: 'numberfield',
                                        allowBlank: false,
                                        regex: /[0-9.]/
                                    }
                                }, {
                                    text: 'SEUIL',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'int_SEUIL'
                                }, {
                                    xtype: 'actioncolumn',
                                    width: 30,
                                    sortable: false,
                                    menuDisabled: true,
                                    items: [{
                                            icon: 'resources/images/icons/fam/delete.png',
                                            tooltip: 'Delete',
                                            scope: this,
                                            handler: this.onRemoveClick
                                        }]
                                }],
                            bbar: {
                                xtype: 'pagingtoolbar',
                                pageSize: 10,
                                store: store_details_sugg,
                                displayInfo: true,
                                plugins: new Ext.ux.ProgressBarPager()
                            },
                            listeners: {
                                scope: this,
                                selectionchange: this.onSelectionChange
                            }
                        }]
                }]
        });

        this.callParent();

        var win = new Ext.window.Window({
            autoShow: true,
            title: this.getTitre(),
            width: 900,
            height: 600,
            minWidth: 300,
            minHeight: 200,
            layout: 'fit',
            plain: true,
            items: form,
            buttons: [{
                    text: 'Enregistrer',
                    handler: this.onbtnsave
                }, {
                    text: 'Annuler',
                    handler: function () {
                        win.close();
                    }
                }]
        });

    },

    onbtnaddclt: function () {
        var mybtntitle = Ext.getCmp('btn_add_clt').getText();
        new testextjs.view.sm_user.dovente.action.addclt({
            obtntext: mybtntitle,
            odatasource: ref,
            nameintern: ref_vente,
            parentview: this,
            mode: "create",
            titre: "Choisir Client"
        });
    },
    checkIfGridIsEmpty: function () {
        var gridTotalCount = Ext.getCmp('gridpanelID').getStore().getTotalCount();
        return gridTotalCount;
    },
    onValidatePreVenteClick: function () {

    },
    getFamilleByName: function (str_famille_name) {
        var url_services_data_famille_select_dovente_search_suggerer = url_services_data_famille_select_dovente_suggerer + "?search_value=" + str_famille_name;
        Ext.Ajax.request({
            url: url_services_data_famille_select_dovente_search_suggerer,
            params: {
            },
            success: function (response)
            {

                var object = Ext.JSON.decode(response.responseText, false);
                var OFamille = object.results[0];
                var int_CIP = OFamille.int_CIP;
                var int_EAN13 = OFamille.int_EAN13;
                Ext.getCmp('int_CIP').setValue(int_CIP);
                Ext.getCmp('int_EAN13').setValue(int_EAN13);
                famille_id_search = OFamille.lg_FAMILLE_ID;



                url_services_data_famille_select_dovente_search_suggerer = url_services_data_famille_select_dovente_suggerer;

            },
            failure: function (response)
            {
                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);
            }
        });

    },
    setTitleFrame: function (str_data) {
        this.title = this.title + " :: Ref " + str_data;
        ref = str_data;
        url_services_data_detailssuggerer = '../webservices/sm_user/suggerercde/ws_data.jsp?lg_SUGGESTION_ORDER_ID=' + ref;

        var OGrid = Ext.getCmp('gridpanelID');
        url_services_data_detailssuggerer = '../webservices/sm_user/suggerercde/ws_data.jsp?lg_SUGGESTION_ORDER_ID=' + ref;

        OGrid.getStore().getProxy().url = url_services_data_detailssuggerer;
        OGrid.getStore().reload();


    },
    getFamilleByCip: function (str_famille_cip) {
        var url_services_data_famille_select_dovente_search_suggerer = url_services_data_famille_select_dovente_suggerer + "?search_cip=" + str_famille_cip;
        Ext.Ajax.request({
            url: url_services_data_famille_select_dovente_search_suggerer,
            params: {
            },
            success: function (response)
            {
                var object = Ext.JSON.decode(response.responseText, false);

                var OFamille = object.results[0];
                var str_NAME = OFamille.str_NAME;
                var int_EAN13 = OFamille.int_EAN13;


                Ext.getCmp('str_NAME').setValue(str_NAME);
                Ext.getCmp('int_EAN13').setValue(int_EAN13);
                famille_id_search = OFamille.lg_FAMILLE_ID;
                famille_price_search = Number(OFamille.int_PRICE);
                famille_qte_search = 1;

                url_services_data_famille_select_dovente_search_suggerer = url_services_data_famille_select_dovente_suggerer;

            },
            failure: function (response)
            {

                console.log("Bug " + response.responseText);
                Ext.MessageBox.alert('Error Message', response.responseText);
            }
        });

    },
    onfiltercheck: function () {
        var str_name = Ext.getCmp('str_NAME').getValue();
        var int_name_size = str_name.length;
        if (int_name_size < 4) {
            Ext.getCmp('btn_add').disable();


        }

    },
    DisplayTotal: function (int_price, int_qte) {
        var TotalAmount_final = 0;
        var TotalAmount_temp = int_qte * int_price;
        var TotalAmount = Number(TotalAmount_temp);
        return TotalAmount;
    },
    DisplayMonnaie: function (int_total, int_amount_recu) {
        var TotalMonnaie = 0;
        Ext.getCmp('int_REEL_RESTE').setValue(int_amount_recu - int_total);
        if (int_total <= int_amount_recu) {
            var TotalMonnaie_temp = int_amount_recu - int_total;
            TotalMonnaie = Number(TotalMonnaie_temp);
            return TotalMonnaie;
        } else {
            return null;
        }
        return TotalMonnaie;
    },
    onbtnadd: function () {

    },
    onbtncloturer: function () {



    },
    onRemoveClick: function (grid, rowIndex) {
        Ext.MessageBox.confirm('Message',
                'confirm la suppresssion',
                function (btn) {
                    if (btn == 'yes') {
                        var rec = grid.getStore().getAt(rowIndex);
                        alert(url_services_transaction_detailsvente + 'delete');

                        Ext.Ajax.request({
                            url: url_services_transaction_detailsvente + 'delete',
                            params: {
                                lg_PREENREGISTREMENT_DETAIL_ID: rec.get('lg_PREENREGISTREMENT_DETAIL_ID')
                            },
                            success: function (response)
                            {
                                var object = Ext.JSON.decode(response.responseText, false);
                                if (object.success === 0) {
                                    Ext.MessageBox.alert('Error Message', object.errors);
                                    return;
                                }
                                grid.getStore().reload();

                                in_total_vente = Number(object.total_vente);

                                if (in_total_vente > 0) {
                                    Ext.getCmp('btn_devis').enable();
                                } else {
                                    Ext.getCmp('btn_devis').disable();
                                }

                                int_total_formated = Ext.util.Format.number(in_total_vente, '0,000.');
                                Ext.getCmp('int_TOTAL_VENTE').setValue(int_total_formated + '  CFA');
                                Ext.getCmp('int_TOTAL_VENTE_RECAP').setValue(int_total_formated + '  CFA');

                                int_total_product = Number(object.int_total_product);
                                Ext.getCmp('int_TOTAL_PRODUIT').setValue(int_total_product + '  Produit(s)');
                                Ext.getCmp('int_NB_PROD_RECAP').setValue(int_total_product + '  Produit(s)');

                            },
                            failure: function (response)
                            {

                                var object = Ext.JSON.decode(response.responseText, false);

                                console.log("Bug " + response.responseText);
                                Ext.MessageBox.alert('Error Message', response.responseText);

                            }
                        });
                        return;
                    }
                });


    },
    onPdfClick: function () {
        var chaine = location.pathname;
        var reg = new RegExp("[/]+", "g");
        var tableau = chaine.split(reg);
        var sitename = tableau[1];
        var linkUrl = url_services_pdf_ticket + '?lg_PREENREGISTREMENT_ID=' + ref;
        window.open(linkUrl);

        var xtype = "";
        if (my_view_title === "by_cloturer_vente") {
            xtype = "ventemanager";
            testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "", "", "");
        } else {
            xtype = "preenregistrementmanager";
            testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "", "", "");
        }

    },
    changeRenderer: function (val) {
        if (val > 0) {
            return '<span style="color:green;">' + val + '</span>';
        } else if (val < 0) {
            return '<span style="color:red;">' + val + '</span>';
        }
        return val;
    },
    pctChangeRenderer: function (val) {
        if (val > 0) {
            return '<span style="color:green;">' + val + '%</span>';
        } else if (val < 0) {
            return '<span style="color:red;">' + val + '%</span>';
        }
        return val;
    },
    renderRating: function (val) {
        switch (val) {
            case 0:
                return 'A';
            case 1:
                return 'B';
            case 2:
                return 'C';
        }
    },
    onSelectionChange: function (model, records) {
        var rec = records[0];
        if (rec) {
            this.getForm().loadRecord(rec);
        }
    },
    onsplitovalue: function (Ovalue) {

        var int_ovalue;
        var string = Ovalue.split(" ");
        int_ovalue = string[0];

        return int_ovalue;

    }



});


