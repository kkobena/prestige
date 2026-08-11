/*
 * Correctifs d'affichage : empecher qu'ExtJS coupe la fin d'un texte.
 *
 * Deux composants sont concernes, pour la meme raison de fond : ExtJS fige une taille
 * calculee a partir d'une mesure du texte, et le conteneur est en overflow:hidden. Quand la
 * mesure est trop courte de quelques pixels (police du poste, zoom du navigateur), la fin du
 * texte existe dans la page mais reste invisible.
 *
 * 1) les info-bulles (QuickTip) ;
 * 2) les boites de message (Ext.MessageBox).
 *
 * =====================================================================================
 * 1) INFO-BULLES
 *
 * Symptome constate : en survolant un bouton de la liste des facturations, on lit
 * "Avoir deja" au lieu de "Avoir deja certifie".
 *
 * Cause : ExtJS mesure le texte de l'info-bulle, arrondit la largeur obtenue au
 * pixel INFERIEUR, puis fige cette largeur. Quand le texte reel fait une fraction
 * de pixel de plus (ce qui depend de la police du poste et du zoom du navigateur),
 * il ne tient plus et passe a la ligne. Or la hauteur de l'info-bulle a, elle, ete
 * calculee pour une seule ligne, et le corps de l'info-bulle est en overflow:hidden
 * dans le theme : la deuxieme ligne existe mais reste invisible. D'ou le texte
 * tronque, sans aucun indice pour l'utilisateur.
 *
 * Correctif : juste apres l'affichage, si le contenu deborde de son cadre :
 *   1. on essaie d'elargir l'info-bulle jusqu'a la largeur naturelle du texte,
 *      sans jamais depasser la largeur maximale d'ExtJS ni une largeur imposee
 *      par l'appelant (data-qwidth, ou config width d'un tip enregistre) ;
 *   2. s'il reste des lignes masquees (texte reellement long, sur plusieurs
 *      lignes), on augmente la hauteur de ce qui manque ;
 *   3. on relance la contrainte d'ExtJS pour que l'info-bulle agrandie reste
 *      entierement dans la fenetre (les boutons concernes sont a droite).
 * Le redimensionnement passe par les methodes d'ExtJS : la mise en page interne
 * (entete, ancre) est recalculee normalement.
 *
 * L'ajustement est fait apres showAt() et non sur l'evenement "show" : a ce
 * moment-la ExtJS n'a pas encore applique la mise en page (le corps mesure
 * encore 34x6 pixels), toute mesure y serait fausse.
 *
 * =====================================================================================
 * 2) BOITES DE MESSAGE
 *
 * Symptome constate : apres l'emission d'un avoir FNE, la boite affiche
 * "Avoir FNE emis. Reference : ... / La facture a ete annulee : les ventes sont a nouveau
 * fact" - la derniere ligne est coupee en deux.
 *
 * Cause : Ext.MessageBox calcule la hauteur de la fenetre a partir d'une mesure du texte.
 * Le conteneur interne de la mise en page (.x-box-inner) recoit cette hauteur en dur et il
 * est en overflow:hidden. Rendre le corps de la fenetre defilable ne suffit donc pas : le
 * corps, lui, ne deborde jamais, c'est le conteneur interne qui rogne.
 *
 * Correctif : apres l'affichage, si un element de la boite masque une partie de son
 * contenu, la fenetre est agrandie d'autant puis recentree. ExtJS efface la hauteur a
 * chaque nouvel appel (delete me.height dans reconfigure) : la hauteur forcee ne reste
 * jamais collee a la boite suivante.
 */
