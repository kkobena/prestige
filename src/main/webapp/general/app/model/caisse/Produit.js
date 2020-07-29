/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/* global Ext */

Ext.define('testextjs.model.caisse.Produit', {
    extend: 'Ext.data.Model',
    idProperty: 'lgFAMILLEID',
    fields: [
        {
            name: 'lgFAMILLEID',
            type: 'string'
        },
        {
            name: 'strLIBELLEE',
            type: 'string'
        },
        {
            name: 'strNAME',
            type: 'string'
        },
        {
            name: 'intCIP',
            type: 'string'
        },
        {
            name: 'strDESCRIPTION',
            type: 'string'
        },
        {
            name: 'intPRICE',
            type: 'number'
        },
        {
            name: 'intNUMBERAVAILABLE',
            type: 'number'
        },
        {
            name: 'intPAF',
            type: 'number'
        },
        {
            name: 'intNUMBER',
            type: 'number'
        },{
            name: 'intNUMBERDETAIL',
            type: 'number'
        },
        {
            name: 'boolDECONDITIONNE',
            type: 'int'
        },
        {
            name: 'lgFAMILLEPARENTID',
            type: 'string'
        }


    ]
});
