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

        // ✅ IMPORTANT: utiliser itemId dans les selectors (pas #...)
        me.control({
            'articlemvtgrid button[itemId=btnSearch]': { click: me.onSearch },
            'articlemvtgrid button[itemId=btnReset]':  { click: me.onReset },
            'articlemvtgrid textfield[itemId=queryField]': { keyup: me.onQueryKeyUp },
            'articlemvtgrid button[itemId=btnCreateInventaire]': { click: me.onCreateInventaireFromSelection }
        });
    },

    onQueryKeyUp: function (field, e) {
        if (e.getKey && e.getKey() === e.ENTER) {
            this.onSearch(field.up('articlemvtgrid'));
        }
    },

    onReset: function (btn) {
        var grid = btn.up('articlemvtgrid');
        if (!grid) { return; }

        // ✅ itemId selectors
        var q = grid.down('textfield[itemId=queryField]');
        var d1 = grid.down('datefield[itemId=dtStart]');
        var d2 = grid.down('datefield[itemId=dtEnd]');

        if (q)  { q.setValue(''); }
        if (d1) { d1.setValue(null); }
        if (d2) { d2.setValue(null); }

        var store = grid.getStore();
        store.getProxy().extraParams.query = '';
        store.getProxy().extraParams.dtStart = '';
        store.getProxy().extraParams.dtEnd = '';
        store.loadPage(1);

        grid.getSelectionModel().deselectAll();
    },

    onSearch: function (btnOrGrid) {
        var grid = (btnOrGrid && btnOrGrid.isXType && btnOrGrid.isXType('gridpanel'))
            ? btnOrGrid
            : btnOrGrid.up('articlemvtgrid');

        if (!grid) { return; }

        // ✅ itemId selectors
        var q = grid.down('textfield[itemId=queryField]');
        var d1 = grid.down('datefield[itemId=dtStart]');
        var d2 = grid.down('datefield[itemId=dtEnd]');

        if (!q || !d1 || !d2) {
            Ext.Msg.alert('Erreur', 'Champs de recherche introuvables dans la barre d’outils.');
            return;
        }

        var query = (q.getValue() || '').trim();
        var dtStart = d1.getSubmitValue ? d1.getSubmitValue() : '';
        var dtEnd = d2.getSubmitValue ? d2.getSubmitValue() : '';

        var store = grid.getStore();
        store.getProxy().extraParams.query = query;
        store.getProxy().extraParams.dtStart = dtStart || '';
        store.getProxy().extraParams.dtEnd = dtEnd || '';
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

        Ext.Msg.confirm(
            'Confirmation',
            'Créer un inventaire à partir des ' + ids.length + ' article(s) sélectionné(s) ?',
            function (choice) {
                if (choice !== 'yes') { return; }

                var url = '../api/v1/inventaires/ws/createFromArticles'; // à remplacer

                Ext.Ajax.request({
                    url: url,
                    method: 'GET',
                    params: { ids: ids.join(',') },
                    success: function () {
                        Ext.Msg.alert('Succès', 'Inventaire créé avec succès.');
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
