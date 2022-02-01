/**
 * Name:    EXT003MI_UpdateEXTMLC
 * Description:
 *          API to update data in EXTMLC table
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
import java.time.format.DateTimeFormatter

public class UpdateEXTMLC extends ExtendM3Transaction {
  private final MIAPI mi;
  private final DatabaseAPI database;
  private final ProgramAPI program;
  private final MICallerAPI miCaller;

  public UpdateEXTMLC(MIAPI mi, ProgramAPI program, MICallerAPI miCaller, DatabaseAPI database) {
    this.mi = mi;
    this.database = database;
    this.program = program;
    this.miCaller = miCaller;
  }

  /**
   * Input variables
   **/
  private int iCONO;
  private String iWHLO;
  private String iITNO;
  private String iWHSL;
  private String iBANO;
  private String iCAMU;
  private int iREPN;
  private Double iNOTB;
  private String iSPKG;
  private boolean hasError = false;

  /**
   * Main method to update record in EXTMLC table
   **/
  public void main() {

    iCONO = mi.in.CONO;
    iWHLO = mi.in.WHLO;
    iITNO = mi.in.ITNO;
    iWHSL = mi.in.WHSL;
    iBANO = mi.in.BANO;
    iCAMU = mi.in.CAMU;
    iREPN = mi.in.REPN;
    iNOTB = (Double)(mi.in.NOTB == null?0:mi.in.NOTB);
    iSPKG = mi.in.SPKG;

    DBAction query = database.table("EXTMLC").index("00").selectAllFields().build();
    DBContainer container = query.getContainer();
    container.set("EXCONO",iCONO);
    container.set("EXWHLO",iWHLO);
    container.set("EXITNO",iITNO);
    container.set("EXWHSL",iWHSL);
    container.set("EXBANO",iBANO);
    container.set("EXCAMU",iCAMU);
    container.set("EXREPN",iREPN);

    if(!query.readLock(container,updateCallBack))
      mi.error("Record does not exist in custom table");
  }

  /**
   * Callback for Update operation
   **/
  Closure<?> updateCallBack = {
    LockedResult lockedResult ->

      if(mi.inData.get("NOTB")?.trim()){
        lockedResult.set("EXNOTB",iNOTB);
      }
      if(mi.inData.get("SPKG")?.trim()){
        lockedResult.set("EXSPKG",iSPKG);
      }

      lockedResult.set("EXLMDT", LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")).toInteger())
      lockedResult.set("EXCHNO", lockedResult.get("EXCHNO").toString().toInteger() + 1);
      lockedResult.set("EXCHID", program.getUser());
      lockedResult.update();
  }

}


