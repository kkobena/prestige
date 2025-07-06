/**
 * Contrôleur pour gérer les actions liées à l'analyse d'inventaire.
 */
Ext.define('Prestige.controller.AnalyseInventaire', {
    extend: 'Ext.app.Controller',
    views: ['AnalyseInventaire'],
    stores: ['AnalyseInvs'],
    models: ['AnalyseInv'],

    init: function() {
        this.control({
            // Sélecteur pour le bouton 'Analyse' (à adapter à votre vue)
            'inventairemanager button#analyseBtn': {
                click: this.onAnalyseClick
            }
        });
    },

    onAnalyseClick: function(button) {
        var grid = button.up('gridpanel'); // Remonte au grid parent
        var selection = grid.getSelectionModel().getSelection();

        if (selection.length > 0) {
            var selectedInventaire = selection[0];
            var inventaireId = selectedInventaire.get('lg_INVENTAIRE_ID'); // Assurez-vous que le champ existe dans votre modèle
            
            // Crée un nouveau store pour cette analyse
            var store = Ext.create('Prestige.store.AnalyseInvs');
            
            // Crée la fenêtre d'analyse en lui passant le store
            var view = Ext.create('Prestige.view.AnalyseInventaire', {
                store: store
            });

            // Charge le store avec le bon ID d'inventaire
            store.load({
                params: {
                    inventaireId: inventaireId
                },
                callback: function(records, operation, success) {
                    if (!success) {
                        Ext.Msg.alert('Erreur', 'Impossible de charger les données d\'analyse.');
                    }
                }
            });

        } else {
            Ext.Msg.alert('Avertissement', 'Veuillez sélectionner un inventaire à analyser.');
        }
    }
});