Ext.onReady(function () {
    'use strict';

    corrigerInfobulles();
    corrigerBoitesDeMessage();

    // ---------------------------------------------------------------------------------
    // 1) info-bulles
    // ---------------------------------------------------------------------------------
    function corrigerInfobulles() {
        if (!Ext.tip || !Ext.tip.QuickTipManager) {
            return;
        }
        // init() est idempotent : s'il a deja ete appele (Ext.app.Application le fait
        // via enableQuickTips), il ne recree pas l'info-bulle.
        Ext.tip.QuickTipManager.init();

        var infobulle = Ext.tip.QuickTipManager.getQuickTip();
        if (!infobulle || infobulle.correctifTexteTronque) {
            return;
        }
        infobulle.correctifTexteTronque = true;

        infobulle.showAt = Ext.Function.createSequence(infobulle.showAt, function () {
            var tip = this;
            // L'ajustement est repousse d'un tour de boucle : au tout premier
            // affichage, ExtJS vient de rendre l'info-bulle et sa mise en page n'est
            // appliquee qu'ensuite. Mesurer immediatement donnerait les dimensions de
            // l'etat intermediaire, et le correctif ne prendrait pas.
            // setWidth/setHeight rappellent la mise en page, jamais showAt : pas de
            // recursion possible. Le garde-fou couvre une eventuelle surcharge tierce.
            if (tip.ajustementEnCours) {
                return;
            }
            tip.ajustementEnCours = true;
            Ext.Function.defer(function () {
                try {
                    ajuster(tip);
                } finally {
                    tip.ajustementEnCours = false;
                }
            }, 1);
        });
    }

    // ---------------------------------------------------------------------------------
    // 2) boites de message
    // ---------------------------------------------------------------------------------
    function corrigerBoitesDeMessage() {
        var boite = Ext.MessageBox;
        if (!boite || !boite.on || boite.correctifTexteTronque) {
            return;
        }
        boite.correctifTexteTronque = true;
        boite.on('show', function () {
            var fenetre = this;
            // meme raison que pour les info-bulles : la mise en page n'est pas encore
            // appliquee au moment ou l'evenement est emis
            Ext.Function.defer(function () {
                agrandirSiTexteMasque(fenetre);
            }, 1);
        });
    }

    function agrandirSiTexteMasque(fenetre) {
        var manque = 0,
            elements = [],
            corps = fenetre.body && fenetre.body.dom;

        if (!fenetre.isVisible() || !corps) {
            return;
        }
        elements.push(corps);
        // .x-box-inner : conteneur de la mise en page en boite, hauteur en dur et
        // overflow:hidden, c'est lui qui coupe reellement le texte
        Ext.Array.each(corps.querySelectorAll('.x-box-inner'), function (n) {
            elements.push(n);
        });
        if (fenetre.msg && fenetre.msg.el) {
            elements.push(fenetre.msg.el.dom);
        }
        Ext.Array.each(elements, function (n) {
            manque = Math.max(manque, n.scrollHeight - n.clientHeight);
        });
        if (manque > 1) {
            fenetre.setHeight(fenetre.getHeight() + manque + 2);
            fenetre.center();
        }
    }

    function contenuMasque(corps) {
        return corps.scrollHeight > corps.clientHeight + 1
                || corps.scrollWidth > corps.clientWidth + 1;
    }

    function ajuster(tip) {
        var corps = tip.body && tip.body.dom,
            redimensionnee = false,
            largeurImposee,
            cadre,
            naturelle,
            manque;

        if (!corps || !tip.isVisible()) {
            return;
        }
        // ExtJS recalcule la largeur a chaque affichage (setWidth dans showAt) mais
        // pas la hauteur : une hauteur forcee par un passage precedent resterait
        // collee a l'info-bulle suivante. On la libere avant de mesurer.
        if (tip.hauteurForcee) {
            tip.hauteurForcee = false;
            tip.setHeight(null);
        }
        if (!contenuMasque(corps)) {
            return;
        }

        largeurImposee = tip.activeTarget && tip.activeTarget.width;
        if (!largeurImposee) {
            // largeur du texte s'il tenait sur une seule ligne
            corps.style.whiteSpace = 'nowrap';
            naturelle = corps.scrollWidth;
            corps.style.whiteSpace = '';
            cadre = tip.getWidth() - corps.clientWidth;
            // +2 px : marge de securite contre l'arrondi qui a cause le probleme
            if (naturelle + cadre + 2 <= tip.maxWidth) {
                tip.setWidth(naturelle + cadre + 2);
                redimensionnee = true;
            }
        }

        // texte volontairement long : il reste sur plusieurs lignes, on lui donne
        // la hauteur necessaire pour que toutes soient visibles
        manque = corps.scrollHeight - corps.clientHeight;
        if (manque > 1) {
            tip.setHeight(tip.getHeight() + manque);
            tip.hauteurForcee = true;
            redimensionnee = true;
        }

        if (redimensionnee && (tip.constrainPosition || tip.constrain)) {
            tip.doConstrain();
        }
    }
});
