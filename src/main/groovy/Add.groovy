/**
 * Name:    EXT002MI_Add
 * Description:
 *          API to add data to EXTDEL table
 *
 * Usage:
 *  Arguments
 * @CONO  Numric,3        Company
 * @INOU  Numeric,1       Direction
 * @DLIX  Numeric,11      Delivery
 * @CREQ  Numeric,1       Credit Requested
 * @CAMT  Numeric,17      Credit Amount
 * @VSNO  Alphanumeric,30 Vessel number
 * @SLNO  Alphanumeric,30 Seal Number
 *
 * Created By: Birlasoft Ltd.
 * Authors:    Aditya Bhatkhande  
 *
 * History:
 *  20220201  base script created
 *
 */


import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

public class Add extends ExtendM3Transaction {
  private final MIAPI mi;
  private final ProgramAPI program;
  private final MICallerAPI miCaller;
  private final DatabaseAPI database;

  //Input variables
  private int iCONO;
  private int iINOU;
  private int iDLIX;
  private int iCREQ;
  private double iCAMT;
  private String iVSNO;
  private String iSLNO;
  private boolean hasError = false;


  public Add(MIAPI mi, ProgramAPI program, MICallerAPI miCaller, DatabaseAPI database) {
    this.mi = mi;
    this.program = program;
    this.miCaller = miCaller;
    this.database = database;
  }

  public void main() {

    iCONO = mi.in.get("CONO");
    iINOU = mi.in.get("INOU");
    iDLIX = mi.in.get("DLIX");
    iCREQ = (mi.inData.get("CREQ").trim()?Integer.parseInt(mi.inData.get("CREQ").trim()):0);
    iCAMT = (mi.inData.get("CAMT").trim()?Double.parseDouble(mi.inData.get("CAMT").trim()):0);
    iVSNO = mi.inData.get("VSNO");
    iSLNO = mi.inData.get("SLNO");


    hasError = false;
    validateData();

    if(!hasError){

      DBAction query = database.table("EXTDEL").index("00").selection("CREQ").build();
      DBContainer container = query.getContainer();
      container.set("EXCONO",iCONO);
      container.set("EXINOU",iINOU);
      container.set("EXDLIX",iDLIX);

      // Check if record exists
      if(query.read(container)){
        mi.error("Record already exists");
      }
      else{
        if(mi.inData.get("CREQ")?.trim()){
          container.set("EXCREQ", iCREQ);
        }

        if(mi.inData.get("CAMT")?.trim()){
          container.set("EXCAMT", iCAMT);
        }

        if(iVSNO?.trim()){
          container.set("EXVSNO", iVSNO);
        }

        if(iSLNO?.trim()){
          container.set("EXSLNO", iSLNO);
        }
        container.set("EXRGDT", LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")).toInteger());
        container.set("EXRGTM", LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger());
        container.set("EXLMDT", LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")).toInteger());
        container.set("EXCHNO", 0);
        container.set("EXCHID", program.getUser());
        query.insert(container, callback);
      }
    }

  }

  Closure <?> callback = {}


/**
 * Validate data in MHDISH table using MWS410MI.GetHead
 *
 **/
  public void validateData() {

    // Check Company
    if(iCONO == 0){
      hasError = true;
      mi.error("CONO - Company must not be 0");
      return;
    }

    // Check Direction
    if(iINOU <1 || iINOU > 4){
      hasError = true;
      mi.error("INOU - Direction is invalid");
      return;
    }

    // Check Delivery
    if(iDLIX == 0){
      hasError = true;
      mi.error("DLIX - Delivery number must not be 0");
      return;
    }

    // Check if valid Delivery number
    def inputRecord = ["CONO": Integer.toString(iCONO), "DLIX": Integer.toString(iDLIX)];
    Closure<?> handler = {
      Map<String, String> response ->

        if(response.containsKey("error")){
          hasError = true;
          mi.error("Delivery number is invalid");
        }
    }
    miCaller.call("MWS410MI","GetHead",inputRecord,handler);
  }
}
