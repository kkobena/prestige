/* global Ext */

var url_services_data_famille_select_retourfournisseur = '../webservices/sm_user/famille/ws_data_retourfrs.jsp';
var url_services_transaction_retourfournisseur = '../webservices/commandemanagement/retourfournisseur/ws_transaction.jsp?mode=';
var url_services_data_bl_grossiste = '../webservices/commandemanagement/retourfournisseur/ws_data_bl_retour.jsp';
var url_services_data_retourfournisseurdetails = '../webservices/commandemanagement/retourfournisseurdetail/ws_data.jsp?lg_RETOUR_FRS_ID=';
var url_services_transaction_retourfournisseurdetails = '../webservices/commandemanagement/retourfournisseurdetail/ws_transaction.jsp?mode=';
var url_services_data_motifretour = '../webservices/configmanagement/motifretour/ws_data.jsp';
var url_services_data_famille_select_dovente = '../webservices/sm_user/famille/ws_data_jdbc.jsp';
var url_services_data_famille_select_retour = '../webservices/sm_user/famille/ws_data_retourfrs.jsp';


var Me;
var Omode;
var ref;
//var famille_id_search;
var in_total_vente;
var int_total_formated;
var int_montant_vente;
var int_montant_achat;
var LaborexWorkFlow;
var store_famille_dovente = null;

Ext.util.Format.decimalSeparator = ',';
Ext.util.Format.thousandSeparator = '.';
function amountformat(val) {
    return Ext.util.Format.number(val, '0,000.');
}

