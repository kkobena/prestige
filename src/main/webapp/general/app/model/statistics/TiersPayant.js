/* global Ext */

Ext.define('testextjs.model.statistics.TiersPayant', {
    extend: 'Ext.data.Model',
    idProperty: 'lg_TIERS_PAYANT_ID',
    fields: [
        // Modele partage entre l'API REST v1/client/tiers-payants (cles lgTIERSPAYANTID/strFULLNAME)
        // et les JSP historiques (cles lg_TIERS_PAYANT_ID/str_FULLNAME).
        // "convert" et non "mapping" : les mappings-fonctions ne sont pas appliques par ce build
        // d'ExtJS (combo tiers payant vide alors que le total arrivait) ; convert lit record.raw
        // et accepte les deux formats de cles.
        {name: 'lg_TIERS_PAYANT_ID', convert: function (v, record) {
                var d = (record && record.raw) ? record.raw : {};
                return d.lg_TIERS_PAYANT_ID !== undefined ? d.lg_TIERS_PAYANT_ID : d.lgTIERSPAYANTID;
            }},
        {name: 'str_FULLNAME', convert: function (v, record) {
                var d = (record && record.raw) ? record.raw : {};
                return d.str_FULLNAME !== undefined ? d.str_FULLNAME : d.strFULLNAME;
            }}
    ]

});
