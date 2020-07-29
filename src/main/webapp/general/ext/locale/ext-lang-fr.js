/*
This file is part of Ext JS 4.2

Copyright (c) 2011-2013 Sencha Inc

Contact:  http://www.sencha.com/contact

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as
published by the Free Software Foundation and appearing in the file LICENSE included in the
packaging of this file.

Please review the following information to ensure the GNU General Public License version 3.0
requirements will be met: http://www.gnu.org/copyleft/gpl.html.

If you are unsure which license is appropriate for your use, please contact the sales department
at http://www.sencha.com/contact.

Build date: 2013-05-16 14:36:50 (f9be68accb407158ba2b1be2c226a6ce1f649314)
*/
/**
 * France (France) translation
 * By Thylia
 * 09-11-2007, 02:22 PM
 * updated by disizben (22 Sep 2008)
 * updated by Thylia (20 Apr 2010)
 */
Ext.onReady(function() {

    if (Ext.Date) {
        Ext.Date.shortMonthNames = ["Janv", "F&eacute;vr", "Mars", "Avr", "Mai", "Juin", "Juil", "Ao&ucirc;t", "Sept", "Oct", "Nov", "D&eacute;c"];

        Ext.Date.getShortMonthName = function(month) {
            return Ext.Date.shortMonthNames[month];
        };

        Ext.Date.monthNames = ["Janvier", "F&eacute;vrier", "Mars", "Avril", "Mai", "Juin", "Juillet", "Ao&ucirc;t", "Septembre", "Octobre", "Novembre", "D&eacute;cembre"];

        Ext.Date.monthNumbers = {
            "Janvier": 0,
            "Janv": 0,
            "F&eacute;vrier": 1,
            "F&eacute;vr": 1,
            "Mars": 2,
            "Avril": 3,
            "Avr": 3,
            "Mai": 4,
            "Juin": 5,
            "Juillet": 6,
            "Juil": 6, 
            "Aoo&ucirc;t": 7,
            "Septembre": 8,
            "Sept": 8,
            "Octobre": 9,
            "Oct": 9,
            "Novembre": 10,
            "Nov": 10,
            "D&eacute;cembre": 11,
            "D&eacute;c": 11
        };

        Ext.Date.getMonthNumber = function(name) {
            return Ext.Date.monthNumbers[Ext.util.Format.capitalize(name)];
        };

        Ext.Date.dayNames = ["Dimanche", "Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi"];

        Ext.Date.getShortDayName = function(day) {
            return Ext.Date.dayNames[day].substring(0, 3);
        };

        Ext.Date.parseCodes.S.s = "(?:er)";

        Ext.Date.getSuffix = function() {
            return (this.getDate() == 1) ? "er" : "";
        };
    }

    if (Ext.util && Ext.util.Format) {
        Ext.apply(Ext.util.Format, {
            thousandSeparator: '.',
            decimalSeparator: ',',
            currencySign: '\u20ac',
            // French Euro
            dateFormat: 'd/m/Y'
        });
    }
});

Ext.define("Ext.locale.fr.view.View", {
    override: "Ext.view.View",
    emptyText: ""
});

Ext.define("Ext.locale.fr.grid.plugin.DragDrop", {
    override: "Ext.grid.plugin.DragDrop",
    dragText: "{0} ligne{1} s&eacute;lectionn&eacute;e{1}"
});

Ext.define("Ext.locale.fr.tab.Tab", {
    override: "Ext.tab.Tab",
    closeText: "Fermer cette onglet"
});

// changing the msg text below will affect the LoadMask
Ext.define("Ext.locale.fr.view.AbstractView", {
    override: "Ext.view.AbstractView",
    loadingText: "En cours de chargement..."
});

Ext.define("Ext.locale.fr.picker.Date", {
    override: "Ext.picker.Date",
    todayText: "Aujourd'hui",
    minText: "Cette date est ant&eacute;rieure &agrave; la date minimum",
    maxText: "Cette date est post&eacute;rieure &agrave; la date maximum",
    disabledDaysText: "",
    disabledDatesText: "",
    nextText: 'Mois suivant (CTRL+Fl&egrave;che droite)',
    prevText: "Mois pr&eacute;c&eacute;dent (CTRL+Fl&egrave;che gauche)",
    monthYearText: "Choisissez un mois (CTRL+Fl&egrave;che haut ou bas pour changer d'ann&eacute;e.)",
    todayTip: "{0} (Barre d'espace)",
    format: "d/m/y",
    startDay: 1
});