Ext.define('testextjs.view.commandemanagement.retourfournisseur.action.add', {
    extend: 'Ext.form.Panel',
    requires: [
        'Ext.selection.CellModel',
        'Ext.grid.*',
        'Ext.form.*',
        'Ext.layout.container.Column',
        'testextjs.model.Famille',
        'testextjs.controller.LaborexWorkFlow',
        'testextjs.model.BonLivraison',
        'testextjs.model.RetourFournisseurDetail'
    ],
    config: {
        odatasource: '',
        parentview: '',
        mode: '',
        titre: '',
        plain: true,
        maximizable: true,
        closable: false,
        nameintern: ''
    },
    xtype: 'retourfournisseurmanagerlist',
    id: 'retourfournisseurmanagerlistID',
    frame: true,
    title: 'Ajouter detail retour fournisseur',
    bodyPadding: 5,
    layout: 'column',
    initComponent: function () {


        Me = this;
        var itemsPerPage = 20;
//        famille_id_search = "";
        in_total_vente = 0;
        int_total_formated = 0;
        ref = this.getNameintern();
        titre = this.getTitre();

        LaborexWorkFlow = Ext.create('testextjs.controller.LaborexWorkFlow', {});
        store_famille_dovente = LaborexWorkFlow.BuildStore('testextjs.model.Famille', itemsPerPage, url_services_data_famille_select_retourfournisseur);
//        ref = this.getNameintern();



        url_services_data_retourfournisseurdetails = '../webservices/commandemanagement/retourfournisseurdetail/ws_data.jsp?lg_RETOUR_FRS_ID=' + ref;

        var storebonlivraison = new Ext.data.Store({
            model: 'testextjs.model.BonLivraison',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../RetourFournisseur',
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                },
                tiemout: 180000
            }
        });

        // storetypemotif
        var storetypemotif = new Ext.data.Store({
            model: 'testextjs.model.MotifRetour',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_motifretour,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });

        if (titre === "Modification fiche retour fournisseur") {
            url_services_data_famille_select_retourfournisseur = url_services_data_famille_select_retourfournisseur;
//            alert("url_services_data_famille_select_retourfournisseur:"+url_services_data_famille_select_retourfournisseur)
        } else {
            url_services_data_famille_select_retourfournisseur = "../webservices/sm_user/famille/ws_data_retourfrs.jsp";


        }


        var store_famille_retourfrs = new Ext.data.Store({
            model: 'testextjs.model.Famille',
            pageSize: itemsPerPage,
            // remoteFilter: true,
            proxy: {
                type: 'ajax',
                url: "../RetourFourData",
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                },
                
                timeout: 1800
            },
            autoLoad: false

        });
        var store_details_retourfournisseur = new Ext.data.Store({
            model: 'testextjs.model.RetourFournisseurDetail',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_services_data_retourfournisseurdetails,
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });

        this.cellEditing = new Ext.grid.plugin.CellEditing({
            clicksToEdit: 1
        });

        Ext.apply(this, {
//            width: 1200,
            width: '98%',
            fieldDefaults: {
                labelAlign: 'left',
                labelWidth: 90,
                anchor: '100%',
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
            //items: ['rech_prod', 'gridpanelID'],
            items: [
                {
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
                                    items: [
                                        {
                                            width: 400,
                                            xtype: 'combobox',
                                            fieldLabel: 'Bon de livraison',
                                            name: 'lg_BON_LIVRAISON_ID',
                                            margin: '0 15 0 0',
                                            id: 'lg_BON_LIVRAISON_ID',
                                            store: storebonlivraison,
                                            pageSize: 20,
                                            valueField: 'str_REF_LIVRAISON',
                                            displayField: 'str_REF_LIVRAISON',
//                                            typeAhead: true,
                                            minChars: 2,
                                            queryMode: 'remote',
                                            emptyText: 'Choisir un bon de livraison...',
                                            listeners: {
                                                select: function (cmp) {
                                                    var comboFamille = Ext.getCmp('str_NAME');
                                                    var record = cmp.findRecord(cmp.valueField || cmp.displayField, cmp.getValue()); //recupere la ligne d
                                                    Ext.getCmp('str_GROSSISTE_LIBELLE').setValue(record.get('str_GROSSISTE_LIBELLE'));
                                                    comboFamille.clearValue();
                                                    comboFamille.getStore().getProxy().url = "../RetourFourData?lg_BON_LIVRAISON_ID=" + cmp.getValue();
                                                    comboFamille.getStore().reload();
                                                    comboFamille.enable();
                                                    Ext.getCmp('str_NAME').focus(true, 100, function () {
                                                        Ext.getCmp('str_NAME').selectText(0, 1);

                                                    });
                                                }
                                            }
                                        },
                                        {
                                            xtype: 'displayfield',
                                            fieldLabel: 'Grossiste',
                                            // labelWidth: 100,
                                            flex: 1,
                                            id: 'str_GROSSISTE_LIBELLE',
                                            fieldStyle: "color:blue;",
                                            margin: '0 5 0 5'
                                        },
                                        {
                                            fieldLabel: 'Commentaire',
                                            emptyText: 'Commentaire',
                                            name: 'str_COMMENTAIRE',
                                            id: 'str_COMMENTAIRE',
                                            flex: 2,
                                            margin: '0 15 0 0'
                                        },
                                        {
                                            fieldLabel: 'Reponse',
                                            emptyText: 'Reponse',
                                            name: 'str_REPONSE_FRS',
                                            id: 'str_REPONSE_FRS',
                                            hidden: true,
                                            flex: 1,
                                            allowBlank: false
                                        }
                                    ]
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
                                    items: [
                                        //code ajouté
                                        {
                                            xtype: 'displayfield',
                                            fieldLabel: 'Id produit :',
                                            name: 'lg_FAMILLE_ID_VENTE',
                                            id: 'lg_FAMILLE_ID_VENTE',
                                            labelWidth: 120,
                                            hidden: true,
                                            fieldStyle: "color:blue;",
                                            margin: '0 15 0 0'

                                        },
                                        {
                                            xtype: 'combobox',
                                            fieldLabel: 'Article',
                                            name: 'str_NAME',
                                            id: 'str_NAME',
                                            store: store_famille_retourfrs,
                                            margins: '0 10 5 10',
                                            enableKeyEvents: true,
                                            valueField: 'str_DESCRIPTION',
                                            pageSize: 20, //ajout la barre de pagination
                                            displayField: 'str_DESCRIPTION',
                                            typeAhead: true,
                                            width: 450,
                                            queryMode: 'remote',
                                            disabled: true,
                                            emptyText: 'Choisir un article par Nom ou Cip...',
                                            listConfig: {
                                                getInnerTpl: function () {
                                                    return '<span style="width:100px;display:inline-block;">{int_CIP}</span>{str_DESCRIPTION} <span style="float: right; font-weight:600;"> ({int_PAF})</span>';
                                                }
                                            },
                                            listeners: {
                                                keypress: function (field, e) {

                                                    if (e.getKey() === e.BACKSPACE || e.getKey() === 46) {
                                                        if (field.getValue().length == 1) { // a decommenter si demande de reload toute la liste des produits
                                                            field.getStore().load();
                                                        }
                                                    }
                                                },
                                                select: function (cmp) {
                                                    var value = cmp.getValue();
                                                    var record = cmp.findRecord(cmp.valueField || cmp.displayField, value); //recupere la ligne de l'element selectionné

                                                    Ext.getCmp('lg_FAMILLE_ID_VENTE').setValue(record.get('lg_FAMILLE_ID'));

                                                    Ext.getCmp('lg_MOTIF_RETOUR').focus(true, 100, function () {
                                                        Ext.getCmp('lg_MOTIF_RETOUR').selectText(0, 1);

                                                    });
                                                }

                                            }
                                        },
                                        {
                                            xtype: 'combobox',
                                            fieldLabel: 'Motif',
                                            name: 'lg_MOTIF_RETOUR',
                                            id: 'lg_MOTIF_RETOUR',
                                            store: storetypemotif,
                                            valueField: 'lg_MOTIF_RETOUR',
                                            displayField: 'str_LIBELLE',
                                            typeAhead: true,
                                            pageSize: 20,
                                            queryMode: 'remote',
                                            flex: 1,
                                            emptyText: 'Choisir un Motif...',
                                            listeners: {
                                                select: function (cmp) {

                                                    Ext.getCmp('int_QUANTITE').focus(true, 100, function () {
                                                        Ext.getCmp('int_QUANTITE').selectText(0, 1);

                                                    });
                                                }

                                            }
                                        },
                                        {
                                            fieldLabel: 'Quantit&eacute;',
                                            emptyText: 'Quantite',
                                            name: 'int_QUANTITE',
                                            id: 'int_QUANTITE',
                                            xtype: 'numberfield',
                                            margin: '0 15 0 10',
                                            minValue: 1,
                                            width: 100,
                                            value: 1,
                                            allowBlank: false,
                                            regex: /[0-9.]/,
                                            enableKeyEvents: true,
                                            listeners: {
                                                specialKey: function (field, e, options) {
                                                    /*if (e.getKey() === e.ENTER) {
                                                     Me.onbtnadd();
                                                     }*/
                                                    if (e.getKey() === e.ENTER) {

                                                        if (Ext.getCmp('lg_FAMILLE_ID_VENTE').getValue() !== "" && Ext.getCmp('int_QUANTITE').getValue() > 0) {
                                                            Me.onbtnadd();

                                                        } else {
                                                            // Ext.MessageBox.alert('Error Message', 'Verifiez votre saisie svp', funt);
                                                            Ext.MessageBox.show({
                                                                title: 'Message d\'erreur',
                                                                width: 320,
                                                                msg: "Verifiez votre saisie svp",
                                                                buttons: Ext.MessageBox.OK,
                                                                icon: Ext.MessageBox.WARNING,
                                                                fn: function (buttonId) {
                                                                    if (buttonId === "ok") {
                                                                        Ext.getCmp('int_QUANTITE').focus(false, 100, function () {
                                                                            this.setFieldStyle('border: 2px solid #C0C0C0; background: #FFFFFF;');
                                                                        });
                                                                    }
                                                                }


                                                            });

                                                        }
                                                    }

                                                }}
                                        }, {
                                            text: 'Ajouter',
                                            id: 'btn_add',
                                            margins: '0 0 0 6',
                                            //  flex: 1,
                                            xtype: 'button',
                                            hidden: true,
                                            handler: this.onbtnadd,
                                            disabled: true
                                        }]
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'fieldset',
                    title: 'Detail(s) retour fournisseur',
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
                            store: store_details_retourfournisseur,
                            height: 300,
                            columns: [{
                                    text: 'Details Suggestion Id',
                                    flex: 1,
                                    sortable: true,
                                    hidden: true,
                                    dataIndex: 'lg_RETOUR_FRS_DETAIL',
                                    id: 'lg_RETOUR_FRS_DETAIL'
                                }, {
                                    text: 'Famille',
                                    flex: 1,
                                    sortable: true,
                                    hidden: true,
                                    dataIndex: 'lg_FAMILLE_ID'
                                },
                                {
                                    xtype: 'rownumberer',
                                    text: 'LG',
                                    width: 45,
                                    sortable: true/*,
                                     locked: true*/
                                },
                                {
                                    text: 'CIP',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'lg_FAMILLE_CIP'
                                },
                                {
                                    text: 'LEBELLE',
                                    flex: 2,
                                    sortable: true,
                                    dataIndex: 'lg_FAMILLE_NAME'
                                },
                                {
                                    text: 'DATE PEREMPTION',
                                    flex: 1,
                                    sortable: true,
                                    hidden: true,
                                    dataIndex: 'dt_PEREMPTION'
                                },
                                {
                                    text: 'STOCK',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'int_STOCK'
                                },
                                {
                                    text: 'QTE Retour',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'int_NUMBER_RETURN',
                                    MaskRe: /[0-9.]/,
                                    minValue: 1,
                                    editor: {
                                        xtype: 'numberfield',
                                        allowBlank: true,
                                        minValue: 1,
                                        maskRe: /[0-9.]/,
                                        selectOnFocus: true,
                                        hideTrigger: true
                                    }
                                },
                                {
                                    text: 'DIFF',
                                    flex: 1,
                                    sortable: true,
                                    dataIndex: 'int_DIFF'
                                },
                                {
                                    text: 'MOTIF',
                                    flex: 1,
                                    dataIndex: 'lg_MOTIF_RETOUR'
                                },
                                {
                                    xtype: 'actioncolumn',
                                    width: 30,
                                    sortable: false,
                                    menuDisabled: true,
                                    items: [{
                                            icon: 'resources/images/icons/fam/delete.png',
                                            tooltip: 'Supprimer',
                                            scope: this,
                                            handler: this.onRemoveClick
                                        }]
                                }
                            ],
                            bbar: {
                                xtype: 'pagingtoolbar',
                                pageSize: itemsPerPage,
                                store: store_details_retourfournisseur,
                                displayInfo: true,
                                plugins: new Ext.ux.ProgressBarPager()
                            },
                            listeners: {
                                scope: this,
                                //selectionchange: this.onSelectionChange
                            }
                        }]
                },
                {
                    xtype: 'toolbar',
                    ui: 'footer',
                    dock: 'bottom',
                    border: '0',
                    items: ['->',
                        {
                            text: 'Enregistrer',
                            id: 'btn_save',
                            iconCls: 'icon-clear-group',
                            scope: this,
                            handler: this.onbtnvalider
                        },
                        {
                            text: 'Retour',
                            id: 'btn_cancel',
                            iconCls: 'icon-clear-group',
                            scope: this,
                            hidden: false,
                            //disabled: true,
                            handler: this.onbtncancel
                        }
                    ]
                }]
        });

        this.callParent();
        this.on('afterlayout', this.loadStore, this, {
            delay: 1,
            single: true
        });
        if (titre === "Modification fiche retour fournisseur") {

            Ext.getCmp('lg_BON_LIVRAISON_ID').setValue(this.getOdatasource().str_REF_LIVRAISON);
            Ext.getCmp('str_REPONSE_FRS').setValue(this.getOdatasource().str_REPONSE_FRS);
            Ext.getCmp('str_COMMENTAIRE').setValue(this.getOdatasource().str_COMMENTAIRE);

            Ext.getCmp('lg_BON_LIVRAISON_ID').disable();
            Ext.getCmp('str_REPONSE_FRS').show();
            Ext.getCmp('str_GROSSISTE_LIBELLE').setValue(this.getOdatasource().str_GROSSISTE_LIBELLE);

            /*Ext.getCmp('str_NAME').enable();
             Ext.getCmp('str_NAME').focus();*/

//alert("url_services_data_famille_select_retourfournisseur "+url_services_data_famille_select_retourfournisseur+ "?lg_BON_LIVRAISON_ID=" + this.getOdatasource().str_REF_LIVRAISON);
            var comboFamille = Ext.getCmp('str_NAME');
            comboFamille.enable();
            comboFamille.focus();
            comboFamille.getStore().getProxy().url = "../RetourFourData?lg_BON_LIVRAISON_ID=" + this.getOdatasource().str_REF_LIVRAISON;
            comboFamille.getStore().reload();


        }


        Ext.getCmp('gridpanelID').on('edit', function (editor, e) {


            var qte = Number(e.record.data.int_NUMBER_RETURN);
            var qteStock = Number(e.record.data.int_STOCK);
            if (qte < qteStock) {
                Ext.Ajax.request({
                    url: '../webservices/commandemanagement/retourfournisseur/ws_transaction.jsp?mode=update',
                    params: {
                        lg_RETOUR_FRS_DETAIL: e.record.data.lg_RETOUR_FRS_DETAIL,
                        lg_RETOUR_FRS_ID: ref,
                        lg_FAMILLE_ID: e.record.data.lg_FAMILLE_ID,
                        lg_BON_LIVRAISON_ID: Ext.getCmp('lg_BON_LIVRAISON_ID').getValue(),
                        int_NUMBER_RETURN: qte
                    },
                    success: function (response)
                    {
                        var object = Ext.JSON.decode(response.responseText, false);
                        if (object.success === 0) {
                            Ext.MessageBox.alert('Error Message', object.errors);
                            return;
                        }

                        e.record.commit();
                        var OGrid = Ext.getCmp('gridpanelID');
                        OGrid.getStore().reload();
                        Ext.getCmp('str_NAME').setValue("");
                        Ext.getCmp('lg_MOTIF_RETOUR').setValue("");
                        Ext.getCmp('int_QUANTITE').setValue(1);
                        Ext.getCmp('str_NAME').focus();
                    },
                    failure: function (response)
                    {
                        console.log("Bug " + response.responseText);
                        Ext.MessageBox.alert('Error Message', response.responseText);
                    }
                });
            } else {
                var OGrid = Ext.getCmp('gridpanelID');
                OGrid.getStore().reload();
                Ext.MessageBox.alert('Error Message', 'Verifier la quantite a retourner');
            }

        });
    },
    loadStore: function () {
        Ext.getCmp('gridpanelID').getStore().load({
            callback: this.onStoreLoad
        });
    },
    onStoreLoad: function () {

    },
    onbtncancel: function () {

        var xtype = "";
        xtype = "retourfrsmanager";
        testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "", "", "");
    },
    onbtnvalider: function () {

        Ext.MessageBox.confirm('Confirmation',
                'Les quantites seront destockees du stock <br> Voulez vous continuer?',
                function (btn) {
                    if (btn == 'yes') {
                        testextjs.app.getController('App').ShowWaitingProcess();
                        var param = {"ref": ref,
                            "refTwo": Ext.getCmp('str_REPONSE_FRS').getValue(),
                            "description": Ext.getCmp('str_COMMENTAIRE').getValue()
                        };
                        Ext.Ajax.request({
                            method: 'POST',
                            headers: {'Content-Type': 'application/json'},
                            url: '../api/v1/produit/validerretourfour',
                            params: Ext.JSON.encode(param),
//                            url: url_services_transaction_retourfournisseur + 'validerRupture',
                            /* params: {
                             lg_RETOUR_FRS_ID: ref,
                             str_REPONSE_FRS: Ext.getCmp('str_REPONSE_FRS').getValue(),
                             str_COMMENTAIRE: Ext.getCmp('str_COMMENTAIRE').getValue()
                             
                             },*/
                            success: function (response)
                            {
                                testextjs.app.getController('App').StopWaitingProcess();
                                var object = Ext.JSON.decode(response.responseText, false);
                                if (!object.success) {
                                    Ext.MessageBox.alert('Error Message', object.msg);
                                    return;
                                } else {
                                    Ext.MessageBox.alert('Confirmation', object.msg);
                                    var xtype = "";
                                    xtype = "retourfrsmanager";
                                    testextjs.app.getController('App').onLoadNewComponentWithDataSource(xtype, "", "", "");
                                }

                            },
                            failure: function (response)
                            {
                                testextjs.app.getController('App').StopWaitingProcess();
                                var object = Ext.JSON.decode(response.responseText, false);
                                console.log("Bug " + response.responseText);
                                Ext.MessageBox.alert('Error Message', response.responseText);
                            }
                        });


                    }
                });

    },
    checkIfGridIsEmpty: function () {
        var gridTotalCount = Ext.getCmp('gridpanelID').getStore().getTotalCount();
        return gridTotalCount;
    },

    setTitleFrame: function (str_data) {
        this.title = this.title + " :: Ref " + str_data;
        ref = str_data;
        url_services_data_retourfournisseurdetails = '../webservices/commandemanagement/retourfournisseurdetail/ws_data.jsp?lg_RETOUR_FRS_ID=' + ref;
        var OGrid = Ext.getCmp('gridpanelID');
        url_services_data_retourfournisseurdetails = '../webservices/commandemanagement/retourfournisseurdetail/ws_data.jsp?lg_RETOUR_FRS_ID=' + ref;
        OGrid.getStore().getProxy().url = url_services_data_retourfournisseurdetails;
        OGrid.getStore().reload();
    },

    onfilterBLcheck: function () {
        var OGrid = Ext.getCmp('lg_BON_LIVRAISON_ID');
        var lg_BON_LIVRAISON_ID = OGrid.getValue();
        var int_name_size = lg_BON_LIVRAISON_ID.length;


        if (lg_BON_LIVRAISON_ID !== null && lg_BON_LIVRAISON_ID !== "" && lg_BON_LIVRAISON_ID !== undefined) {
            var OComponent_length = lg_BON_LIVRAISON_ID.length;

            var url_final = url_services_data_bl_grossiste + "?search_value=" + lg_BON_LIVRAISON_ID + "&str_STATUT=is_Closed";
            if (OComponent_length >= 3) {
                OGrid.getStore().getProxy().url = url_final;
                OGrid.getStore().reload();
            }
        } else {
            //alert('ici');
            OGrid.getStore().getProxy().url = url_services_data_bl_grossiste + "&str_STATUT=is_Closed";
            OGrid.getStore().reload();

        }



    },
    onbtnadd: function () {

        if (ref === "") {
            ref = null;
        } else if (ref === undefined) {
            ref = null;
        }
        if (Ext.getCmp('lg_BON_LIVRAISON_ID').getValue() === null) {
            Ext.MessageBox.alert('Error Message', 'Renseignez le BonLivraison ');
            return;
        }
        if (Ext.getCmp('lg_MOTIF_RETOUR').getValue() === null) {
            Ext.MessageBox.alert('Error Message', 'Renseignez le motif ');
            return;
        }
        if (Ext.getCmp('str_NAME').getValue() == null || Ext.getCmp('str_NAME').getValue() == "") {
            Ext.MessageBox.alert('Error Message', 'Veuillez selecttionner un article');
            return;
        }

//
        Ext.Ajax.request({
            url: '../webservices/commandemanagement/retourfournisseur/ws_transaction.jsp?mode=create',
            params: {
//                lg_FAMILLE_ID: Ext.getCmp('str_NAME').getValue(),
                lg_FAMILLE_ID: Ext.getCmp('lg_FAMILLE_ID_VENTE').getValue(),
                lg_RETOUR_FRS_ID: ref,
                lg_RETOUR_FRS_DETAIL: null,
                lg_BON_LIVRAISON_ID: Ext.getCmp('lg_BON_LIVRAISON_ID').getValue(),
                str_REPONSE_FRS: Ext.getCmp('str_REPONSE_FRS').getValue(),
                str_COMMENTAIRE: Ext.getCmp('str_COMMENTAIRE').getValue(),
                lg_MOTIF_RETOUR: Ext.getCmp('lg_MOTIF_RETOUR').getValue(),
                int_NUMBER_RETURN: Ext.getCmp('int_QUANTITE').getValue()


            },
            success: function (response)
            {
                var object = Ext.JSON.decode(response.responseText, false);
                if (object.success == "0") {
                    Ext.MessageBox.alert('Error Message', object.errors);

                    return;
                } else {
                    //Ext.MessageBox.alert('Confirmation', object.errors);
                    ref = object.ref;
                    // url_services_data_retourfournisseurdetails = '../webservices/commandemanagement/retourfournisseurdetail/ws_data.jsp?lg_RETOUR_FRS_ID=' + ref;
                    // Me.setTitleFrame(object.ref);
                    Ext.getCmp('lg_BON_LIVRAISON_ID').disable();
                    Ext.getCmp('str_NAME').setValue("");
                    Ext.getCmp('lg_MOTIF_RETOUR').setValue("");
                    Ext.getCmp('int_QUANTITE').setValue(1);

                    var OGrid = Ext.getCmp('gridpanelID');
                    var OComboFamille = Ext.getCmp('str_NAME');
                    OComboFamille.focus();
//                    Ext.getCmp('str_NAME').getStore().reload();
                    OGrid.getStore().getProxy().url = '../webservices/commandemanagement/retourfournisseurdetail/ws_data.jsp?lg_RETOUR_FRS_ID=' + ref;
                    OGrid.getStore().reload();

                    OComboFamille.getStore().getProxy().url = url_services_data_famille_select_retourfournisseur + "?lg_BON_LIVRAISON_ID=" + value;
                    Me.SearchArticle();

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
    onRemoveClick: function (grid, rowIndex) {
        Ext.MessageBox.confirm('Message',
                'Confirmer la suppresssion',
                function (btn) {
                    if (btn == 'yes') {
                        var rec = grid.getStore().getAt(rowIndex);

                        Ext.Ajax.request({
                            url: url_services_transaction_retourfournisseur + 'deleteDetail',
                            params: {
                                lg_RETOUR_FRS_DETAIL: rec.get('lg_RETOUR_FRS_DETAIL')
                            },
                            success: function (response)
                            {
                                var object = Ext.JSON.decode(response.responseText, false);
                                if (object.success === 0) {
                                    Ext.MessageBox.alert('Error Message', object.errors);
                                    return;
                                }
                                grid.getStore().reload();
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
    },
    SearchArticle: function (url) {
        var lg_BON_LIVRAISON_ID = "";
        var lg_FAMILLE_ID = "";
        if (Ext.getCmp('lg_BON_LIVRAISON_ID').getValue() !== null) {
            lg_BON_LIVRAISON_ID = Ext.getCmp('lg_BON_LIVRAISON_ID').getValue();
        }
        if (Ext.getCmp('str_NAME').getValue() !== null) {
            lg_FAMILLE_ID = Ext.getCmp('str_NAME').getValue();
        }
        Ext.getCmp('str_NAME').getStore().load({
            params: {
                lg_FAMILLE_ID: lg_FAMILLE_ID,
                lg_BON_LIVRAISON_ID: lg_BON_LIVRAISON_ID
            }
        }, url_services_data_famille_select_retourfournisseur, function (r, op, suc) {
            console.log(r);
        });




    }
});


