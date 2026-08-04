-- Remplace "EDITER FACTURE" par "GENERER FACTURE" sur le menu de l'ecran de facturation
-- (uniquement si le libelle actuel contient encore l'ancienne formulation).
UPDATE t_sous_menu
   SET str_VALUE = REPLACE(REPLACE(REPLACE(str_VALUE,
           'EDITER FACTURE', 'GENERER FACTURE'),
           'Editer facture', 'Generer facture'),
           'EDITION DE FACTURE', 'GENERATION DE FACTURE'),
       str_DESCRIPTION = REPLACE(REPLACE(REPLACE(str_DESCRIPTION,
           'EDITER FACTURE', 'GENERER FACTURE'),
           'Editer facture', 'Generer facture'),
           'EDITION DE FACTURE', 'GENERATION DE FACTURE')
 WHERE str_COMPOSANT = 'facturemanager';
