/**
 * Store pour charger et gérer les données de l'analyse d'inventaire.
 */
Ext.define('Prestige.store.AnalyseInvs', {
    extend: 'Ext.data.Store',
    model: 'Prestige.model.AnalyseInv',
    
    // Groupement par emplacement pour l'affichage des écarts
    groupField: 'emplacement',

    proxy: {
        type: 'ajax',
        // L'URL de base de l'API. L'ID de l'inventaire sera ajouté en paramètre.
        url: '/laborex/api/v1/analyse-inventaire',
        reader: {
            type: 'json',
            root: 'data', // Assurez-vous que votre API renvoie { "success": true, "data": [...] }
            totalProperty: 'total'
        }
    },
    autoLoad: false // On ne charge pas les données automatiquement
});