Ext.define("Ext.locale.fr.picker.Month", {
    override: "Ext.picker.Month",
    okText: "&#160;OK&#160;",
    cancelText: "Annuler"
});

Ext.define("Ext.locale.fr.toolbar.Paging", {
    override: "Ext.PagingToolbar",
    beforePageText: "Page",
    afterPageText: "sur {0}",
    firstText: "Premi&egrave;re page",
    prevText: "Page pr&eacute;c&eacute;dente",
    nextText: "Page suivante",
    lastText: "Derni&egrave;re page",
    refreshText: "Actualiser la page",
    displayMsg: "Page courante {0} - {1} sur {2}",
    emptyMsg: 'Aucune donn&eacute;e &agrave; afficher'
});

Ext.define("Ext.locale.fr.form.Basic", {
    override: "Ext.form.Basic",
    waitTitle: "Veuillez patienter..."
});

Ext.define("Ext.locale.fr.form.field.Base", {
    override: "Ext.form.field.Base",
    invalidText: "La valeur de ce champ est invalide"
});

Ext.define("Ext.locale.fr.form.field.Text", {
    override: "Ext.form.field.Text",
    minLengthText: "La longueur minimum de ce champ est de {0} caract&egrave;re(s)",
    maxLengthText: "La longueur maximum de ce champ est de {0} caract&egrave;re(s)",
    blankText: "Ce champ est obligatoire",
    regexText: "",
    emptyText: null
});

Ext.define("Ext.locale.fr.form.field.Number", {
    override: "Ext.form.field.Number",
    decimalSeparator: ",",
    decimalPrecision: 2,
    minText: "La valeur minimum de ce champ doit être de {0}",
    maxText: "La valeur maximum de ce champ doit être de {0}",
    nanText: "{0} n'est pas un nombre valide",
    negativeText: "La valeur de ce champ ne peut être n&eacute;gative"    
});

Ext.define("Ext.locale.fr.form.field.File", { 
    override: "Ext.form.field.File", 
    buttonText: "Parcourir..." 
});

Ext.define("Ext.locale.fr.form.field.Date", {
    override: "Ext.form.field.Date",
    disabledDaysText: "D&eacute;sactiv&eacute;",
    disabledDatesText: "D&eacute;sactiv&eacute;",
    minText: "La date de ce champ ne peut être ant&eacute;rieure au {0}",
    maxText: "La date de ce champ ne peut être post&eacute;rieure au {0}",
    invalidText: "{0} n'est pas une date valide - elle doit être au format suivant: {1}",
    format: "d/m/y",
    altFormats: "d/m/Y|d-m-y|d-m-Y|d/m|d-m|dm|dmy|dmY|d|Y-m-d"
});

Ext.define("Ext.locale.fr.form.field.ComboBox", {
    override: "Ext.form.field.ComboBox",
    valueNotFoundText: undefined
}, function() {
    Ext.apply(Ext.form.field.ComboBox.prototype.defaultListConfig, {
        loadingText: "En cours de chargement..."
    });
});

Ext.define("Ext.locale.fr.form.field.VTypes", {
    override: "Ext.form.field.VTypes",
    emailText: 'Ce champ doit contenir une adresse email au format: "usager@example.com"',
    urlText: 'Ce champ doit contenir une URL au format suivant: "http:/' + '/www.example.com"',
    alphaText: 'Ce champ ne peut contenir que des lettres et le caract&egrave;re soulign&eacute; (_)',
    alphanumText: 'Ce champ ne peut contenir que des caract&egrave;res alphanum&eacute;riques ainsi que le caract&egrave;re soulign&eacute; (_)'
});

