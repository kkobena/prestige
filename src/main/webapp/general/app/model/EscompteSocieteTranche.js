Ext.define('testextjs.model.EscompteSocieteTranche', {
    extend: 'Ext.data.Model',
    fields: [
        {
            name: 'lg_ESCOMPTE_SOCIETE_ID',
            type: 'string'
        },
        {
            name: 'lg_ESCOMPTE_SOCIETE_TRANCHE_ID',
            type: 'string'
        },
        {
            name: 'lg_TRANCHE_ID',
            type: 'string'
        },
        {
            name: 'str_STATUT',
            type: 'string'
        },
        {
            name: 'dbl_POURCENTAGE_TRANCHE',
            type: 'int'
        }

    ]
});
