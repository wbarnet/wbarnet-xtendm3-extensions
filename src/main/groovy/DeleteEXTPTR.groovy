/**
 * Name:    EXT005MI_DeleteEXTPTR
 * Description:
 *          API to delete data from EXTPTR table
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
 *
 * Created By: Birlasoft Ltd.
 * Authors:    Aditya Bhatkhande  
 *
 * History:
 *  20220201  base script created
 *
 */
public class DeleteEXTPTR extends ExtendM3Transaction {
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

  public DeleteEXTPTR(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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
    if(!query.readLock(container,deleterCallback)){
      mi.error("Record does not exist in custom table");
    }

  }

  //Callback for delete opeation
  Closure<?> deleterCallback = { LockedResult lockedResult ->
    lockedResult.delete();
  }
}
