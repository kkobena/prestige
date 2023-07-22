/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest.service.impl;

import dal.MvtTransaction;
import dal.MvtTransaction_;
import dal.TBonLivraison;
import dal.TGrossiste;
import dal.TPreenregistrement;
import dal.TTypeMvtCaisse;
import dal.TTypeReglement;
import dal.TUser;
import dal.TUser_;
import dal.enumeration.CategoryTransaction;
import dal.enumeration.TypeTransaction;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import rest.service.TransactionService;
import util.DateConverter;

/**
 *
 * @author DICI
 */
@Stateless
public class TransactionServiceImpl implements TransactionService {

    @Override
    public void copyTransaction(TUser ooTUser, MvtTransaction cashTransaction, TPreenregistrement _newP,
            TPreenregistrement old, EntityManager emg) {
        MvtTransaction _new = cashTransaction;
        if (cashTransaction.getMvtDate().isEqual(LocalDate.now())) {
            cashTransaction.setUser(ooTUser);
            cashTransaction.setAvoidAmount((-1) * cashTransaction.getAvoidAmount());
            cashTransaction.setMontant((-1) * cashTransaction.getMontant());
            cashTransaction.setMontantNet((-1) * cashTransaction.getMontantNet());
            cashTransaction.setMontantRegle((-1) * cashTransaction.getMontantRegle());
            cashTransaction.setMontantRestant((-1) * cashTransaction.getMontantRestant());
            cashTransaction.setMontantRemise((-1) * cashTransaction.getMontantRemise());
            cashTransaction.setMontantCredit((-1) * cashTransaction.getMontantCredit());
            cashTransaction.setMontantPaye((-1) * cashTransaction.getMontantPaye());
            cashTransaction.setCategoryTransaction(CategoryTransaction.DEBIT);
            cashTransaction.setMontantTva((-1) * cashTransaction.getMontantTva());
            cashTransaction.setMarge((-1) * cashTransaction.getMarge());
            cashTransaction.setChecked(Boolean.FALSE);
            emg.merge(cashTransaction);
        } else {
            _new.setUuid(UUID.randomUUID().toString());
            _new.setUser(ooTUser);
            _new.setCreatedAt(LocalDateTime.now());
            _new.setPkey(_newP.getLgPREENREGISTREMENTID());
            _new.setMvtDate(LocalDate.now());
            _new.setAvoidAmount((-1) * cashTransaction.getAvoidAmount());
            _new.setMontant((-1) * cashTransaction.getMontant());
            _new.setMontantNet((-1) * cashTransaction.getMontantNet());
            _new.setMontantRegle((-1) * cashTransaction.getMontantRegle());
            _new.setMontantRestant((-1) * cashTransaction.getMontantRestant());
            _new.setMontantRemise((-1) * cashTransaction.getMontantRemise());
            _new.setMontantCredit((-1) * cashTransaction.getMontantCredit());
            _new.setMontantPaye((-1) * cashTransaction.getMontantPaye());
            _new.setCategoryTransaction(CategoryTransaction.DEBIT);
            _new.setMontantTva((-1) * cashTransaction.getMontantTva());
            _new.setMarge((-1) * cashTransaction.getMarge());
            _new.setChecked(Boolean.TRUE);
            _new.setReference(_newP.getStrREF());
            emg.persist(_new);
            // emg.merge(cashTransaction);

        }

    }

