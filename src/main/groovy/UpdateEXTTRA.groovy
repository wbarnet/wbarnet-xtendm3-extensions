/**
 * Name:    EXT004MI_UpdateEXTTRA
 * Description:
 *          API to update data in EXTTRA table
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
public class UpdateEXTTRA extends ExtendM3Transaction {
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

  public UpdateEXTTRA(MIAPI mi, DatabaseAPI database, ProgramAPI program, MICallerAPI miCaller) {
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

    // Check if record exists
    if(!query.readLock(container, updateCallback)){
      mi.error("Record does not exist in custom table");
    }

  }


  // Callback for update operation
  Closure<?> updateCallback = {
    LockedResult lockedResult ->

      if(mi.inData.get("NOTB")?.trim()){
        lockedResult.set("EXNOTB",iNOTB);
      }
      if(mi.inData.get("SPKG")?.trim()){
        lockedResult.set("EXSPKG",iSPKG);
      }
      //container.set("EXLMDT", LocalDate.now().format(DateTimeFormatter.ofPattern("YYYYMMdd")).toInteger());
      int changeNumber = (Integer) lockedResult.getInt("EXCHNO") + 1;
      lockedResult.set("EXCHNO", changeNumber);
      lockedResult.set("EXCHID", program.getUser());
      lockedResult.update();
  };


}
