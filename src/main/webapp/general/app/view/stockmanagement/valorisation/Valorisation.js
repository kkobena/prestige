/* global Ext */

Ext.define('testextjs.view.stockmanagement.valorisation.Valorisation', {
    extend: 'Ext.form.Panel',
    xtype: 'valorisationstock',

    frame: true,
    title: 'Valorisation du stock',
    bodyPadding: 10,
    // width: '60%',
    width: 580,
    closable: false,
    fieldDefaults: {
        labelAlign: 'right',
        labelWidth: 115,
        msgTarget: 'side'
    },
    config: {
        nameintern: '',
        titre: ''
    },
    initComponent: function () {

        var itemsPerPage = 20;
        var store_famillearticle = new Ext.data.Store({
            model: 'testextjs.model.FamilleArticle',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../webservices/configmanagement/famillearticle/ws_data_other.jsp',
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });

        var str_NAME_USER = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    //allowBlank: false,
                    fieldLabel: 'UTILISATEUR',
                    name: 'str_NAME_USER',
                    id: 'str_NAME_USER',
                    fieldStyle: "color:blue;font-size:1.5em;font-weight: 700;",
                    emptyText: 'str_NAME_USER'
                });
        var store_zonegeo = new Ext.data.Store({
            model: 'testextjs.model.ZoneGeographique',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../webservices/configmanagement/zonegeographique/ws_data_other.jsp',
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });
        var storerepartiteur = new Ext.data.Store({
            model: 'testextjs.model.Grossiste',
            pageSize: itemsPerPage,
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: '../webservices/configmanagement/grossiste/ws_data_other.jsp',
                reader: {
                    type: 'json',
                    root: 'results',
                    totalProperty: 'total'
                }
            }

        });


        var TOTAL_VENTE = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    fieldLabel: 'VALEUR VENTE',
                    name: 'TOTAL_VENTE',
                    id: 'TOTAL_VENTE',
                    emptyText: 'TOTAL_VENTE',
                    renderer: function (v) {
                                        return Ext.util.Format.number(v, '0,000.');
                                    },
                    fieldStyle: "color:blue;font-size:1.5em;font-weight: 900;",
                    value: 0
                });

        var TOTAL_ACHAT = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    fieldLabel: 'VALEUR ACHAT',
                    name: 'TOTAL_ACHAT',
                    id: 'TOTAL_ACHAT',
                    emptyText: 'TOTAL_ACHAT',
                    renderer: function (v) {
                                        return Ext.util.Format.number(v, '0,000.');
                                    },
                    fieldStyle: "color:blue;font-size:1.5em;font-weight: 900;",
                    value: 0
                });

        var dt_CREATED = new Ext.form.field.Display(
                {
                    xtype: 'displayfield',
                    //allowBlank: false,
                    fieldLabel: 'Date.Système',
                    name: 'dt_CREATED',
                    id: 'dt_CREATED',
                    fieldStyle: "color:green;font-size:1.5em;font-weight: 900;",
                    emptyText: 'dt_CREATED'
                });

        var store_type = new Ext.data.Store({
            fields: ['str_TYPE_TRANSACTION', 'str_STATUT_TRANSACTION'],
            data: [{str_TYPE_TRANSACTION: 0, str_STATUT_TRANSACTION: 'Simple'}, {str_TYPE_TRANSACTION: 1, str_STATUT_TRANSACTION: 'Famille'}, {str_TYPE_TRANSACTION: 2, str_STATUT_TRANSACTION: 'Emplacement'}, {str_TYPE_TRANSACTION: 3, str_STATUT_TRANSACTION: 'Grossiste'}]
        });

        this.items = [{
                xtype: 'fieldset',
                title: 'Infos Utilisateur',
                defaultType: 'textfield',
                defaults: {
                    anchor: '100%'
                },
                items: [
                    str_NAME_USER
                ]
            },
            {
                xtype: 'fieldset',
                title: 'Crit&egrave;res de recherche',
                defaultType: 'textfield',
                defaults: {
                    anchor: '100%'
                },
                items: [
                    {
                        xtype: 'datefield',
                        format: 'd/m/Y',
                        emptyText: 'Date',
                        submitFormat: 'Y-m-d',
                        fieldLabel: 'Date',
                        margin: '0 0 5 0',
                        value: new Date(),
                        maxValue: new Date(),
                        flex: 1,
                        id: 'dt_periode'

                    },

                    {
                        xtype: 'combobox',
                        fieldLabel: 'Filtrer par',
//                        margins: '0 0 0 10',
                        id: 'str_TYPE_TRANSACTION',
                        store: store_type,
                        valueField: 'str_TYPE_TRANSACTION',
                        displayField: 'str_STATUT_TRANSACTION',
//                            typeAhead: true,
                        editable: false,
                        queryMode: 'local',
                        flex: 1,
                        value: 0,
                        emptyText: 'Filtrer par...',
                        listeners: {
                            select: function (cmp) {
                                var value = cmp.getValue();

                                var OGridFamille = Ext.getCmp('lg_FAMILLEARTICLE_ID');
                                var OGridemplacement = Ext.getCmp('lg_ZONE_GEO_ID');
                                var OGridGrossiste = Ext.getCmp('lg_GROSSISTE_ID');
                                var OContainerInterval = Ext.getCmp('contenaire_intervalle');
                                OContainerInterval.hide();
                                Ext.getCmp('str_BEGIN').reset();
                                Ext.getCmp('str_END').reset();
                                if (value == 0) {
                                    OGridFamille.hide();
                                    OGridemplacement.hide();
                                    OGridGrossiste.hide();
                                }
                                if (value == 1) {
                                    OGridFamille.show();
                                    OGridemplacement.hide();
                                    OGridGrossiste.hide();

                                    OGridemplacement.setValue("");
                                    OGridGrossiste.setValue("");
                                } else if (value == 2) {
                                    OGridemplacement.show();
                                    OGridFamille.hide();
                                    OGridGrossiste.hide();

                                    OGridFamille.setValue("");
                                    OGridGrossiste.setValue("");
                                } else if (value == 3) {
                                    OGridGrossiste.show();
                                    OGridFamille.hide();
                                    OGridemplacement.hide();

                                    OGridFamille.setValue("");
                                    OGridemplacement.setValue("");
                                }



                            }
                        }
                    }, {
                        xtype: 'combobox',
                        fieldLabel: 'Famille article',
                        name: 'lg_FAMILLEARTICLE_ID',
                        id: 'lg_FAMILLEARTICLE_ID',
                        store: store_famillearticle,
                        hidden: true,
                        valueField: 'lg_FAMILLEARTICLE_ID',
                        pageSize: itemsPerPage,
                        displayField: 'str_LIBELLE',
                        typeAhead: true,
                        queryMode: 'remote',
                        emptyText: 'Sectionner une famille article...',
                        listeners: {
                            select: function (cmp) {
                                var value = cmp.getValue();
                                var OContainerInterval = Ext.getCmp('contenaire_intervalle');
                                if (value == "0") {
                                    OContainerInterval.show();
                                } else {
                                    OContainerInterval.hide();
                                    Ext.getCmp('str_BEGIN').reset();
                                    Ext.getCmp('str_END').reset();
                                }


                            }
                        }
                    }, {
                        xtype: 'combobox',
                        name: 'lg_ZONE_GEO_ID',
                        fieldLabel: 'Emplacement',
//                        margins: '0 0 0 10',
                        id: 'lg_ZONE_GEO_ID',
                        store: store_zonegeo,
                        hidden: true,
                        valueField: 'lg_ZONE_GEO_ID',
                        displayField: 'str_LIBELLEE',
                        pageSize: itemsPerPage,
                        typeAhead: true,
                        queryMode: 'remote',
                        flex: 1,
                        emptyText: 'Sectionner un emplacement...',
                        listeners: {
                            select: function (cmp) {
                                var value = cmp.getValue();
                                var OContainerInterval = Ext.getCmp('contenaire_intervalle');
                                if (value === "0") {
                                    OContainerInterval.show();
                                } else {
                                    OContainerInterval.hide();
                                    Ext.getCmp('str_BEGIN').reset();
                                    Ext.getCmp('str_END').reset();
                                }


                            }
                        }
                    }, {
                        xtype: 'combobox',
                        fieldLabel: 'Grossiste',
                        name: 'lg_GROSSISTE_ID',
//                        margin: '5 15 0 0',
                        id: 'lg_GROSSISTE_ID',
                        store: storerepartiteur,
                        valueField: 'lg_GROSSISTE_ID',
                        displayField: 'str_LIBELLE',
                        pageSize: itemsPerPage, //ajout la barre de pagination
                        hidden: true,
                        typeAhead: true,
                        queryMode: 'remote',
                        flex: 1,
                        emptyText: 'Sectionner un grossiste...',
                        listeners: {
                            select: function (cmp) {
                                var value = cmp.getValue();
                                var OContainerInterval = Ext.getCmp('contenaire_intervalle');
                                if (value === "0") {
                                    OContainerInterval.show();
                                } else {
                                    OContainerInterval.hide();
                                    Ext.getCmp('str_BEGIN').reset();
                                    Ext.getCmp('str_END').reset();
                                }


                            }
                        }

                    }, {
                        xtype: 'fieldcontainer',
                        fieldLabel: 'Intervalle',
                        layout: 'hbox',
                        id: 'contenaire_intervalle',
                        hidden: true,
                        combineErrors: true,
                        defaultType: 'textfield',
                        defaults: {
                            hideLabel: 'true'
                        },
                        items: [
                            {
                                fieldLabel: 'De',
                                width: 100,
                                emptyText: '',
                                name: 'str_BEGIN',
                                id: 'str_BEGIN',
                                margin: '0 10 0 0'

                            },
                            {
                                fieldLabel: 'A',
                                xtype: 'textfield',
                                width: 100,
//                                            hidden: true,
                                emptyText: '',
                                name: 'str_END',
                                id: 'str_END'/*,
                                 maxLength: 1,
                                 minLength: 1*/
                            }
                        ]
                    }
                ]
            },
            {
                xtype: 'fieldset',
                title: 'Detail Valorisation',
                defaultType: 'textfield',
                defaults: {
                    anchor: '100%'
                },
                items: [
                    TOTAL_VENTE,
                    TOTAL_ACHAT,
                    dt_CREATED
                ]
            }];

        this.callParent();
        var progress = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
        Ext.Ajax.request({
            method: 'GET',
            url: '../api/v1/produit/valorisation',
             timeout: 6000000,
            params: {
                dtStart: Ext.getCmp('dt_periode').getSubmitValue(),
                mode: 0,
                lgGROSSISTEID: null,
                lgFAMILLEARTICLEID: null,
                lgZONEGEOID: null,
                END: null,
                BEGIN: null
            },
            success: function (response) {
                progress.hide();
                var result = Ext.JSON.decode(response.responseText, true);
                var data = result.data;
                Ext.getCmp('TOTAL_VENTE').setValue(data.valueTwo);
                Ext.getCmp('TOTAL_ACHAT').setValue(data.value);
                Ext.getCmp('str_NAME_USER').setValue(result.user);
//                Ext.getCmp('ld_CREATED_BY').setValue(jsonResponse.CREATED_BY);
                Ext.getCmp('dt_CREATED').setValue(result.dtCREATED);

            },
            failure: function (error) {
                progress.hide();
            }
        });

    },
    buttons: [
        {
            text: 'Valoriser le stock',
            id: 'btn_valoriser',
            //disabled: true,
            handler: function () {
                var progress = Ext.MessageBox.wait('Veuillez patienter . . .', 'En cours de traitement!');
                Ext.Ajax.request({
                    method: 'GET',
                    url: '../api/v1/produit/valorisation',
                    params: {
                        dtStart: Ext.getCmp('dt_periode').getSubmitValue(),
                        mode: Ext.getCmp('str_TYPE_TRANSACTION').getValue(),
                        lgGROSSISTEID:  Ext.getCmp('lg_GROSSISTE_ID').getValue(),
                        lgFAMILLEARTICLEID: Ext.getCmp('lg_FAMILLEARTICLE_ID').getValue(),
                        lgZONEGEOID:  Ext.getCmp('lg_ZONE_GEO_ID').getValue(),
                        END:  Ext.getCmp('str_END').getValue(),
                        BEGIN: Ext.getCmp('str_BEGIN').getValue()
                    },
                    success: function (response) {
                        progress.hide();
                        var result = Ext.JSON.decode(response.responseText, true);
                        var data = result.data;
                        Ext.getCmp('TOTAL_VENTE').setValue(data.valueTwo);
                        Ext.getCmp('TOTAL_ACHAT').setValue(data.value);
                        Ext.getCmp('str_NAME_USER').setValue(result.user);
//                Ext.getCmp('ld_CREATED_BY').setValue(jsonResponse.CREATED_BY);
                        Ext.getCmp('dt_CREATED').setValue(result.dtCREATED);

                    },
                    failure: function (error) {
                        progress.hide();
                    }
                });

            }
        },
        {
            text: 'Imprimer',
            id: 'btn_print',
            disabled: true,
            formBind: true,
            handler: function () {
                var str_TYPE_TRANSACTION = Ext.getCmp('str_TYPE_TRANSACTION').getValue();
                var lg_GROSSISTE_ID = Ext.getCmp('lg_GROSSISTE_ID').getValue(),
                        lg_FAMILLEARTICLE_ID = Ext.getCmp('lg_FAMILLEARTICLE_ID').getValue(),
                        lg_ZONE_GEO_ID = Ext.getCmp('lg_ZONE_GEO_ID').getValue(),
                        str_END = Ext.getCmp('str_END').getValue(),
                        str_BEGIN = Ext.getCmp('str_BEGIN').getValue();
                if (str_END === null || str_END === undefined) {
                    str_END = "";
                }
                if (str_BEGIN === null || str_BEGIN === undefined) {
                    str_BEGIN = "";
                }
                if (lg_ZONE_GEO_ID === null || lg_ZONE_GEO_ID === undefined) {
                    lg_ZONE_GEO_ID = "";
                }
                if (lg_FAMILLEARTICLE_ID === null || lg_FAMILLEARTICLE_ID === undefined) {
                    lg_FAMILLEARTICLE_ID = "";
                }
                if (lg_GROSSISTE_ID === null || lg_GROSSISTE_ID === undefined) {
                    lg_GROSSISTE_ID = "";
                }

                var date = Ext.getCmp('dt_periode').getSubmitValue();

                var linkUrl = '../SockServlet?mode=VALORISATION&dtStart=' + date + '&action=' + str_TYPE_TRANSACTION + '&lgGROSSISTEID=' + lg_GROSSISTE_ID+ '&lgFAMILLEARTICLEID=' + lg_FAMILLEARTICLE_ID+ '&lgZONEGEOID=' + lg_ZONE_GEO_ID+ '&END=' + str_END+ '&BEGIN=' + str_BEGIN;
             
                window.open(linkUrl);

            }
        }



    ]

});
