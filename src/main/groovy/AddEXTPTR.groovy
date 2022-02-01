/**
 * Name:    EXT005MI_AddEXTPTR
 * Description:
 *          API to add data to EXTPTR table
 *
 * Usage:
 *  Arguments
 * @CONO  Numric,3        Company
 * @FACI  Alphanumeric,3  Facility
 * @PRNO  Alphanumeric,15 Product Number
 * @MFNO  Alphanumeric,10 Manufacuting Order
 * @OPNO  Numeric,4       Operation
 * @TRDT  Numeric,8       Transaction Date
 * @TRTM  Numeric,6       Transaction Time
 * @TMSX  Numeric,3       Time suffix
 * @GRWT  Numeric,18      Gross Weight
 * @TAWT  Numeric,18      Tare Weight
 * @NOTB  Numeric,18      No. of Tubes
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

public class AddEXTPTR extends ExtendM3Transaction {
  private final MIAPI mi;
  private final DatabaseAPI database;
  private final ProgramAPI program;
  private final MICallerAPI miCaller;

  private int iCONO;
  private String iFACI;
  private String iPRNO;
  private String iMFNO;
  private int iOPNO;
  private int iTRDT;
  private int iTRTM;
  private int iTMSX;
  private double iGRWT;
  private double iTAWT;
  private double iNOTB;
  private boolean isError = false;

  public AddEXTPTR(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
    this.mi = mi;
    this.database = database;
    this.program = program;
    this.miCaller = miCaller;
  }

  public void main() {

    iCONO = mi.in.CONO;
    iFACI = mi.in.FACI;
    iPRNO = mi.in.PRNO;
    iMFNO = mi.in.MFNO;
    iOPNO = mi.in.OPNO;
    iTRDT = mi.in.TRDT;
    iTRTM = mi.in.TRTM;
    iTMSX = mi.in.TMSX;
    iGRWT = (Double)(mi.in.GRWT == null?0:mi.in.GRWT);
    iTAWT = (Double)(mi.in.TAWT == null?0:mi.in.TAWT);
    iNOTB = (Double)(mi.in.NOTB == null?0:mi.in.NOTB);

    DBAction query = database.table("EXTPTR").index("00").selectAllFields().build();
    DBContainer container = query.getContainer();
    container.set("EXCONO",iCONO);
    container.set("EXFACI",iFACI);
    container.set("EXPRNO",iPRNO);
    container.set("EXMFNO",iMFNO);
    container.set("EXOPNO",iOPNO);
    container.set("EXTRDT",iTRDT);
    container.set("EXTRTM",iTRTM);
    container.set("EXTMSX",iTMSX);

    // Check if the record exists
    if(query.read(container)){
      mi.error("Record already exists in custom table");
    }
    else{
      validateRecord();

      if(!isError){

        container.set("EXGRWT",iGRWT);
        container.set("EXTAWT",iTAWT);
        container.set("EXNOTB",iNOTB);
        container.set("EXRGDT",LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")).toInteger());
        container.set("EXRGTM",LocalTime.now().format(DateTimeFormatter.ofPattern("HHmmss")).toInteger());
        container.set("EXLMDT", LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")).toInteger());
        container.set("EXCHNO", 0);
        container.set("EXCHID", program.getUser());
        query.insert(container,callback);
      }
    }

  }

  Closure<?> callback = {};

  /**
   * Validate the record in MWOPTR table
   *
   * */
  public void validateRecord(){

    DBAction queryMWOPTR = database.table("MWOPTR").index("00").selectAllFields().build();
    DBContainer containerMWOPTR = queryMWOPTR.getContainer();
    containerMWOPTR.set("DJCONO",iCONO);
    containerMWOPTR.set("DJFACI",iFACI);
    containerMWOPTR.set("DJPRNO",iPRNO);
    containerMWOPTR.set("DJMFNO",iMFNO);
    containerMWOPTR.set("DJOPNO",iOPNO);
    containerMWOPTR.set("DJTRDT",iTRDT);
    containerMWOPTR.set("DJTRTM",iTRTM);
    containerMWOPTR.set("DJTMSX",iTMSX);

    if(!queryMWOPTR.read(containerMWOPTR)){
      isError = true;
      mi.error("Record does not exist in MWOPTR");
    }

  }
}