    @Override
    public void addTransaction(TUser ooTUser, String pkey, Integer montant, Integer montantNet, Integer montantVerse,
            TGrossiste grossiste, EntityManager emg, Integer montantTva, String reference) {
        MvtTransaction _new = new MvtTransaction();
        _new.setUuid(UUID.randomUUID().toString());
        _new.setUser(ooTUser);
        _new.setCreatedAt(LocalDateTime.now());
        _new.setPkey(pkey);
        _new.setMvtDate(LocalDate.now());
        _new.setAvoidAmount(0);
        _new.setMontant(montant);
        _new.setMontantTva(montantTva);
        _new.setMagasin(ooTUser.getLgEMPLACEMENTID());
        _new.setCaisse(ooTUser);
        _new.setMontantCredit(0);
        _new.setGrossiste(grossiste);
        _new.setMontantRegle(0);
        _new.setMontantVerse(montantVerse);
        _new.setMontantRestant(montant);
        _new.setMontantNet(montantNet);
        _new.setMontantRemise(0);
        _new.setMarge(0);
        _new.setReference(reference);
        _new.setCategoryTransaction(CategoryTransaction.DEBIT);
        _new.setTypeTransaction(TypeTransaction.ACHAT);
        _new.setChecked(Boolean.TRUE);
        emg.persist(_new);
    }

    @Override
    public void addTransaction(TUser ooTUser, TUser caisse, String pkey, Integer montant, Integer voidAmount,
            Integer montantNet, Integer montantVerse, Boolean checked, CategoryTransaction categoryTransaction,
            TypeTransaction typeTransaction, TTypeReglement reglement, TTypeMvtCaisse tTypeMvtCaisse, EntityManager emg,
            Integer montantPaye, Integer montantTva, Integer marge, String reference) {
        MvtTransaction _new = new MvtTransaction();
        int compare = montantNet.compareTo(montantVerse);
        Integer montantPaid, montantRestant = 0;
        if (compare <= 0) {
            montantPaid = montantNet;
        } else {
            montantPaid = montantVerse;
            montantRestant = montantNet - montantVerse;
        }

        _new.setUuid(UUID.randomUUID().toString());
        _new.setUser(ooTUser);
        _new.setCreatedAt(LocalDateTime.now());
        _new.setPkey(pkey);
        _new.setMvtDate(LocalDate.now());
        _new.setAvoidAmount(voidAmount);
        _new.setMontant(montant);
        _new.setMagasin(ooTUser.getLgEMPLACEMENTID());
        _new.setCaisse(caisse);
        _new.setMontantCredit(0);
        _new.setMontantVerse(montantVerse);
        _new.setMontantRegle(montantPaid);
        _new.setReference(reference);
        _new.setMontantNet(montantNet);
        _new.settTypeMvtCaisse(tTypeMvtCaisse);
        _new.setReglement(reglement);
        // _new.setMontantRestant(montantRestant);
        _new.setMontantRestant(montantRestant > 4 ? montantRestant : 0);
        _new.setMarge(marge);
        _new.setMontantPaye(montantPaye);
        _new.setMontantRemise(montant - montantNet);
        _new.setCategoryTransaction(categoryTransaction);
        _new.setTypeTransaction(typeTransaction);
        _new.setChecked(checked);
        _new.setMontantTva(montantTva);
        emg.persist(_new);

    }

    @Override
    public void addTransaction(TUser ooTUser, TUser caisse, String pkey, Integer montant, Integer voidAmount,
            Integer montantNet, Integer discount, Integer montantVerse, Boolean checked,
            CategoryTransaction categoryTransaction, TypeTransaction typeTransaction, TTypeReglement reglement,
            TTypeMvtCaisse tTypeMvtCaisse, Integer montantCredit, EntityManager emg, Integer montantPaye,
            Integer montantTva, Integer marge, String reference) {
        MvtTransaction _new = new MvtTransaction();
        int compare = montantNet.compareTo(montantVerse);
        Integer montantPaid, montantRestant = 0;
        if (compare <= 0) {
            montantPaid = montantNet;
        } else {
            montantPaid = montantVerse;
            montantRestant = montantNet - montantVerse;
        }

        _new.setUuid(UUID.randomUUID().toString());
        _new.setUser(ooTUser);
        _new.setCreatedAt(LocalDateTime.now());
        _new.setPkey(pkey);
        _new.setMvtDate(LocalDate.now());
        _new.setAvoidAmount(voidAmount);
        _new.setMontant(montant);
        _new.setMagasin(ooTUser.getLgEMPLACEMENTID());
        _new.setCaisse(caisse);
        _new.setMontantCredit(montantCredit);
        _new.setMontantVerse(montantVerse);
        _new.setMontantRegle(montantPaid);
        _new.setMontantPaye(montantPaye);
        _new.setMontantNet(montantNet + montantCredit);
        _new.settTypeMvtCaisse(tTypeMvtCaisse);
        _new.setReglement(reglement);
        // _new.setMontantRestant(montantRestant);
        _new.setMontantRestant(montantRestant > 4 ? montantRestant : 0);
        _new.setMontantRemise(discount);
        _new.setMontantTva(montantTva);
        _new.setMarge(marge);
        _new.setCategoryTransaction(categoryTransaction);
        _new.setTypeTransaction(typeTransaction);
        _new.setChecked(checked);
        _new.setReference(reference);
        emg.persist(_new);
    }

