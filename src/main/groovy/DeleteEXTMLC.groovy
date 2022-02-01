/**
 * Name:    EXT003MI_DeleteEXTMLC
 * Description:
 *          API to delete data from EXTMLC table
 *
 * Usage:
 *  Arguments
 * @CONO  Numeric,3       Company
 * @WHLO  Alphanumeric,3  Warehouse
 * @ITNO  Alphanumeric,15 Item Number
 * @WHSL  Alphanumeric,10 Location
 * @BANO  Alphanumeric,20 Lot number
 * @CAMU  Alphanumeric,20 Container
 * @REPN  Numeric,10      Receiving Number
 *
 * Created By: Birlasoft Ltd.
 * Authors:    Priyanka Nadgouda
 *
 * History:
 *20220201  base script created
 *
 */
public class DeleteEXTMLC extends ExtendM3Transaction {
  private final MIAPI mi;
  private final DatabaseAPI database;
  private final ProgramAPI program;
  private final MICallerAPI miCaller;

  public DeleteEXTMLC(MIAPI mi, ProgramAPI program, MICallerAPI miCaller, DatabaseAPI database) {
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

    DBAction query = database.table("EXTMLC").index("00").selectAllFields().build();
    DBContainer container = query.getContainer();
    container.set("EXCONO",iCONO);
    container.set("EXWHLO",iWHLO);
    container.set("EXITNO",iITNO);
    container.set("EXWHSL",iWHSL);
    container.set("EXBANO",iBANO);
    container.set("EXCAMU",iCAMU);
    container.set("EXREPN",iREPN);


    // Check if record exists
    if(!query.readLock(container,deleterCallback)){
      mi.error("Record does not exist in custom table");
    }}

  /**
   * Callback for delete operation
   **/
  Closure<?> deleterCallback = {
    LockedResult lockedResult -> lockedResult.delete()
  }
}

;
