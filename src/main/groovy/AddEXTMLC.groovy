/**
 * Name:    EXT003MI_AddEXTMLC
 * Description:
 *          API to add data to EXTMLC table
 *
 * Usage:
 *  Arguments
 * @CONO  Numeric,3       Company
 * @WHLO  Alphanumeric,3  Warehouse
 * @ITNO  Alphanumeric,15 Item Number
 * @WHSL  Alphanumeric,10 Location
 * @BANO  Alphanumeric,20 Lot number
 * @CAMU  Alphanumeric,20 Container 
 * @NOTB  Numeric,10      No. of Tubes
 * @SPKG  Numeric,18      Supplier Package
 *
 * Created By: Birlasoft Ltd.
 * Authors:    Priyanka Nadgouda  
 *
 * History:
 *20220201  base script created
 *
 */


import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

public class AddEXTMLC extends ExtendM3Transaction {

  private final MIAPI mi;
  private final DatabaseAPI database;
  private final ProgramAPI program;
  private final MICallerAPI miCaller;

  public AddEXTMLC(MIAPI mi, DatabaseAPI database,ProgramAPI program, MICallerAPI miCaller ) {
    this.mi = mi;
    this.database = database;
    this.program = program;
    this.miCaller = miCaller;
  }


  //Input variables
  private int iCONO;
  private String iWHLO;
  private String iITNO;
  private String iWHSL;
  private String iBANO;
  private String iCAMU;
  private int iREPN;
  private int iNOTB;
  private String iSPKG;
  private boolean hasError = false;

  /**
   * Main method to initiate the Add transaction
   **/
  public void main() {

    iCONO = mi.in.get("CONO");
    iWHLO = mi.in.get("WHLO");
    iITNO = mi.in.get("ITNO");
    iWHSL = mi.in.get("WHSL");
    iBANO = mi.in.get("BANO");
    iCAMU = mi.in.get("CAMU");
    iREPN = mi.in.get("REPN");
    iNOTB = mi.in.get("NOTB");
    iSPKG = mi.in.get("SPKG");

    hasError = false;
    validateData();

    if(!hasError){

      DBAction query = database.table("EXTMLC").index("00").build();
      DBContainer container = query.getContainer();
      container.set("EXCONO",iCONO);
      container.set("EXWHLO",iWHLO);
      container.set("EXITNO",iITNO);
      container.set("EXWHSL",iWHSL);
      container.set("EXBANO",iBANO);
      container.set("EXCAMU",iCAMU);
      container.set("EXREPN",iREPN);



      if(query.read(container)){
        mi.error("Record already exists");
      }
      else{
        container.set("EXNOTB", iNOTB)
        container.set("EXSPKG", iSPKG)
        container.set("EXRGDT", LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")).toInteger())
        container.set("EXRGTM", LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger())
        container.set("EXLMDT", LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")).toInteger())
        container.set("EXCHNO", 0)
        container.set("EXCHID", program.getUser())
        query.insert(container, callback)
      }
    }
  }


  Closure <?> callback = {}

  /**
   * Vaidate the record in MITLOC table via MMS060MI.Get
   **/
  public void validateData() {

    if(iCONO == 0){
      hasError = true;
      mi.error("CONO - Company must not be 0");
      return;
    }

    if(iWHLO == 0){
      hasError = true;
      mi.error("WHLO - Warehouse must not be 0");
      return;
    }

    if(iITNO == 0){
      hasError = true;
      mi.error("ITNO - Item number must not be 0");
      return;
    }

    if(iWHSL == 0){
      hasError = true;
      mi.error("WHSL - Location must not be 0");
      return;
    }

    def inputRecord = ["CONO": Integer.toString(iCONO),  "WHLO": iWHLO,  "ITNO": iITNO, "WHSL": iWHSL,  "BANO": iBANO,  "CAMU": iCAMU,  "REPN": Integer.toString(iREPN) ];
    Closure<?> handler = {
      Map<String, String> response ->

        if(response.containsKey("error")){
          hasError = true;
          mi.error("Record does not exist in MITLOC");
        }
    }
    miCaller.call("MMS060MI","Get",inputRecord,handler);
  }
}

;
