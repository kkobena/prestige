/**
 * Modèle de données pour une ligne de l'analyse d'inventaire.
 * Définit les champs et les calculs pour chaque produit.
 */
Ext.define('Prestige.model.AnalyseInv', {
    extend: 'Ext.data.Model',
    fields: [
        { name: 'codeCip', type: 'string' },
        { name: 'nom', type: 'string' },
        { name: 'prixAchat', type: 'int' },
        { name: 'prixVente', type: 'int' },
        { name: 'emplacement', type: 'string' },
        { name: 'invName', type: 'string' },
        { name: 'qteSaisie', type: 'int' },
        { name: 'qteInitiale', type: 'int' },

        // --- Champs calculés pour l'analyse ---

        // Valeur d'achat du stock machine (initial)
        {
            name: 'valeurAchatMachine',
            type: 'float',
            convert: function(v, record) {
                return record.get('qteInitiale') * record.get('prixAchat');
            }
        },
        // Valeur de vente du stock machine (initial)
        {
            name: 'valeurVenteMachine',
            type: 'float',
            convert: function(v, record) {
                return record.get('qteInitiale') * record.get('prixVente');
            }
        },
        // Valeur d'achat du stock rayon (saisie)
        {
            name: 'valeurAchatRayon',
            type: 'float',
            convert: function(v, record) {
                return record.get('qteSaisie') * record.get('prixAchat');
            }
        },
        // Valeur de vente du stock rayon (saisie)
        {
            name: 'valeurVenteRayon',
            type: 'float',
            convert: function(v, record) {
                return record.get('qteSaisie') * record.get('prixVente');
            }
        },
        // Écart sur la quantité
        {
            name: 'ecartQuantite',
            type: 'int',
            convert: function(v, record) {
                return record.get('qteSaisie') - record.get('qteInitiale');
            }
        },
        // Écart sur la valeur d'achat
        {
            name: 'ecartValeurAchat',
            type: 'float',
            convert: function(v, record) {
                return record.get('valeurAchatRayon') - record.get('valeurAchatMachine');
            }
        }
    ]
});