Ext.define("Ext.locale.fr.form.field.HtmlEditor", {
    override: "Ext.form.field.HtmlEditor",
    createLinkText: "Veuillez entrer l'URL pour ce lien:"
}, function() {
    Ext.apply(Ext.form.field.HtmlEditor.prototype, {
        buttonTips: {
            bold: {
                title: 'Gras (Ctrl+B)',
                text: 'Met le texte s&eacute;lectionn&eacute; en gras.',
                cls: Ext.baseCSSPrefix + 'html-editor-tip'
            },
            italic: {
                title: 'Italique (Ctrl+I)',
                text: 'Met le texte s&eacute;lectionn&eacute; en italique.',
                cls: Ext.baseCSSPrefix + 'html-editor-tip'
            },
            underline: {
                title: 'Soulign&eacute; (Ctrl+U)',
                text: 'Souligne le texte s&eacute;lectionn&eacute;.',
                cls: Ext.baseCSSPrefix + 'html-editor-tip'
            },
            increasefontsize: {
                title: 'Agrandir la police',
                text: 'Augmente la taille de la police.',
                cls: Ext.baseCSSPrefix + 'html-editor-tip'
            },
            decreasefontsize: {
                title: 'R&eacute;duire la police',
                text: 'R&eacute;duit la taille de la police.',
                cls: Ext.baseCSSPrefix + 'html-editor-tip'
            },
            backcolor: {
                title: 'Couleur de surbrillance',
                text: 'Modifie la couleur de fond du texte s&eacute;lectionn&eacute;.',
                cls: Ext.baseCSSPrefix + 'html-editor-tip'
            },
            forecolor: {
                title: 'Couleur de police',
                text: 'Modifie la couleur du texte s&eacute;lectionn&eacute;.',
                cls: Ext.baseCSSPrefix + 'html-editor-tip'
            },
            justifyleft: {
                title: 'Aligner &agrave; gauche',
                text: 'Aligne le texte &agrave; gauche.',
                cls: Ext.baseCSSPrefix + 'html-editor-tip'
            },
            justifycenter: {
                title: 'Centrer',
                text: 'Centre le texte.',
                cls: Ext.baseCSSPrefix + 'html-editor-tip'
            },
            justifyright: {
                title: 'Aligner &agrave; droite',
                text: 'Aligner le texte &agrave; droite.',
                cls: Ext.baseCSSPrefix + 'html-editor-tip'
            },
            insertunorderedlist: {
                title: 'Liste &agrave; puce',
                text: 'D&eacute;marre une liste &agrave; puce.',
                cls: Ext.baseCSSPrefix + 'html-editor-tip'
            },
            insertorderedlist: {
                title: 'Liste num&eacute;rot&eacute;e',
                text: 'D&eacute;marre une liste num&eacute;rot&eacute;e.',
                cls: Ext.baseCSSPrefix + 'html-editor-tip'
            },
            createlink: {
                title: 'Lien hypertexte',
                text: 'Transforme en lien hypertexte.',
                cls: Ext.baseCSSPrefix + 'html-editor-tip'
            },
            sourceedit: {
                title: 'Code source',
                text: 'Basculer en mode &eacute;dition du code source.',
                cls: Ext.baseCSSPrefix + 'html-editor-tip'
            }
        }
    });
});

Ext.define("Ext.locale.fr.grid.header.Container", {
    override: "Ext.grid.header.Container",
    sortAscText: "Tri croissant",
    sortDescText: "Tri d&eacute;croissant",
    columnsText: "Colonnes"
});

Ext.define("Ext.locale.fr.grid.GroupingFeature", {
    override: "Ext.grid.GroupingFeature",
    emptyGroupText: '(Aucun)',
    groupByText: 'Grouper par ce champ',
    showGroupsText: 'Afficher par groupes'
});

Ext.define("Ext.locale.fr.grid.PropertyColumnModel", {
    override: "Ext.grid.PropertyColumnModel",
    nameText: "Propri&eacute;t&eacute;",
    valueText: "Valeur",
    dateFormat: "d/m/Y",
    trueText: "vrai",
    falseText: "faux"
});

Ext.define("Ext.locale.fr.form.field.Time", {
    override: "Ext.form.field.Time",
    minText: "L'heure de ce champ ne peut être ant&eacute;rieure &agrave; {0}",
    maxText: "L'heure de ce champ ne peut être post&eacute;rieure &agrave; {0}",
    invalidText: "{0} n'est pas une heure valide",
    format: "H:i",
    altFormats: "g:ia|g:iA|g:i a|g:i A|h:i|g:i|H:i|ga|h a|g a|g A|gi|hi|Hi|gia|hia|g|H"
});

Ext.define("Ext.locale.fr.form.CheckboxGroup", {
    override: "Ext.form.CheckboxGroup",
    blankText: "Vous devez s&eacute;lectionner au moins un &eacute;l&eacute;ment dans ce groupe"
});

Ext.define("Ext.locale.fr.form.RadioGroup", {
    override: "Ext.form.RadioGroup",
    blankText: "Vous devez s&eacute;lectionner au moins un &eacute;l&eacute;ment dans ce groupe"
});

Ext.define("Ext.locale.fr.window.MessageBox", {
    override: "Ext.window.MessageBox",
    buttonText: {
        ok: "OK",
        cancel: "Annuler",
        yes: "Oui",
        no: "Non"
    }    
});

// This is needed until we can refactor all of the locales into individual files
Ext.define("Ext.locale.fr.Component", {	
    override: "Ext.Component"
});

