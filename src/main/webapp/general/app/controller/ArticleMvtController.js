Ext.define('testextjs.controller.ArticleMvtController', {
    extend: 'Ext.app.Controller',

    views: [
        'testextjs.view.stat.ArticleMvtGrid'
    ],

    refs: [
        { ref: 'grid', selector: 'articlemvtgrid' }
    ],

    init: function () {
        var me = this;

        me.control({
            'articlemvtgrid': {
                afterrender: me.onGridAfterRender     
            },
            'articlemvtgrid button[itemId=btnSearch]': {
                click: me.onSearch
            },
            'articlemvtgrid button[itemId=btnReset]':  {
                click: me.onReset
            },
            'articlemvtgrid textfield[itemId=queryField]': {
                keyup: me.onQueryKeyUp
            },
            'articlemvtgrid button[itemId=btnCreateInventaire]': {
                click: me.onCreateInventaireFromSelection
            }
        });
    },

    onGridAfterRender: function (grid) {
        var d1 = grid.down('datefield[itemId=dtStart]');
        var d2 = grid.down('datefield[itemId=dtEnd]');
        var store = grid.getStore();
        var today = new Date();

        if (d1) { d1.setValue(today); }
        if (d2) { d2.setValue(today); }

        var dtStart = d1 && d1.getSubmitValue ? d1.getSubmitValue() : '';
        var dtEnd   = d2 && d2.getSubmitValue ? d2.getSubmitValue() : '';

        store.getProxy().extraParams = store.getProxy().extraParams || {};
        store.getProxy().extraParams.dtStart = dtStart;
        store.getProxy().extraParams.dtEnd   = dtEnd;
        store.getProxy().extraParams.query   = '';

        store.loadPage(1);
    },

    onQueryKeyUp: function (field, e) {
        if (e.getKey && e.getKey() === e.ENTER) {
            this.onSearch(field.up('articlemvtgrid'));
        }
    },

    onReset: function (btn) {
        var grid = btn.up('articlemvtgrid');
        if (!grid) { return; }

        var q  = grid.down('textfield[itemId=queryField]');
        var d1 = grid.down('datefield[itemId=dtStart]');
        var d2 = grid.down('datefield[itemId=dtEnd]');

        var today = new Date();

        if (q)  { q.setValue(''); }
        if (d1) { d1.setValue(today); }
        if (d2) { d2.setValue(today); }

        var store = grid.getStore();
        var dtStart = d1 && d1.getSubmitValue ? d1.getSubmitValue() : '';
        var dtEnd   = d2 && d2.getSubmitValue ? d2.getSubmitValue() : '';

        store.getProxy().extraParams.query   = '';
        store.getProxy().extraParams.dtStart = dtStart;
        store.getProxy().extraParams.dtEnd   = dtEnd;

        store.loadPage(1);
        grid.getSelectionModel().deselectAll();
    },

    onSearch: function (btnOrGrid) {
        var grid = (btnOrGrid && btnOrGrid.isXType && btnOrGrid.isXType('gridpanel'))
            ? btnOrGrid
            : btnOrGrid.up('articlemvtgrid');

        if (!grid) { return; }

        var q  = grid.down('textfield[itemId=queryField]');
        var d1 = grid.down('datefield[itemId=dtStart]');
        var d2 = grid.down('datefield[itemId=dtEnd]');

        if (!q || !d1 || !d2) {
            Ext.Msg.alert('Erreur', 'Champs de recherche introuvables dans la barre d’outils.');
            return;
        }

        var query   = (q.getValue() || '').trim();
        var vStart  = d1.getValue();
        var vEnd    = d2.getValue();

        if (vStart && !vEnd) {
            Ext.Msg.alert('Information', 'Veuillez renseigner la date de fin.', function () {
                d2.focus();
            });
            return;
        }

        if (!vStart && vEnd) {
            Ext.Msg.alert('Information', 'Veuillez renseigner la date de début.', function () {
                d1.focus();
            });
            return;
        }

        if (vStart && vEnd && vStart > vEnd) {
            Ext.Msg.alert('Information', 'La date de début ne peut pas être supérieure à la date de fin.', function () {
                d1.focus();
            });
            return;
        }

        var dtStart = d1.getSubmitValue ? d1.getSubmitValue() : '';
        var dtEnd   = d2.getSubmitValue ? d2.getSubmitValue() : '';

        var store = grid.getStore();
        store.getProxy().extraParams.query   = query;
        store.getProxy().extraParams.dtStart = dtStart || '';
        store.getProxy().extraParams.dtEnd   = dtEnd || '';
        store.loadPage(1);

        grid.getSelectionModel().deselectAll();
    },

    onCreateInventaireFromSelection: function (btn) {
        var grid = btn.up('articlemvtgrid');
        var selections = grid.getSelectionModel().getSelection();

        if (!selections || selections.length === 0) {
            Ext.Msg.alert('Information', 'Veuillez sélectionner au moins un article.');
            return;
        }

        var ids = Ext.Array.pluck(selections, 'data').map(function (d) {
            return d.lgFamilleId;
        });

        var d1 = grid.down('datefield[itemId=dtStart]');
        var d2 = grid.down('datefield[itemId=dtEnd]');

        var dtStart = d1 && d1.getSubmitValue ? d1.getSubmitValue() : '';
        var dtEnd   = d2 && d2.getSubmitValue ? d2.getSubmitValue() : '';

        Ext.Msg.confirm(
            'Confirmation',
            'Créer un inventaire à partir des ' + ids.length + ' article(s) sélectionné(s) ?',
            function (choice) {
                if (choice !== 'yes') { return; }

                Ext.Ajax.request({
                    url: '../api/v1/articlemvt/inventaire',
                    method: 'GET',
                    params: {
                        ids: ids.join(','),
                        dtStart: dtStart || '',
                        dtEnd: dtEnd || ''
                    },
                    success: function (response) {
                        var result = Ext.decode(response.responseText, true) || {};
                        if (result.success) {
                            Ext.Msg.alert('Succès', result.message || 'Inventaire créé avec succès.');
                        } else {
                            Ext.Msg.alert('Information', result.message || 'Opération non réalisée.');
                        }
                        grid.getStore().reload();
                        grid.getSelectionModel().deselectAll();
                    },
                    failure: function () {
                        Ext.Msg.alert('Erreur', 'Impossible de créer l’inventaire. Vérifiez les logs serveur.');
                    }
                });
            }
        );
    }
});
