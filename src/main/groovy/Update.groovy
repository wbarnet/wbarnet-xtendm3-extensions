/**
 * Name:    EXT002MI_Update
 * Description:
 *          API to update data in EXTDEL table
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
import java.time.format.DateTimeFormatter

public class Update extends ExtendM3Transaction {
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


  public Update(MIAPI mi, ProgramAPI program, MICallerAPI miCaller, DatabaseAPI database) {
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


    DBAction query = database.table("EXTDEL").index("00").selection("CREQ").build();
    DBContainer container = query.getContainer();
    container.set("EXCONO",iCONO);
    container.set("EXINOU",iINOU);
    container.set("EXDLIX",iDLIX);

    // Check if the record exists
    if(!query.read(container)){
      mi.error("Record does not exist");
    }
    else{
      query.readLock(container,updateCallBack);
    }


  }


  // Callback for update operation
  Closure<?> updateCallBack = { LockedResult lockedResult ->

    if(mi.inData.get("CREQ")?.trim()){
      lockedResult.set("EXCREQ", iCREQ);
    }

    if(mi.inData.get("CAMT")?.trim()){
      lockedResult.set("EXCAMT", iCAMT);
    }

    if(iVSNO?.trim()){
      lockedResult.set("EXVSNO", iVSNO);
    }

    if(iSLNO?.trim()){
      lockedResult.set("EXSLNO", iSLNO);
    }


    lockedResult.set("EXLMDT", LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")).toInteger())
    lockedResult.set("EXCHNO", lockedResult.get("EXCHNO").toString().toInteger() + 1);
    lockedResult.set("EXCHID", program.getUser());
    lockedResult.update();
  }


}
