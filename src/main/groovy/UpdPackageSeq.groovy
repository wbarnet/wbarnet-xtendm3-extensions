/**
 * Name:    EXT006MI_UpdPackageSeq
 * Description:
 *          API to update data in MMNSEQ table
 *
 * Usage:
 *  Arguments
 * @CONO  Numric,3        Company
 * @WHLO  Alphanumeric,3       Warehouse
 * @SEQN  Numeric,9      Sequence Number
 *
 *
 * Created By: Birlasoft Ltd.
 * Authors:    Hitesh Chotrani  
 *
 * History:
 *  20220519  base script created
 * 
 */

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class UpdPackageSeq extends ExtendM3Transaction {
  private final MIAPI mi;
  private final DatabaseAPI database;
  private final ProgramAPI program;
  private final MICallerAPI miCaller;

  private int iCONO;
  private String iWHLO;
  private int iSEQN;

  public UpdPackageSeq(MIAPI mi, DatabaseAPI database, ProgramAPI program,MICallerAPI miCaller) {
    this.mi = mi;
    this.database = database;
    this.program = program;
    this.miCaller=miCaller;
  }

  public void main() {

    if (mi.in.CONO != null) {
      iCONO = mi.in.CONO;
    } else {
      iCONO = program.LDAZD.get("CONO");
    }

    iWHLO = mi.in.WHLO;
    iSEQN = mi.in.SEQN;
    
    if(checkWareshouse(iWHLO)){

    DBAction query = database.table("MMNSEQ").index("00").build();
    DBContainer container = query.getContainer();
    container.set("ZNCONO", iCONO);
    container.set("ZNANPR", "");
    container.set("ZNWHLO", iWHLO);
    container.set("ZNDATE", LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")).toInteger());

    // Check if the record exists
    int noOfRecords = query.readAllLock(container, 4, updateRecord);
    if (noOfRecords == 0) {
      //Create a new record
      query.readAllLock(container, 3, deleteRecord);
      container.set("ZNSEQN", iSEQN);
      container.set("ZNLMDT", LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")).toInteger());
      container.set("ZNRGDT", LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")).toInteger());
      container.set("ZNRGTM", LocalTime.now().format(DateTimeFormatter.ofPattern("hhmmss")).toInteger());
      container.set("ZNCHNO", 0);
      container.set("ZNCHID", program.getUser());
      query.insert(container);
    }
    

    }else{
      mi.error("Warehouse "+iWHLO+" does not exist");
    }

  }
  
  public boolean checkWareshouse(String warehouse){
   boolean warehouseExist = false;
    def params = [ "WHLO":warehouse ] // toString is needed to convert from gstring to string
    String customer = null
    def callback = {
    Map<String, String> response ->
      if(response.WHLO != null){
       warehouseExist=true;
      }
      
    }
    
    miCaller.call("MMS005MI","GetWarehouse", params, callback)
    return warehouseExist;
  }

  /**
   *  Delete the previous day's record if found.
   **/
  Closure < ? > deleteRecord = {
    LockedResult lockedResult ->
    lockedResult.delete();
  }

  /**
   * Update the record if found for current date.
   **/
  Closure < ? > updateRecord = {
    LockedResult lockedResult ->

    lockedResult.set("ZNSEQN", lockedResult.get("ZNSEQN").toString().toInteger() + iSEQN);
    lockedResult.set("ZNLMDT", LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")).toInteger())
    lockedResult.set("ZNCHNO", lockedResult.get("ZNCHNO").toString().toInteger() + 1);
    lockedResult.set("ZNCHID", program.getUser());
    lockedResult.update();
  }
}
