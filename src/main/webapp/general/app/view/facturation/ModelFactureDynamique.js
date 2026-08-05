/* global Ext */

// Createur de modeles de facture : l'utilisateur choisit les colonnes a afficher,
// leurs libelles, leur ordre et le tri, puis rattache des tiers payants au modele.
// Les tiers payants rattaches sont factures par le nouveau moteur PDF ; les autres
// gardent les modeles Jasper historiques.
var url_rest_model_facture_dynamique = '../api/v1/model-facture-dynamique/';

Ext.define('testextjs.view.facturation.ModelFactureDynamique', {
    extend: 'Ext.panel.Panel',
    xtype: 'modelfacturedynamique',
    requires: [
        'Ext.ux.CheckColumn',
        'testextjs.store.Statistics.TiersPayans'
    ],
    frame: true,
    title: 'Cr&eacute;ateur de mod&egrave;les de facture',
    scrollable: true,
    width: '90%',
    minHeight: 500,
    cls: 'custompanel',
    layout: {
        type: 'fit'
    },
    initComponent: function () {
        var me = this;
        var store = Ext.create('Ext.data.Store', {
            fields: [
                {name: 'id', type: 'int'},
                {name: 'nom', type: 'string'},
                {name: 'description', type: 'string'},
                {name: 'modeTri', type: 'string'},
                {name: 'nbColonnes', type: 'int'},
                {name: 'nbTiersPayants', type: 'int'},
                {name: 'colonnes'}
            ],
            autoLoad: true,
            proxy: {
                type: 'ajax',
                url: url_rest_model_facture_dynamique + 'list',
                reader: {
                    type: 'json',
                    root: 'data',
                    totalProperty: 'total'
                }
            }
        });
        me.modelStore = store;

        Ext.applyIf(me, {
            items: [{
                    xtype: 'gridpanel',
                    store: store,
                    viewConfig: {
                        forceFit: true,
                        columnLines: true
                    },
                    tbar: [{
                            text: 'Cr&eacute;er un mod&egrave;le',
                            iconCls: 'addicon',
                            scope: me,
                            handler: function () {
                                me.openModelWindow(null);
                            }
                        }],
                    columns: [
                        {
                            header: 'Nom du mod&egrave;le',
                            dataIndex: 'nom',
                            flex: 1.5
                        },
                        {
                            header: 'Description',
                            dataIndex: 'description',
                            flex: 2
                        },
                        {
                            header: 'Tri',
                            dataIndex: 'modeTri',
                            flex: 1.2,
                            renderer: function (v) {
                                if (v === 'ALPHABETIQUE') {
                                    return 'Alphab&eacute;tique (nom client)';
                                }
                                if (v === 'DATE_BON') {
                                    return 'Date du bon / op&eacute;ration';
                                }
                                return 'Selon la fiche du tiers payant';
                            }
                        },
                        {
                            header: 'Colonnes',
                            dataIndex: 'nbColonnes',
                            flex: 0.6,
                            align: 'center'
                        },
                        {
                            header: 'Tiers payants rattach&eacute;s',
                            dataIndex: 'nbTiersPayants',
                            flex: 0.8,
                            align: 'center'
                        },
                        {
                            xtype: 'actioncolumn',
                            width: 30,
                            sortable: false,
                            menuDisabled: true,
                            items: [{
                                    icon: 'resources/images/icons/fam/page_white_edit.png',
                                    tooltip: 'Modifier le mod&egrave;le',
                                    handler: function (grid, rowIndex) {
                                        me.openModelWindow(grid.getStore().getAt(rowIndex));
                                    }
                                }]
                        },
                        {
                            xtype: 'actioncolumn',
                            width: 30,
                            sortable: false,
                            menuDisabled: true,
                            items: [{
                                    iconCls: 'detailclients',
                                    tooltip: 'Tiers payants rattach&eacute;s au mod&egrave;le',
                                    handler: function (grid, rowIndex) {
                                        me.openAssignWindow(grid.getStore().getAt(rowIndex));
                                    }
                                }]
                        },
                        {
                            xtype: 'actioncolumn',
                            width: 30,
                            sortable: false,
                            menuDisabled: true,
                            items: [{
                                    icon: 'resources/images/icons/fam/delete.png',
                                    tooltip: 'Supprimer le mod&egrave;le',
                                    handler: function (grid, rowIndex) {
                                        me.onDeleteModel(grid.getStore().getAt(rowIndex));
                                    }
                                }]
                        }
                    ]
                }]
        });
        this.callParent();
    },

    onDeleteModel: function (rec) {
        var me = this;
        Ext.MessageBox.confirm('Message',
                'Supprimer le mod&egrave;le <b>' + rec.get('nom') + '</b> ?<br/>'
                + 'Les tiers payants rattach&eacute;s reviendront aux mod&egrave;les de facture classiques.',
                function (btn) {
                    if (btn === 'yes') {
                        Ext.Ajax.request({
                            url: url_rest_model_facture_dynamique + 'delete',
                            method: 'POST',
                            params: {id: rec.get('id')},
                            success: function (response) {
                                var object = Ext.JSON.decode(response.responseText, false);
                                if (object.success === "0") {
                                    Ext.MessageBox.alert('Message d\'erreur', object.errors);
                                    return;
                                }
                                me.modelStore.reload();
                            },
                            failure: function (response) {
                                Ext.MessageBox.alert('Error Message', response.responseText);
                            }
                        });
                    }
                });
    },

    // ============================ Fenetre creation / modification =============================
    openModelWindow: function (rec) {
        var me = this;
        var colonnesStore = Ext.create('Ext.data.Store', {
            fields: [
                {name: 'champ', type: 'string'},
                {name: 'libelleDefaut', type: 'string'},
                {name: 'libelle', type: 'string'},
                {name: 'inclure', type: 'boolean'},
                {name: 'ordre', type: 'int'}
            ],
            proxy: {type: 'memory'}
        });

        // colonnes disponibles depuis le serveur, puis pre-cochage avec celles du modele edite
        Ext.Ajax.request({
            url: url_rest_model_facture_dynamique + 'colonnes',
            method: 'GET',
            success: function (response) {
                var object = Ext.JSON.decode(response.responseText, false);
                var existantes = {};
                if (rec) {
                    Ext.each(rec.get('colonnes') || [], function (c) {
                        existantes[c.champ] = c;
                    });
                }
                var rows = [];
                var prochainOrdre = 100;
                Ext.each(object.data, function (c) {
                    var choisie = existantes[c.champ];
                    rows.push({
                        champ: c.champ,
                        libelleDefaut: c.libelle,
                        libelle: choisie ? choisie.libelle : c.libelle,
                        inclure: !!choisie,
                        ordre: choisie ? choisie.ordre : prochainOrdre++
                    });
                });
                colonnesStore.loadData(rows);
                colonnesStore.sort('ordre', 'ASC');
            }
        });

        var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {clicksToEdit: 1});
        var win = Ext.create('Ext.window.Window', {
            autoShow: true,
            modal: true,
            title: rec ? 'Modifier le mod&egrave;le [' + rec.get('nom') + ']' : 'Cr&eacute;er un mod&egrave;le de facture',
            width: 760,
            height: 620,
            layout: 'fit',
            items: [{
                    xtype: 'form',
                    bodyPadding: 10,
                    scrollable: true,
                    items: [
                        {
                            xtype: 'textfield',
                            fieldLabel: 'Nom du mod&egrave;le',
                            itemId: 'mfdNom',
                            allowBlank: false,
                            anchor: '100%',
                            value: rec ? rec.get('nom') : ''
                        },
                        {
                            xtype: 'textfield',
                            fieldLabel: 'Description',
                            itemId: 'mfdDescription',
                            anchor: '100%',
                            value: rec ? rec.get('description') : ''
                        },
                        {
                            xtype: 'combobox',
                            fieldLabel: 'Tri des lignes',
                            itemId: 'mfdModeTri',
                            anchor: '100%',
                            store: Ext.create('Ext.data.ArrayStore', {
                                data: [
                                    ['TIERS_PAYANT', 'Selon la fiche du tiers payant'],
                                    ['ALPHABETIQUE', 'Alphab&eacute;tique (nom du client)'],
                                    ['DATE_BON', 'Date du bon / op&eacute;ration']
                                ],
                                fields: [{name: 'value', type: 'string'}, {name: 'libelle', type: 'string'}]
                            }),
                            valueField: 'value',
                            displayField: 'libelle',
                            editable: false,
                            queryMode: 'local',
                            value: rec ? rec.get('modeTri') : 'TIERS_PAYANT'
                        },
                        {
                            xtype: 'gridpanel',
                            title: 'Colonnes de la facture (cochez, renommez et ordonnez)',
                            itemId: 'mfdColonnes',
                            height: 380,
                            margin: '10 0 0 0',
                            store: colonnesStore,
                            plugins: [cellEditing],
                            columns: [
                                {
                                    xtype: 'checkcolumn',
                                    header: 'Afficher',
                                    dataIndex: 'inclure',
                                    width: 70
                                },
                                {
                                    header: 'Information',
                                    dataIndex: 'libelleDefaut',
                                    flex: 1.2
                                },
                                {
                                    header: 'Libell&eacute; sur la facture',
                                    dataIndex: 'libelle',
                                    flex: 1.2,
                                    editor: {xtype: 'textfield', allowBlank: false}
                                },
                                {
                                    header: 'Ordre',
                                    dataIndex: 'ordre',
                                    width: 70,
                                    align: 'center',
                                    editor: {xtype: 'numberfield', minValue: 0, allowDecimals: false}
                                }
                            ]
                        }
                    ]
                }],
            buttons: [
                {
                    text: 'Enregistrer',
                    handler: function (btn) {
                        var formulaire = btn.up('window').down('form');
                        if (!formulaire.isValid()) {
                            return;
                        }
                        var colonnes = [];
                        colonnesStore.each(function (r) {
                            if (r.get('inclure')) {
                                colonnes.push({
                                    champ: r.get('champ'),
                                    libelle: r.get('libelle') || r.get('libelleDefaut'),
                                    ordre: r.get('ordre')
                                });
                            }
                        });
                        if (colonnes.length === 0) {
                            Ext.MessageBox.alert('Avertissement',
                                    'Choisissez au moins une colonne &agrave; afficher sur la facture');
                            return;
                        }
                        colonnes.sort(function (a, b) {
                            return a.ordre - b.ordre;
                        });
                        Ext.Ajax.request({
                            url: url_rest_model_facture_dynamique + 'save',
                            method: 'POST',
                            params: {
                                id: rec ? rec.get('id') : '',
                                nom: formulaire.down('#mfdNom').getValue(),
                                description: formulaire.down('#mfdDescription').getValue(),
                                modeTri: formulaire.down('#mfdModeTri').getValue(),
                                colonnes: Ext.encode(colonnes)
                            },
                            success: function (response) {
                                var object = Ext.JSON.decode(response.responseText, false);
                                if (object.success === "0") {
                                    Ext.MessageBox.alert('Message d\'erreur', object.errors);
                                    return;
                                }
                                win.close();
                                me.modelStore.reload();
                            },
                            failure: function (response) {
                                Ext.MessageBox.alert('Error Message', response.responseText);
                            }
                        });
                    }
                },
                {
                    text: 'Annuler',
                    handler: function () {
                        win.close();
                    }
                }
            ]
        });
    },

    // ============================ Fenetre d'affectation des tiers payants ====================
    // Deux grilles : en haut la recherche (affectation selective ou de masse aux resultats
    // coches), en bas les tiers payants deja rattaches au modele.
    openAssignWindow: function (rec) {
        var me = this;
        var modelId = rec.get('id');

        var assignedStore = Ext.create('Ext.data.Store', {
            fields: [
                {name: 'lg_TIERS_PAYANT_ID', type: 'string'},
                {name: 'str_FULLNAME', type: 'string'}
            ],
            autoLoad: true,
            proxy: {
                type: 'ajax',
                url: url_rest_model_facture_dynamique + 'tiers-payants?modelId=' + modelId,
                reader: {type: 'json', root: 'data', totalProperty: 'total'}
            }
        });

        var rechercheStore = Ext.create('Ext.data.Store', {
            fields: [
                {name: 'lg_TIERS_PAYANT_ID', type: 'string'},
                {name: 'str_FULLNAME', type: 'string'},
                {name: 'MODELE_ACTUEL', type: 'string'},
                {name: 'EST_DYNAMIQUE', type: 'boolean'},
                {name: 'isChecked', type: 'boolean'}
            ],
            autoLoad: false,
            proxy: {
                type: 'ajax',
                url: url_rest_model_facture_dynamique + 'rechercher-tiers-payants',
                reader: {type: 'json', root: 'data', totalProperty: 'total'}
            }
        });

        var lancerRecherche = function (grille) {
            var champ = grille.up('window').down('#mfdRecherche');
            rechercheStore.load({params: {query: champ ? (champ.getValue() || '') : ''}});
        };

        var idsCoches = function () {
            var ids = [];
            rechercheStore.each(function (r) {
                if (r.get('isChecked')) {
                    ids.push(r.get('lg_TIERS_PAYANT_ID'));
                }
            });
            return ids;
        };

        var affecter = function (ids, idModele, libelleAction) {
            if (!ids.length) {
                Ext.MessageBox.alert('Information',
                        'Cochez au moins un tiers payant dans les r\u00e9sultats de recherche.');
                return;
            }
            Ext.MessageBox.confirm('Confirmation',
                    libelleAction + ' pour ' + ids.length + ' tiers payant(s) ?',
                    function (btn) {
                        if (btn !== 'yes') {
                            return;
                        }
                        Ext.Ajax.request({
                            url: url_rest_model_facture_dynamique + 'assigner-masse',
                            method: 'POST',
                            params: {modelId: idModele, tiersPayants: Ext.encode(ids)},
                            success: function (response) {
                                var object = Ext.JSON.decode(response.responseText, false);
                                if (object.success === "0") {
                                    Ext.MessageBox.alert('Message d\'erreur', object.errors);
                                    return;
                                }
                                Ext.MessageBox.alert('Information', object.errors);
                                rechercheStore.each(function (r) {
                                    r.set('isChecked', false);
                                });
                                rechercheStore.commitChanges();
                                assignedStore.reload();
                                me.modelStore.reload();
                            },
                            failure: function (response) {
                                Ext.MessageBox.alert('Error Message', response.responseText);
                            }
                        });
                    });
        };

        var win = Ext.create('Ext.window.Window', {
            autoShow: true,
            modal: true,
            title: 'Affecter le mod&egrave;le [' + rec.get('nom') + '] &agrave; des tiers payants',
            width: 820,
            height: 620,
            layout: {type: 'vbox', align: 'stretch'},
            items: [
                {
                    xtype: 'gridpanel',
                    title: '1. Rechercher des tiers payants et cocher ceux &agrave; affecter',
                    flex: 1,
                    store: rechercheStore,
                    tbar: [
                        {
                            xtype: 'textfield',
                            itemId: 'mfdRecherche',
                            flex: 1,
                            emptyText: 'Nom du tiers payant (laisser vide pour tout lister)...',
                            enableKeyEvents: true,
                            listeners: {
                                specialKey: function (field, e) {
                                    if (e.getKey() === e.ENTER) {
                                        rechercheStore.load({params: {query: field.getValue() || ''}});
                                    }
                                }
                            }
                        },
                        {
                            text: 'Rechercher',
                            iconCls: 'searchicon',
                            handler: function (btn) {
                                lancerRecherche(btn.up('gridpanel'));
                            }
                        },
                        '-',
                        {
                            text: 'Tout cocher',
                            handler: function () {
                                rechercheStore.each(function (r) {
                                    r.set('isChecked', true);
                                });
                                rechercheStore.commitChanges();
                            }
                        },
                        {
                            text: 'Tout d&eacute;cocher',
                            handler: function () {
                                rechercheStore.each(function (r) {
                                    r.set('isChecked', false);
                                });
                                rechercheStore.commitChanges();
                            }
                        },
                        '->',
                        {
                            text: 'Affecter ce mod&egrave;le',
                            iconCls: 'addicon',
                            handler: function () {
                                affecter(idsCoches(), modelId, 'Affecter le mod\u00e8le "' + rec.get('nom') + '"');
                            }
                        },
                        {
                            text: 'Retirer le mod&egrave;le',
                            iconCls: 'cancelicon',
                            tooltip: 'Remettre le mod&egrave;le de facture par d&eacute;faut',
                            handler: function () {
                                affecter(idsCoches(), 0, 'Remettre le mod\u00e8le par d\u00e9faut');
                            }
                        }
                    ],
                    columns: [
                        {
                            xtype: 'checkcolumn',
                            dataIndex: 'isChecked',
                            width: 40
                        },
                        {
                            header: 'Tiers payant',
                            dataIndex: 'str_FULLNAME',
                            flex: 1.6
                        },
                        {
                            header: 'Mod&egrave;le actuellement affect&eacute;',
                            dataIndex: 'MODELE_ACTUEL',
                            flex: 1.4,
                            renderer: function (value, meta, r) {
                                if (r.get('EST_DYNAMIQUE')) {
                                    return '<span style="color:green;">' + (value || '') + '</span>';
                                }
                                return value || '';
                            }
                        }
                    ]
                },
                {
                    xtype: 'gridpanel',
                    title: '2. Tiers payants actuellement factur&eacute;s avec ce mod&egrave;le',
                    flex: 1,
                    store: assignedStore,
                    columns: [
                        {
                            header: 'Tiers payant',
                            dataIndex: 'str_FULLNAME',
                            flex: 1
                        },
                        {
                            xtype: 'actioncolumn',
                            width: 30,
                            sortable: false,
                            menuDisabled: true,
                            items: [{
                                    icon: 'resources/images/icons/fam/delete.png',
                                    tooltip: 'D&eacute;tacher du mod&egrave;le (retour au mod&egrave;le par d&eacute;faut)',
                                    handler: function (grid, rowIndex) {
                                        var tpRec = grid.getStore().getAt(rowIndex);
                                        Ext.Ajax.request({
                                            url: url_rest_model_facture_dynamique + 'assigner',
                                            method: 'POST',
                                            params: {
                                                lg_TIERS_PAYANT_ID: tpRec.get('lg_TIERS_PAYANT_ID'),
                                                modelId: ''
                                            },
                                            success: function (response) {
                                                var object = Ext.JSON.decode(response.responseText, false);
                                                if (object.success === "0") {
                                                    Ext.MessageBox.alert('Message d\'erreur', object.errors);
                                                    return;
                                                }
                                                assignedStore.reload();
                                                me.modelStore.reload();
                                            },
                                            failure: function (response) {
                                                Ext.MessageBox.alert('Error Message', response.responseText);
                                            }
                                        });
                                    }
                                }]
                        }
                    ]
                }
            ],
            buttons: [{
                    text: 'Fermer',
                    handler: function () {
                        win.close();
                    }
                }]
        });
    }
});