    @Override
    public void addTransaction(TUser ooTUser, TUser caisse, String pkey, Integer montant, Integer voidAmount,
            Integer montantNet, Integer montantVerse, Boolean checked, CategoryTransaction categoryTransaction,
            TypeTransaction typeTransaction, TTypeReglement reglement, TTypeMvtCaisse tTypeMvtCaisse, EntityManager emg,
            Integer montantPaye, Integer montantTva, Integer marge, String reference, String organisme) {
        MvtTransaction _new = new MvtTransaction();
        int compare = montantNet.compareTo(montantVerse);
        Integer montantPaid, montantRestant = 0;
        if (compare <= 0) {
            montantPaid = montantNet;
        } else {
            montantPaid = montantVerse;
            montantRestant = montantNet - montantVerse;
        }
        _new.setUuid(UUID.randomUUID().toString());
        _new.setUser(ooTUser);
        _new.setCreatedAt(LocalDateTime.now());
        _new.setPkey(pkey);
        _new.setMvtDate(LocalDate.now());
        _new.setAvoidAmount(voidAmount);
        _new.setMontant(montant);
        _new.setMagasin(ooTUser.getLgEMPLACEMENTID());
        _new.setCaisse(caisse);
        _new.setMontantCredit(0);
        _new.setMontantVerse(montantVerse);
        _new.setMontantRegle(montantPaid);
        _new.setMontantPaye(montantPaye);
        _new.setMontantNet(montantNet);
        _new.settTypeMvtCaisse(tTypeMvtCaisse);
        _new.setReglement(reglement);
        _new.setMontantRestant(montantRestant > 4 ? montantRestant : 0);
        _new.setMontantRemise(0);
        _new.setMontantTva(montantTva);
        _new.setMarge(marge);
        _new.setCategoryTransaction(categoryTransaction);
        _new.setTypeTransaction(typeTransaction);
        _new.setChecked(checked);
        _new.setReference(reference);
        _new.setOrganisme(organisme);
        emg.persist(_new);
    }

    @Override
    public Integer avoidAmount(String userId, LocalDate dtStart, EntityManager emg) {
        List<Predicate> predicates = new ArrayList<>();
        try {
            CriteriaBuilder cb = emg.getCriteriaBuilder();
            CriteriaQuery<MvtTransaction> cq = cb.createQuery(MvtTransaction.class);
            Root<MvtTransaction> root = cq.from(MvtTransaction.class);
            cq.select(root);
            // Predicate btw = cb.between(root.get(MvtTransaction_.mvtDate), dtStart, dtEnd);
            predicates.add(cb.equal(root.get(MvtTransaction_.mvtDate), dtStart));
            predicates.add(cb.equal(root.get(MvtTransaction_.caisse).get(TUser_.lgUSERID), userId));
            predicates.add(cb.equal(root.get(MvtTransaction_.categoryTransaction), CategoryTransaction.DEBIT));
            predicates.add(root.get(MvtTransaction_.typeTransaction).in(TypeTransaction.VENTE_CREDIT,
                    TypeTransaction.VENTE_COMPTANT));
            // predicates.add(cb.or(cb.equal(root.get(MvtTransaction_.typeTransaction), TypeTransaction.VENTE_COMPTANT),
            // cb.equal(root.get(MvtTransaction_.typeTransaction), TypeTransaction.VENTE_CREDIT)));
            cq.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
            TypedQuery<MvtTransaction> q = emg.createQuery(cq);
            List<MvtTransaction> list = q.getResultList();

            return list.stream().map(x -> Math.abs(x.getMontantRegle())).reduce(0, Integer::sum);

        } catch (Exception e) {
            e.printStackTrace(System.err);
            return 0;
        }
    }

