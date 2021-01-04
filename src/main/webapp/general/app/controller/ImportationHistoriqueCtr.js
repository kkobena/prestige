
/* global Ext */

Ext.define('testextjs.controller.ImportationHistoriqueCtr', {
    extend: 'Ext.app.Controller',

    views: [
        'testextjs.view.depot.ImportationHistorique',
        'testextjs.view.depot.ImportForm'
    ],

    refs: [

        {
            ref: 'importfromjson',
            selector: 'importfromjson'
        },
        {
            ref: 'importform',
            selector: 'importform'
        },
        {
            ref: 'importForm',
            selector: 'importform form'
        }
        , {
            ref: 'dtStart',
            selector: 'importfromjson #dtStart'
        }, {
            ref: 'dtEnd',
            selector: 'importfromjson #dtEnd'
        },
        {
            ref: 'montantAchat',
            selector: 'importfromjson #montantAchat'
        },
        {
            ref: 'nbreLigne',
            selector: 'importfromjson #nbreLigne'
        },
        {
            ref: 'montantVente',
            selector: 'importfromjson #montantVente'
        },

        {
            ref: 'historiqueGrid',
            selector: 'importfromjson gridpanel'
        },
        {
            ref: 'pagingtoolbar',
            selector: 'importfromjson gridpanel pagingtoolbar'
        }

    ],
    init: function () {
        this.control({
            'importfromjson gridpanel pagingtoolbar': {
                beforechange: this.doBeforechange
            },
            'importfromjson #rechercher': {
                click: this.doSearch
            },

            'importfromjson gridpanel': {
                viewready: this.doInitStore
            },
            'importform #btnsave': {
                click: this.onbtnsave
            },
            'importform #btnCancel': {
                click: this.onCancel
            },
            'importfromjson #import': {
                click: this.onImport
            }

        });
    },

    doSearch: function () {
        var me = this;
        let store=me.getHistoriqueGrid().getStore();
                store.load({
                    params: {
                        dtEnd: me.getDtEnd().getSubmitValue(),
                        dtStart: me.getDtStart().getSubmitValue()
                    },
                    callback: function (records, operation, successful) {
                        console.log(records);
                        let montantAchat = 0, montantVente = 0, nbreLigne = 0;
                        Ext.each(records, function (item) {
                            montantAchat += item.get('montantAchat');
                            nbreLigne += item.get('nbreLigne');
                            montantVente += item.get('montantVente');
                               console.warn(item);
                        });
                        me.getMontantAchat().setValue(montantAchat);
                        me.getMontantVente().setValue(montantVente);
                        me.getNbreLigne().setValue(nbreLigne);

                    }

                });
    },
    doInitStore: function () {
        var me = this;
        me.doSearch();
    },
    doBeforechange: function (page, currentPage) {
        var me = this;
        var myProxy = me.getHistoriqueGrid().getStore().getProxy();
        myProxy.params = {
            dtEnd: null,
            dtStart: null
        };
        myProxy.setExtraParam('dtEnd', me.getDtEnd().getSubmitValue());
        myProxy.setExtraParam('dtStart', me.getDtStart().getSubmitValue());
    },
  
    onCancel: function () {
        let me = this;
        let form = me.getImportform();
        form.destroy();
    },
    onbtnsave: function () {
        let me = this;
        let form = me.getImportForm();
        form.submit({
            url: '../ImportationVenteCtr',
            waitMsg: 'Veuillez patienter le temps du telechargemetnt du fichier...',
            timeout: 360000000,
            success: function (formulaire, action) {
                var result = Ext.JSON.decode(action.response.responseText, true);
                Ext.MessageBox.show({
                    title: 'Message d\'erreur',
                    width: 320,
                    msg: result.count + ' vente(s) prises en compte sur ' + result.ligne,
                    buttons: Ext.MessageBox.OK,
                    icon: Ext.MessageBox.INFO

                });
                me.onCancel();
                me.doSearch();
            },
            failure: function (formulaire, action) {
                Ext.MessageBox.alert('Erreur', 'Erreur  ' + action.result.errors);
            }
        });
    },

    onImport: function () {
        Ext.create('testextjs.view.depot.Import');
    }

}
);
