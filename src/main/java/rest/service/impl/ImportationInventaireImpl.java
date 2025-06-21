package rest.service.impl;

import dal.TInventaireFamille;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Iterator;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.http.Part;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONObject;
import rest.service.ImportationInventaire;

/**
 *
 * @author koben
 */
@Stateless
public class ImportationInventaireImpl implements ImportationInventaire {

    @PersistenceContext(unitName = "JTA_UNIT")
    private EntityManager em;

    @Override
    public JSONObject bulkUpdate(Part part, String idInventaire) throws Exception {
        int count = 0;
        int i = 0;
        JSONObject json = new JSONObject();

        CSVParser parser = new CSVParser(new InputStreamReader(part.getInputStream()), CSVFormat.EXCEL);

        for (CSVRecord cSVRecord : parser) {

            TInventaireFamille inventaireFamille = findByArticleAndInventaire(cSVRecord.get(0), idInventaire);

            inventaireFamille.setIntNUMBER(Integer.valueOf(cSVRecord.get(1)));
            inventaireFamille.setDtUPDATED(new Date());
            em.merge(inventaireFamille);
            i++;

            count++;
        }

        json.put("count", i);
        json.put("ligne", count - 1);

        return json;
    }

    private TInventaireFamille findByArticleAndInventaire(String cipArticle, String idInventaire) throws Exception {
        TypedQuery<TInventaireFamille> query = em.createQuery(
                " SELECT DISTINCT t FROM  TInventaireFamille t, TFamilleGrossiste g WHERE t.lgINVENTAIREID.lgINVENTAIREID =?1  AND g.lgFAMILLEID.lgFAMILLEID = t.lgFAMILLEID.lgFAMILLEID AND (t.lgFAMILLEID.intCIP =?2 OR g.strCODEARTICLE =?2) ",
                TInventaireFamille.class);
        query.setFirstResult(0).setMaxResults(1).setParameter(1, idInventaire).setParameter(2, cipArticle);
        return query.getSingleResult();
    }
//SELECT DISTINCT t FROM TInventaireFamille t, TFamilleGrossiste g WHERE g.lgFAMILLEID.lgFAMILLEID = t.lgFAMILLEID.lgFAMILLEID AND t.lgINVENTAIREID.lgINVENTAIREID LIKE ?1 AND t.lgFAMILLEID.lgGROSSISTEID.lgGROSSISTEID LIKE ?2 AND t.lgFAMILLEID.lgZONEGEOID.lgZONEGEOID LIKE ?3 AND t.lgFAMILLEID.lgFAMILLEARTICLEID.lgFAMILLEARTICLEID LIKE ?4 AND (t.lgFAMILLEID.strDESCRIPTION LIKE ?6 OR t.lgFAMILLEID.intCIP LIKE ?6 OR t.lgFAMILLEID.intEAN13 LIKE ?6 OR g.strCODEARTICLE LIKE ?6 OR t.lgFAMILLEID.lgZONEGEOID.strCODE LIKE ?6 OR t.lgFAMILLEID.lgFAMILLEARTICLEID.strCODEFAMILLE LIKE ?6) AND t.boolINVENTAIRE = ?8 AND t.strUPDATEDID LIKE ?9 ORDER BY t.lgFAMILLEID.lgFAMILLEARTICLEID.strCODEFAMILLE ASC, t.lgFAMILLEID.strDESCRIPTION
    @Override
    public JSONObject bulkUpdateWithExcel(Part part, String idInventaire) throws Exception {
        int count = 0;
        int i = 0;
        JSONObject json = new JSONObject();

        Workbook workbook = new HSSFWorkbook(part.getInputStream());

        int num = workbook.getNumberOfSheets();

        for (int j = 0; j < num; j++) {
            Sheet sheet = workbook.getSheetAt(j);
            Iterator rows = sheet.rowIterator();
            while (rows.hasNext()) {
                if (count > 0) {
                    Row nextrow = (Row) rows.next();
                    Cell id = nextrow.getCell(1);
                    Cell qty = nextrow.getCell(4);
                    TInventaireFamille inventaireFamille = findByArticleAndInventaire(id.getStringCellValue(),
                            idInventaire);
                    inventaireFamille.setIntNUMBER(Double.valueOf(qty.getNumericCellValue()).intValue());
                    inventaireFamille.setDtUPDATED(new Date());
                    em.merge(inventaireFamille);
                    i++;
                }

                count++;
            }

            json.put("count", i);
            json.put("ligne", count - 1);
        }

        json.put("count", i);
        json.put("ligne", count - 1);

        return json;
    }
}
