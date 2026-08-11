/* global Ext */

Ext.define('testextjs.store.RecpaOrganisme', {
    extend: 'Ext.data.Store',
    requires: [
        'testextjs.model.RecapOrganisme'
    ],
    model: 'testextjs.model.RecapOrganisme',
    autoLoad: true,
   // groupField: 'FULNAME',
    pageSize:20,  
    proxy: {
        type: 'ajax',
        // endpoint reloge sous reglement-facture (la ressource autonome n'etait pas enregistree sur certains serveurs -> 404)
        url: '../api/v1/reglement-facture/recap-organisme/list',
        reader: {
            type: 'json',
            root: 'data',
            totalProperty: 'total'
        },
        timeout: 240000,
        listeners: {
            /*
             * Echec du calcul : le dire, au lieu d'afficher un tableau vide.
             *
             * Le service repondait "succes" avec zero ligne quand la procedure stockee echouait
             * (cas constate : une valeur trop longue pour la base). L'utilisateur en concluait
             * qu'aucun reglement n'existait sur la periode, et le Centre de Support ne recevait
             * rien puisqu'il n'enregistre que les appels en erreur. Le service repond desormais en
             * erreur, avec la cause en clair dans "errors" : on l'affiche telle quelle.
             */
            exception: function (proxy, response) {
                var message = 'Le récapitulatif n\'a pas pu être calculé.';
                try {
                    var objet = Ext.JSON.decode(response.responseText, true);
                    if (objet && objet.errors) {
                        message = objet.errors;
                    }
                } catch (ignore) {
                    // reponse illisible : on garde le message generique
                }
                Ext.MessageBox.show({
                    title: 'Récapitulatif par compte organisme',
                    msg: message,
                    width: 460,
                    buttons: Ext.MessageBox.OK,
                    icon: Ext.MessageBox.ERROR
                });
            }
        }
    }
});