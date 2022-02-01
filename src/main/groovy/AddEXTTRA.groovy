/**
 * Name:    EXT004MI_AddEXTTRA
 * Description:
 *          API to add data to EXTTRA table
 *
 * Usage:
 *  Arguments
 * @CONO  Numric,3        Company
 * @WHLO  Alphanumeric,3  Warehouse
 * @ITNO  Alphanumeric,15 Item Number
 * @RGDT  Numeric,8       Entry Date
 * @RGTM  Numeric,6       Entry time
 * @TMSX  Numeric,3       Time suffix
 * @NOTB  Numeric,18      No. of Tubes
 * @SPKG  Alphanumeric,30 Supplier Package
 *
 * Created By: Birlasoft Ltd.
 * Authors:    Aditya Bhatkhande  
 *
 * History:
 *  20220201  base script created
 *
 */

import java.time.LocalDate
import java.time.format.DateTimeFormatter

public class AddEXTTRA extends ExtendM3Transaction {
  private final MIAPI mi;
  private final DatabaseAPI database;
  private final ProgramAPI program;
  private final MICallerAPI miCaller;

  private int iCONO;
  private String iWHLO;
  private String iITNO;
  private int iRGDT;
  private int iRGTM;
  private int iTMSX;
  private double iNOTB;
  private String iSPKG;
  private boolean isError = false;

  public AddEXTTRA(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
    this.mi = mi;
    this.database = database;
    this.program = program;
    this.miCaller = miCaller;
  }

  public void main() {

    iCONO = mi.in.CONO;
    iWHLO = mi.in.WHLO;
    iITNO = mi.in.ITNO;
    iRGDT = mi.in.RGDT;
    iRGTM = mi.in.RGTM;
    iTMSX = mi.in.TMSX;
    iNOTB = (Double)(mi.in.NOTB == null?0:mi.in.NOTB);
    iSPKG = mi.in.SPKG;

    DBAction query = database.table("EXTTRA").index("00").selectAllFields().build();
    DBContainer container = query.getContainer();
    container.set("EXCONO",iCONO);
    container.set("EXWHLO",iWHLO);
    container.set("EXITNO",iITNO);
    container.set("EXRGDT",iRGDT);
    container.set("EXRGTM",iRGTM);
    container.set("EXTMSX",iTMSX);


    // Check if the record exists
    if(query.read(container)){
      mi.error("Record already exists in custom table");
    }
    else{
      validateRecord();

      if(!isError){

        container.set("EXNOTB",iNOTB);
        if(iSPKG?.trim()){
          container.set("EXSPKG",iSPKG);
        }
        container.set("EXLMDT", LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")).toInteger());
        container.set("EXCHNO", 0);
        container.set("EXCHID", program.getUser());
        query.insert(container,callback);
      }
    }

  }

  Closure<?> callback = {};


  /**
   * Validate record in MITTRA table using MWS070MI.GetStockTrans
   *
   * */
  public void validateRecord(){

    def params = ["CONO":mi.inData.get("CONO"), "WHLO":mi.inData.get("WHLO"), "ITNO":mi.inData.get("ITNO"), "RGDT":mi.inData.get("RGDT"), "RGTM":mi.inData.get("RGTM"), "TMSX":mi.inData.get("TMSX")];

    def callbackMWS070MI = {

      Map<String, String> response ->
        if(response.ITNO == null){

          isError = true;
          mi.error("Record does not exist in MITTRA");
        }

    }

    miCaller.call("MWS070MI","GetStockTrans",params,callbackMWS070MI);
  }
}