    @Override
    public void addTransaction(TUser ooTUser, TUser caisse, String pkey, Integer montant, Integer voidAmount,
            Integer montantNet, Integer montantVerse, Boolean checked, CategoryTransaction categoryTransaction,
            TypeTransaction typeTransaction, TTypeReglement reglement, TTypeMvtCaisse tTypeMvtCaisse, EntityManager emg,
            Integer montantPaye, Integer montantTva, Integer marge, String reference, String organisme,
            Integer montantRestant) {
        MvtTransaction transaction = new MvtTransaction();
        int compare = montantNet.compareTo(montantVerse);
        Integer montantPaid;
        if (compare <= 0) {
            montantPaid = montantNet;
        } else {
            montantPaid = montantVerse;

        }
        transaction.setUuid(UUID.randomUUID().toString());
        transaction.setUser(ooTUser);
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setPkey(pkey);
        transaction.setMvtDate(LocalDate.now());
        transaction.setAvoidAmount(voidAmount);
        transaction.setMontant(montant);
        transaction.setMagasin(ooTUser.getLgEMPLACEMENTID());
        transaction.setCaisse(caisse);
        transaction.setMontantCredit(0);
        transaction.setMontantVerse(montantVerse);
        transaction.setMontantRegle(montantPaid);
        transaction.setMontantPaye(montantPaye);
        transaction.setMontantNet(montantNet);
        transaction.settTypeMvtCaisse(tTypeMvtCaisse);
        transaction.setReglement(reglement);
        transaction.setMontantRestant(montantRestant > 4 ? montantRestant : 0);
        transaction.setMontantRemise(0);
        transaction.setMontantTva(montantTva);
        transaction.setMarge(marge);
        transaction.setCategoryTransaction(categoryTransaction);
        transaction.setTypeTransaction(typeTransaction);
        transaction.setChecked(checked);
        transaction.setReference(reference);
        transaction.setOrganisme(organisme);
        emg.persist(transaction);
    }

    public void addTransactionBL(TUser ooTUser, TBonLivraison bl, EntityManager emg) {

        MvtTransaction transaction = new MvtTransaction();
        transaction.setUuid(UUID.randomUUID().toString());
        transaction.setUser(ooTUser);
        transaction.setCreatedAt(DateConverter.convertDateToLocalDateTime(bl.getDtDATELIVRAISON()));
        transaction.setPkey(bl.getLgBONLIVRAISONID());
        transaction.setMvtDate(DateConverter.convertDateToLocalDate(bl.getDtDATELIVRAISON()));
        transaction.setAvoidAmount(0);
        transaction.setMontant(bl.getIntHTTC());
        transaction.setMontantTva(bl.getIntTVA());
        transaction.setMagasin(ooTUser.getLgEMPLACEMENTID());
        transaction.setCaisse(ooTUser);
        transaction.setMontantCredit(0);
        transaction.setGrossiste(bl.getLgORDERID().getLgGROSSISTEID());
        transaction.setMontantRegle(0);
        transaction.setMontantVerse(0);
        transaction.setMontantRestant(bl.getIntHTTC());
        transaction.setMontantNet(bl.getIntMHT());
        transaction.setMontantRemise(0);
        transaction.setMarge(0);
        transaction.setMontantAcc(bl.getIntMHT());
        transaction.setReference(bl.getStrREFLIVRAISON());
        transaction.setCategoryTransaction(CategoryTransaction.DEBIT);
        transaction.setTypeTransaction(TypeTransaction.ACHAT);
        transaction.setChecked(Boolean.TRUE);
        emg.persist(transaction);
    }

}
