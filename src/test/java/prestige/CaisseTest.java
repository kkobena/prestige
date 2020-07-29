package prestige;


import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;


import bll.bllBase;
import bll.common.Parameter;

import bll.printer.DriverPrinter;
import commonTasks.dto.MvtCaisseDTO;
import cust_barcode.barecodeManager;
import dal.TClient;
import rest.AjustementRessource;
import rest.report.ReportUtil;
import rest.report.pdf.Balance;
import rest.service.CaisseService;

import util.DateConverter;



//@RunWith(Arquillian.class)
public class CaisseTest {
	//@Deployment
	public static Archive<?> create() {
		return ShrinkWrap.create(WebArchive.class)
				.addAsLibraries(Maven.resolver().loadPomFromFile("pom.xml")
						.importRuntimeDependencies().resolve()
						.withTransitivity().asFile())
				.addPackage(bllBase.class.getPackage())
			//	 .addAsLibraries(Maven.configureResolver()
				//.addPackage(SuggestionManager.class.getPackage())
			
				//.addPackage(CalendrierManager.class.getPackage())
				//.addPackage(DiffereManagement.class.getPackage())
				
				//.addPackage(EntityData.class.getPackage())
				.addPackage(TClient.class.getPackage())
				//.addPackage(factureManagement.class.getPackage())
				//.addPackage(gatewayManager.class.getPackage())
				.addPackage(Parameter.class.getPackage())
				//.addPackage(ServicesNotifCustomer.class.getPackage())
				//.addPackage(ServiceCaisse.class.getPackage())
				//.addPackage(Bonlivraisonmanagerinterface.class.getPackage())
				//.addPackage(MigrationManager.class.getPackage())
				//.addPackage(DevisManagement.class.getPackage())
				//.addPackage(BalanceVenteCaisse.class.getPackage())
				//.addPackage(CommonTasksImpl.class.getPackage())
				.addPackage(DriverPrinter.class.getPackage())
				.addPackage(MvtCaisseDTO.class.getPackage())
			//	.addPackage(DataSourceManager.class.getPackage())
				//.addPackage(Custom.class.getPackage())
				.addPackage(barecodeManager.class.getPackage())
				//.addPackage(DashboardBuilder.class.getPackage())
				//.addPackage(NumeComptable.class.getPackage())
				//.addPackage(LogMSB.class.getPackage())
				.addPackage(AjustementRessource.class.getPackage())
				.addPackage(ReportUtil.class.getPackage())
				.addPackage(Balance.class.getPackage())
				.addPackage(CaisseService.class.getPackage())
//				.addPackage(CaisseServiceImpl.class.getPackage())
				.addPackage(DateConverter.class.getPackage())
				.addAsResource("persistence.xml","META-INF/persistence.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				
				;
	}
//	@Inject CaisseService caisseService;
	//@Test
	public void caisseIntance() throws Exception{
//		assertTrue("caisse injectée correctement ----", caisseService!=null);
	}
	//@Test
	public void testData() {
//		caisseService.findAllTypeMvtProduit().stream().forEach(x->System.out.println(x));
	}

}
