/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/* global Ext */

Ext.define('testextjs.model.caisse.AyantDroit', {
    extend: 'Ext.data.Model',
    idProperty: 'lgAYANTSDROITSID',
    fields: [
        {
            name: 'lgAYANTSDROITSID',
            type: 'string'
        },
        {
            name: 'strFIRSTNAME',
            type: 'string'
        },
        {
            name: 'strLASTNAME',
            type: 'string'
        },
        {
            name: 'strADRESSE',
            type: 'string'
        }, {
            name: 'lgTYPECLIENTID',
            type: 'string'
        }, {
            name: 'strSEXE',
            type: 'string'
        }, {
            name: 'strNUMEROSECURITESOCIAL',
            type: 'string'
        }, {
            name: 'strCODEINTERNE',
            type: 'string'
        }
        , {
            name: 'fullName',
            type: 'string'
        }, {
            name: 'dtNAISSANCE',
            type: 'string'
        }, {
            name: 'strCODEPOSTAL',
            type: 'string'
        }, {
            name: 'lgVILLEID',
            type: 'string'
        }
        , {
            name: 'lgRISQUEID',
            type: 'string'
        }, {
            name: 'lgCATEGORIEAYANTDROITID',
            type: 'string'
        }, {
            name: 'lgCLIENTID',
            type: 'string'
        }


    ]
});
