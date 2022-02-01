/**
 * Name:    EXT004MI_DeleteEXTTRA
 * Description:
 *          API to delete data from EXTTRA table
 *
 * Usage:
 *  Arguments
 * @CONO  Numric,3        Company
 * @WHLO  Alphanumeric,3  Warehouse
 * @ITNO  Alphanumeric,15 Item Number
 * @RGDT  Numeric,8       Entry Date
 * @RGTM  Numeric,6       Entry time
 * @TMSX  Numeric,3       Time suffix
 *
 * Created By: Birlasoft Ltd.
 * Authors:    Aditya Bhatkhande
 *
 * History:
 *  20220201  base script created
 *
 */

public class DeleteEXTTRA extends ExtendM3Transaction {
  private final MIAPI mi;
  private final DatabaseAPI database;

  private int iCONO;
  private String iWHLO;
  private String iITNO;
  private int iRGDT;
  private int iRGTM;
  private int iTMSX;
  private double iNOTB;
  private String iSPKG;

  public DeleteEXTTRA(MIAPI mi, DatabaseAPI database) {
    this.mi = mi;
    this.database = database;
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
    if(!query.readLock(container,deleterCallback)){
      mi.error("Record does not exist in custom table");
    }

  }


  // Callback for delete operation
  Closure<?> deleterCallback = { LockedResult lockedResult ->
    lockedResult.delete();
  }

}
