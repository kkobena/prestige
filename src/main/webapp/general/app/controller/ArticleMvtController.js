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
            'articlemvtgrid button[itemId=btnSearch]': { click: me.onSearch },
            'articlemvtgrid button[itemId=btnReset]':  { click: me.onReset },
            'articlemvtgrid textfield[itemId=queryField]': { keyup: me.onQueryKeyUp },
            'articlemvtgrid button[itemId=btnCreateInventaire]': { click: me.onCreateInventaireFromSelection },
            'articlemvtgrid button[itemId=btnExportExcel]': { click: me.onExportExcel }
        });
    },

    onExportExcel: function (btn) {
        var grid = btn.up('articlemvtgrid');

        var q = grid.down('textfield[itemId=queryField]');
        var d1 = grid.down('datefield[itemId=dtStart]');
        var d2 = grid.down('datefield[itemId=dtEnd]');

        var query = (q && q.getValue()) ? q.getValue().trim() : '';
        var dtStart = d1 && d1.getSubmitValue ? d1.getSubmitValue() : '';
        var dtEnd = d2 && d2.getSubmitValue ? d2.getSubmitValue() : '';

        var params = Ext.Object.toQueryString({
            dtStart: dtStart || '',
            dtEnd: dtEnd || '',
            query: query || ''
        });

        window.location = '../api/v1/articlemvt/export?' + params;
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
                url: '../api/v1/articlemvt/inventaire', // 
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
,

    onQueryKeyUp: function (field, e) {
        if (e.getKey && e.getKey() === e.ENTER) {
            this.onSearch(field.up('articlemvtgrid'));
        }
    },

    onReset: function (btn) {
        var grid = btn.up('articlemvtgrid');
        if (!grid) { return; }

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

    resetView: function () {
    var grid = Ext.ComponentQuery.query('articlemvtgrid')[0];
    if (grid && grid.resetScreen) {
        grid.resetScreen();
    }
    }
});
